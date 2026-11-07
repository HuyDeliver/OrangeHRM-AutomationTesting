param(
    [Parameter(Mandatory)][ValidateSet('api', 'security')][string]$Name,
    [Parameter(Mandatory)][int]$Expected
)
$ErrorActionPreference = 'Stop'
$evidence = 'target/cd-evidence'
New-Item -ItemType Directory -Path $evidence -Force | Out-Null
$summary = [ordered]@{
    environment = 'http://localhost:8081'
    total = 0; passed = 0; failed = 0; skipped = 0
    result = 'NO_REPORT'; tests = @()
}
$path = "target/cd-$Name/surefire/testng-results.xml"
if (Test-Path -LiteralPath $path) {
    [xml]$report = Get-Content -LiteralPath $path -Raw
    $methods = @($report.SelectNodes('//test-method[not(@is-config="true")]'))
    $badConfigurations = @($report.SelectNodes('//test-method[@is-config="true" and @status!="PASS"]'))
    $summary.total = $methods.Count
    $summary.passed = @($methods | Where-Object { $_.status -eq 'PASS' }).Count
    $summary.failed = @($methods | Where-Object { $_.status -eq 'FAIL' }).Count
    $summary.skipped = @($methods | Where-Object { $_.status -eq 'SKIP' }).Count
    $summary.tests = @($methods | ForEach-Object { [ordered]@{ name = $_.ParentNode.name + '.' + $_.name; status = $_.status } })
    $summary.result = 'FAIL'
    if ($summary.total -eq $Expected -and $summary.passed -eq $Expected -and $badConfigurations.Count -eq 0) {
        $summary.result = 'PASS'
    }
}
$summary | ConvertTo-Json -Depth 4 | Set-Content "$evidence/$Name-summary.json" -Encoding UTF8
Write-Output "CD $Name tests: $($summary.result); total=$($summary.total); passed=$($summary.passed); failed=$($summary.failed); skipped=$($summary.skipped)"
if ($summary.result -ne 'PASS') { throw "CD $Name requires all $Expected invocations to pass; inspect local reports" }
