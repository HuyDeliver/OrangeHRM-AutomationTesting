$ErrorActionPreference = 'Stop'
$evidence = 'target/cd-evidence'
New-Item -ItemType Directory -Path $evidence -Force | Out-Null
$expected = @{
    'Issue1890RegressionTest.validRowShouldKeepOneHourAfterSave' = 1
    'Issue1890RegressionTest.emptyRowShouldNotDiscardValidHours' = 1
    'Issue1787RegressionTest.attendanceCalendarShouldStartOnMonday' = 1
}
$summary = [ordered]@{
    environment = 'http://localhost:8081'
    total = 0; passed = 0; failed = 0; skipped = 0
    result = 'NO_REPORT'; tests = @()
}
$path = 'target/cd-time/surefire/testng-results.xml'
if (Test-Path -LiteralPath $path) {
    [xml]$report = Get-Content -LiteralPath $path -Raw
    $methods = @($report.SelectNodes('//test-method[not(@is-config="true")]'))
    $badConfigurations = @($report.SelectNodes('//test-method[@is-config="true" and @status!="PASS"]'))
    $summary.total = $methods.Count
    $summary.passed = @($methods | Where-Object { $_.status -eq 'PASS' }).Count
    $summary.failed = @($methods | Where-Object { $_.status -eq 'FAIL' }).Count
    $summary.skipped = @($methods | Where-Object { $_.status -eq 'SKIP' }).Count
    $summary.tests = @($methods | ForEach-Object { [ordered]@{ name = $_.ParentNode.name + '.' + $_.name; status = $_.status } })
    $correctTests = $methods.Count -eq 3
    foreach ($key in $expected.Keys) {
        $matches = @($methods | Where-Object { ($_.ParentNode.name + '.' + $_.name) -eq "com.hrm.TestCase.TimeTab.$key" })
        if ($matches.Count -ne $expected[$key]) { $correctTests = $false }
    }
    $summary.result = 'FAIL'
    if ($correctTests -and $summary.passed -eq 3 -and $badConfigurations.Count -eq 0) {
        $summary.result = 'PASS'
    }
}
$summary | ConvertTo-Json -Depth 4 | Set-Content "$evidence/time-summary.json" -Encoding UTF8
Write-Output "Time regression: $($summary.result); total=$($summary.total); passed=$($summary.passed); failed=$($summary.failed); skipped=$($summary.skipped)"
if ($summary.result -ne 'PASS') { throw 'Time regression requires all 3 invocations to pass; inspect local reports' }
