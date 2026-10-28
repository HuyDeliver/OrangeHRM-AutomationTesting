$ErrorActionPreference = 'Stop'
$deadline = (Get-Date).AddSeconds(120)

while ((Get-Date) -lt $deadline) {
    try {
        $response = Invoke-WebRequest -UseBasicParsing `
            -Uri 'http://localhost:8081/web/index.php/auth/login' -TimeoutSec 5 -MaximumRedirection 0
        if ($response.StatusCode -eq 200 -and $response.Content -match '<auth-login\b') {
            Write-Output 'READY: OrangeHRM Login at localhost:8081'
            return
        }
    } catch { }
    Start-Sleep -Seconds 2
}

throw 'Readiness failed: installed Login page not available within 120 seconds (plus final request timeout)'
