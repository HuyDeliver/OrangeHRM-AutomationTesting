param(
    [string]$ConfigPath = (Join-Path $PSScriptRoot 'config.cd.properties'),
    [ValidatePattern('^[a-zA-Z0-9_-]+$')]
    [string]$RunName = (Get-Date -Format 'yyyyMMdd-HHmmss-fff')
)
$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = New-Object System.Text.UTF8Encoding
$OutputEncoding = [Console]::OutputEncoding
$configFile = (Resolve-Path -LiteralPath $ConfigPath).Path
$config = ConvertFrom-StringData ([System.IO.File]::ReadAllText($configFile))
if ($config['app.url'] -ne 'http://localhost:8081/web/index.php/auth/login') {
    throw 'STOP: seed only supports CD 8081; no browser or database write was started.'
}

# Kiểm tra container thực sự phục vụ 8081 và Conf.php trỏ database CD.
$web = & docker inspect orangehrm-cd-web-1
if ($LASTEXITCODE -ne 0) { throw 'Cannot inspect CD web container' }
$container = ($web | ConvertFrom-Json)[0]
if ($container.Config.Labels.'com.docker.compose.project' -ne 'orangehrm-cd' -or
    $container.NetworkSettings.Ports.'80/tcp'[0].HostPort -ne '8081') {
    throw 'STOP: wrong CD project or port'
}
$check = 'require "/var/www/html/lib/confs/Conf.php"; $c = new Conf(); if ($c->getDbHost() !== "db" || $c->getDbName() !== "orangehrm_cd") { exit(1); } echo "CD database verified";'
# Truyền qua stdin để Windows PowerShell 5.1 không làm mất dấu quote của PHP.
('<?php ' + $check + ' echo PHP_EOL;') | & docker exec -i orangehrm-cd-web-1 php
if ($LASTEXITCODE -ne 0) { throw 'STOP: cannot verify orangehrm_cd database' }

Push-Location (Split-Path $PSScriptRoot -Parent)
try {
    $output = Join-Path (Get-Location) "target/cd-seed/$RunName"
    if (Test-Path -LiteralPath $output) { throw 'Choose a new RunName to preserve previous seed evidence' }
    New-Item -ItemType Directory -Path $output | Out-Null
    Write-Output "[SEED] Preparing data on CD 8081. Local log: $output/seed.log"
    # Windows PowerShell 5.1: native stderr progress không phải lỗi nếu exit code=0.
    $ErrorActionPreference = 'Continue'
    & mvn -B -ntp test -Pcd-seed "-Dconfig.file=$configFile" '-Dheadless=true' `
        '-Dcd.seed.verified=true' "-Dseed.output=$output" *> "$output/seed.log"
    $code = $LASTEXITCODE
    $ErrorActionPreference = 'Stop'
    if ($null -eq $code -or $code -ne 0) { throw "Seed failed; see $output/seed.log" }
    $result = Get-Content -LiteralPath "$output/seed-result.properties" -Raw | ConvertFrom-StringData
    if ($result['result'] -ne 'SUCCESS') { throw 'Seed did not produce successful preparation evidence' }
    Write-Output "[SEED] $($result['fixture.key']) $($result['action']) | empNumber=$($result['employee.empNumber']) | matches=$($result['match.count'])"
    Write-Output '[SEED] Setup completed. Functional/security/regression testcase count added: 0.'
} finally {
    $ErrorActionPreference = 'Stop'
    Pop-Location
}
