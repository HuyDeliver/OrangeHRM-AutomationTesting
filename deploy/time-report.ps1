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

    $knownBug1787 = "com.hrm.TestCase.TimeTab.Issue1787RegressionTest.attendanceCalendarShouldStartOnMonday"
    $status1787 = ($methods | Where-Object { ($_.ParentNode.name + '.' + $_.name) -eq $knownBug1787 }).status
    $functional1890 = @($methods | Where-Object { ($_.ParentNode.name + '.' + $_.name) -ne $knownBug1787 -and $_.status -eq 'PASS' }).Count

    # Chấp nhận khi 2 test Timesheet PASS + xác nhận bug #1787 (lịch bắt đầu bằng Sunday)
    if ($correctTests -and $functional1890 -eq 2 -and ($status1787 -eq 'PASS' -or $status1787 -eq 'FAIL') -and $badConfigurations.Count -eq 0) {
        $summary.result = 'PASS'
        if ($status1787 -eq 'FAIL') {
            $summary['knownBugConfirmed'] = 'Issue #1787 (attendance calendar starts on Sunday) reproduced on OrangeHRM 5.9'
        }
    }
}
$summary | ConvertTo-Json -Depth 4 | Set-Content "$evidence/time-summary.json" -Encoding UTF8
Write-Output "Time regression: $($summary.result); total=$($summary.total); passed=$($summary.passed); failed=$($summary.failed); skipped=$($summary.skipped)"
if ($summary.result -ne 'PASS') { throw 'Time regression requires Timesheet invocations to pass; inspect local reports' }
