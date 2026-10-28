param(
    [string]$ConfigPath = (Join-Path $PSScriptRoot 'config.cd.properties'),
    [switch]$Initialize
)

$ErrorActionPreference = 'Stop'

# File local bị gitignore; GitHub Actions truyền password bằng Secret.
$localEnv = Join-Path $PSScriptRoot '.env.local'
if (-not $env:CD_DB_PASSWORD -and (Test-Path -LiteralPath $localEnv)) {
    $env:CD_DB_PASSWORD = ((Get-Content -LiteralPath $localEnv |
        Where-Object { $_ -match '^CD_DB_PASSWORD=' }) -split '=', 2)[1]
}
if ([string]::IsNullOrWhiteSpace($env:CD_DB_PASSWORD)) {
    throw 'Missing CD_DB_PASSWORD. See docs/docker-cd.md.'
}

$config = [System.IO.File]::ReadAllText((Resolve-Path -LiteralPath $ConfigPath))
if ($config -notmatch '(?m)^app\.url\s*=\s*http://localhost:8081/web/index\.php/auth/login\s*$') {
    throw 'CD config must target http://localhost:8081/web/index.php/auth/login'
}
if ($config -notmatch '(?m)^browser\s*=\s*chrome\s*$') {
    throw 'CD smoke requires browser=chrome'
}
foreach ($key in @('username', 'password')) {
    $match = [regex]::Match($config, "(?m)^$key\s*=\s*(.+)$")
    if (-not $match.Success) { throw "Missing $key in CD config" }
    if ($key -eq 'username') { $env:CD_ADMIN_USERNAME = $match.Groups[1].Value.Trim() }
    else { $env:CD_ADMIN_PASSWORD = $match.Groups[1].Value.Trim() }
}

$compose = @('compose', '-p', 'orangehrm-cd', '-f', (Join-Path $PSScriptRoot 'compose.cd.yml'))
Get-Command docker -ErrorAction Stop | Out-Null

try {
    # PowerShell 5.1 coi progress stderr của Docker là ErrorRecord khi redirect log.
    # Cho phép đọc hết output; từng lệnh Docker vẫn phải trả exit code 0 ở dưới.
    $ErrorActionPreference = 'Continue'
    Write-Output 'CD 1: Check Docker and pull pinned images'
    & docker info --format '{{.ServerVersion}}'
    if ($LASTEXITCODE -ne 0) { throw 'Docker Engine is not running' }
    & docker @compose pull
    if ($LASTEXITCODE -ne 0) { throw 'Image pull failed' }

    Write-Output 'CD 2: Start dedicated database; keep existing CD data'
    & docker @compose up -d --wait --wait-timeout 120 db
    if ($LASTEXITCODE -ne 0) { throw 'CD database is not ready' }

    Write-Output 'CD 3: Deploy web container from pinned image'
    # Image khai báo VOLUME /var/www/html: cần renew để không giữ mã cũ.
    # Named volumes cd-db và cd-config vẫn được giữ.
    & docker @compose up -d --no-deps --force-recreate --renew-anon-volumes web
    if ($LASTEXITCODE -ne 0) { throw 'CD web deployment failed' }

    Write-Output 'CD 4: Check installation (initialize only when explicitly requested)'
    $mode = 'check'
    if ($Initialize) { $mode = 'initialize' }
    & docker @compose exec -T -e CD_DB_PASSWORD -e CD_ADMIN_USERNAME -e CD_ADMIN_PASSWORD web php /opt/ci-install.php $mode
    if ($LASTEXITCODE -ne 0) { throw 'CD installation failed; check ci-deploy.log' }
    & docker @compose ps
    if ($LASTEXITCODE -ne 0) { throw 'Cannot read deployed container state' }
} finally {
    $ErrorActionPreference = 'Stop'
    Remove-Item Env:CD_ADMIN_USERNAME, Env:CD_ADMIN_PASSWORD -ErrorAction SilentlyContinue
}
