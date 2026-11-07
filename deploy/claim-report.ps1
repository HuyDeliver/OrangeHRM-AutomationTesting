$ErrorActionPreference = 'Stop'
$evidence = 'target/cd-evidence'
New-Item -ItemType Directory -Path $evidence -Force | Out-Null
$summary = [ordered]@{
    environment = 'http://localhost:8081'
    total = 0; passed = 0; failed = 0; skipped = 0
    result = 'NO_REPORT'; tests = @()
}
$path = 'target/cd-claim/surefire/testng-results.xml'
if (Test-Path -LiteralPath $path) {
    [xml]$report = Get-Content -LiteralPath $path -Raw
    $methods = @($report.SelectNodes('//test-method[not(@is-config="true")]'))
    $badConfigurations = @($report.SelectNodes('//test-method[@is-config="true" and @status!="PASS"]'))
    $summary.total = $methods.Count
    $summary.passed = @($methods | Where-Object { $_.status -eq 'PASS' }).Count
    $summary.failed = @($methods | Where-Object { $_.status -eq 'FAIL' }).Count
    $summary.skipped = @($methods | Where-Object { $_.status -eq 'SKIP' }).Count
    $summary.tests = @($methods | ForEach-Object { [ordered]@{ name = $_.ParentNode.name + '.' + $_.name; status = $_.status } })
    $onlyClaim = $methods.Count -eq 1 -and ($methods[0].ParentNode.name -eq 'com.hrm.TestCase.ClaimTab.Issue1820RegressionTest')
    $summary.result = 'FAIL'
    if ($onlyClaim -and $summary.passed -eq 1 -and $badConfigurations.Count -eq 0) {
        $summary.result = 'PASS'
    }
}
$summary | ConvertTo-Json -Depth 4 | Set-Content "$evidence/claim-summary.json" -Encoding UTF8
Write-Output "Claim regression: $($summary.result); total=$($summary.total); passed=$($summary.passed); failed=$($summary.failed); skipped=$($summary.skipped)"
if ($summary.result -ne 'PASS') { throw 'Claim regression requires Issue1820RegressionTest to pass; inspect local reports' }
