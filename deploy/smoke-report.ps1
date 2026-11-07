$ErrorActionPreference = 'Stop'
$evidence = 'target/cd-evidence'
New-Item -ItemType Directory -Path $evidence -Force | Out-Null
$reportPath = 'target/surefire-reports/testng-results.xml'
$summary = [ordered]@{
    test = 'LoginTest.loginSuccess'
    environment = 'http://localhost:8081'
    total = 0
    passed = 0
    failed = 0
    skipped = 0
    excluded = 0
    result = 'NO_REPORT'
}

if (Test-Path -LiteralPath $reportPath) {
    [xml]$report = Get-Content -LiteralPath $reportPath -Raw
    $result = $report.'testng-results'
    # TestNG total gồm cả ignored methods; đếm method thực sự chạy để không nhầm.
    $methods = @($report.SelectNodes('//test-method[not(@is-config="true")]'))
    $summary.total = $methods.Count
    $summary.passed = [int]$result.passed
    $summary.failed = [int]$result.failed
    $summary.skipped = [int]$result.skipped
    $summary.excluded = [int]$result.ignored
    $badConfigurations = @($report.SelectNodes('//test-method[@is-config="true" and @status!="PASS"]'))
    $correctTest = $methods.Count -eq 1 -and $methods[0].name -eq 'loginSuccess' `
        -and $methods[0].ParentNode.name -eq 'com.hrm.TestCase.LoginTab.LoginTest'
    
    if ($methods.Count -eq 0) {
        $summary.result = 'NO_REPORT'
    } elseif ($correctTest -and $methods[0].status -eq 'PASS' -and $summary.total -eq 1 -and $summary.passed -eq 1 `
        -and $summary.failed -eq 0 -and $summary.skipped -eq 0 -and $badConfigurations.Count -eq 0) {
        $summary.result = 'PASS'
    } elseif ($correctTest -and ($methods[0].status -eq 'SKIP' -or $summary.skipped -gt 0) `
        -and $summary.failed -eq 0 -and $badConfigurations.Count -eq 0) {
        $summary.result = 'SKIP'
    } else {
        $summary.result = 'FAIL'
    }
}

# Chỉ upload số đếm và kết quả thật; không copy stdout, stack trace, config hay token.
$summary | ConvertTo-Json | Set-Content -LiteralPath "$evidence/smoke-summary.json" -Encoding UTF8
Write-Output "Login smoke: $($summary.result); total=$($summary.total); passed=$($summary.passed); failed=$($summary.failed); skipped=$($summary.skipped)"
if ($summary.result -eq 'NO_REPORT') {
    throw 'Smoke failed: NO_REPORT (test did not run or total=0)'
} elseif ($summary.result -eq 'SKIP') {
    throw 'Smoke failed: test was SKIPPED'
} elseif ($summary.result -ne 'PASS') {
    throw 'Smoke must run exactly OHR1 and pass without skips'
}
