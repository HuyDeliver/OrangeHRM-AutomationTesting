param(
    [switch]$IncludePIM,
    [switch]$IncludeRecruitment,
    [switch]$IncludeMyInfo,
    [switch]$IncludePerformance,
    [switch]$IncludeApi,
    [string]$ConfigPath = (Join-Path $PSScriptRoot 'config.cd.properties'),
    [ValidatePattern('^[a-zA-Z0-9_-]+$')]
    [string]$RunName = (Get-Date -Format 'yyyyMMdd-HHmmss-fff')
)
$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = New-Object System.Text.UTF8Encoding
$OutputEncoding = [Console]::OutputEncoding
$configFile = (Resolve-Path -LiteralPath $ConfigPath).Path
$config = @{}
foreach ($line in [System.IO.File]::ReadAllLines($configFile)) {
    if ($line -match '^\s*([^#=\s]+)\s*=\s*(.*)$') {
        $config[$matches[1]] = $matches[2].Trim()
    }
}
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

# API/Security: đăng ký OAuth client công khai (PKCE, không secret) trong DB CD; chạy lại không tạo trùng.
if ($IncludeApi) {
    if (-not $config['oauth.client.id']) {
        $config['oauth.client.id'] = 'orangehrm_cd_automation'
        [System.IO.File]::AppendAllText($configFile, "`noauth.client.id=orangehrm_cd_automation`n")
    }
    if (-not $config['ess.password']) {
        $config['ess.password'] = $config['password']
        [System.IO.File]::AppendAllText($configFile, "`ness.password=$($config['password'])`n")
    }
    if (-not $config['oauth.client.id'] -or -not $config['ess.password']) {
        throw 'STOP: thiếu oauth.client.id hoặc ess.password trong config CD'
    }
    $oauth = 'require "/var/www/html/lib/confs/Conf.php"; $c = new Conf(); $p = new PDO("mysql:host=" . $c->getDbHost() . ";dbname=" . $c->getDbName(), $c->getDbUser(), $c->getDbPass()); $s = $p->prepare("INSERT IGNORE INTO ohrm_oauth2_client (name, client_id, client_secret, redirect_uri, is_confidential, enabled) VALUES (?, ?, NULL, ?, 0, 1)"); $s->execute(["CD Automation", getenv("OAUTH_ID"), "http://localhost:8081/oauth-callback"]); echo "OAuth client ready";'
    ('<?php ' + $oauth + ' echo PHP_EOL;') | & docker exec -i -e "OAUTH_ID=$($config['oauth.client.id'])" orangehrm-cd-web-1 php
    if ($LASTEXITCODE -ne 0) { throw 'STOP: cannot register OAuth client on CD' }
}

Push-Location (Split-Path $PSScriptRoot -Parent)
try {
    $output = Join-Path (Get-Location) "target/cd-seed/$RunName"
    if (Test-Path -LiteralPath $output) { throw 'Choose a new RunName to preserve previous seed evidence' }
    New-Item -ItemType Directory -Path $output | Out-Null
    Write-Output "[SEED] Preparing data on CD 8081. Local log: $output/seed.log"
    # Windows PowerShell 5.1: native stderr progress không phải lỗi nếu exit code=0.
    $ErrorActionPreference = 'Continue'
    $seedTest = 'com.hrm.Setup.CDSeedTest'
    if ($IncludePIM) { $seedTest = 'com.hrm.Setup.CDPIMSeedTest' }
    if ($IncludeRecruitment) { $seedTest = 'com.hrm.Setup.CDRecruitmentSeedTest' }
    if ($IncludeMyInfo) { $seedTest = 'com.hrm.Setup.CDMyInfoSeedTest' }
    if ($IncludePerformance) { $seedTest = 'com.hrm.Setup.CDPerformanceSeedTest' }
    if ($IncludeApi) { $seedTest = 'com.hrm.Setup.CDApiSeedTest' }
    & mvn -B -ntp test -Pcd-seed "-Dseed.test=$seedTest" "-Dconfig.file=$configFile" '-Dheadless=true' `
        '-Dcd.seed.verified=true' "-Dseed.output=$output" *> "$output/seed.log"
    $code = $LASTEXITCODE
    $ErrorActionPreference = 'Stop'
    if ($null -eq $code -or $code -ne 0) { throw "Seed failed; see $output/seed.log" }
    $result = Get-Content -LiteralPath "$output/seed-result.properties" -Raw | ConvertFrom-StringData
    if ($result['result'] -ne 'SUCCESS') { throw 'Seed did not produce successful preparation evidence' }
    if ($IncludePIM -or $IncludeRecruitment -or $IncludeMyInfo -or $IncludePerformance -or $IncludeApi) {
        $pim = Get-Content -LiteralPath "$output/pim-seed-result.properties" -Raw | ConvertFrom-StringData
        if ($pim['result'] -ne 'SUCCESS') { throw 'PIM seed did not complete' }
        Write-Output '[SEED] PIM catalogs verified; setup only, not functional PASS.'
    }
    if ($IncludeRecruitment -or $IncludeMyInfo -or $IncludePerformance -or $IncludeApi) {
        $recruitment = Get-Content -LiteralPath "$output/recruitment-seed-result.properties" -Raw | ConvertFrom-StringData
        if ($recruitment['result'] -ne 'SUCCESS') { throw 'Recruitment seed did not complete' }
        Write-Output '[SEED] Recruitment vacancies verified; setup only, not functional PASS.'
    }
    if ($IncludeMyInfo -or $IncludePerformance -or $IncludeApi) {
        $myinfo = Get-Content -LiteralPath "$output/myinfo-seed-result.properties" -Raw | ConvertFrom-StringData
        if ($myinfo['result'] -ne 'SUCCESS') { throw 'MyInfo seed did not complete' }
        Write-Output '[SEED] MyInfo education verified; setup only.'
    }
    if ($IncludePerformance -or $IncludeApi) {
        $performance = Get-Content -LiteralPath "$output/performance-seed-result.properties" -Raw | ConvertFrom-StringData
        if ($performance['result'] -ne 'SUCCESS') { throw 'Performance seed did not complete' }
        Write-Output '[SEED] Performance KPIs and subordinate verified; setup only.'
    }
    if ($IncludeApi) {
        $api = Get-Content -LiteralPath "$output/api-seed-result.properties" -Raw | ConvertFrom-StringData
        if ($api['result'] -ne 'SUCCESS') { throw 'API seed did not complete' }
        [System.IO.File]::AppendAllText($configFile, "`ness.empNumber=$($api['ess.empNumber'])`ntarget.empNumber=$($result['employee.empNumber'])`n")
        Write-Output '[SEED] ESS employee/user verified; setup only.'
    }
    Write-Output "[SEED] $($result['fixture.key']) $($result['action']) | empNumber=$($result['employee.empNumber']) | matches=$($result['match.count'])"
    Write-Output '[SEED] Setup completed. Functional/security/regression testcase count added: 0.'
} finally {
    $ErrorActionPreference = 'Stop'
    Pop-Location
}
