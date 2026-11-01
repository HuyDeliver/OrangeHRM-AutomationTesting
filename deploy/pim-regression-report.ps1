$ErrorActionPreference = 'Stop'
$evidence = 'target/cd-evidence'
New-Item -ItemType Directory -Path $evidence -Force | Out-Null
$expected = @{
    'AddEmployeeFunctionality.addNewEmployee' = 1
    'EditJobDetailFunctionality.editEmployeeInfo' = 1
    'EditSalaryFunctionality.editEmployeeSalary' = 1
    'EditSupervisorFunctionality.editEmployeeSupervisor' = 1
    'SearchEmployeeFunctionality.searchEmployee' = 4
    'DeleteEmployeeFunctionality.deleteEmployee' = 1
    'TerminateEmployeeFunctionality.terminatEmployee' = 1
    'Issue1876RegressionTest.tabShouldApplyPastFilterLikeEnter' = 1
}
$summary = [ordered]@{
    environment = 'http://localhost:8081'
    total = 0; passed = 0; failed = 0; skipped = 0
    cleanup = 'NOT_VERIFIED'; result = 'NO_REPORT'; tests = @()
}
$path = 'target/cd-pim-regression/surefire/testng-results.xml'
if (Test-Path -LiteralPath $path) {
    [xml]$report = Get-Content -LiteralPath $path -Raw
    $methods = @($report.SelectNodes('//test-method[not(@is-config="true")]'))
    $badConfigurations = @($report.SelectNodes('//test-method[@is-config="true" and @status!="PASS"]'))
    $summary.total = $methods.Count
    $summary.passed = @($methods | Where-Object { $_.status -eq 'PASS' }).Count
    $summary.failed = @($methods | Where-Object { $_.status -eq 'FAIL' }).Count
    $summary.skipped = @($methods | Where-Object { $_.status -eq 'SKIP' }).Count
    $summary.tests = @($methods | ForEach-Object { [ordered]@{ name = $_.ParentNode.name + '.' + $_.name; status = $_.status } })
    $correctTests = $methods.Count -eq 11
    foreach ($key in $expected.Keys) {
        $matches = @($methods | Where-Object { ($_.ParentNode.name + '.' + $_.name) -eq "com.hrm.TestCase.PIMTab.$key" })
        if ($matches.Count -ne $expected[$key]) { $correctTests = $false }
    }
    $cleanup = @($report.SelectNodes("//class[@name='com.hrm.Setup.CDPIMSetup']/test-method[@is-config='true' and (@name='cleanupEmployees' or @name='cleanupRunUser') and @status='PASS']"))
    $terminateCleanup = @($report.SelectNodes("//class[@name='com.hrm.TestCase.PIMTab.TerminateEmployeeFunctionality']/test-method[@is-config='true' and @name='deleteEmployee' and @status='PASS']"))
    if ($cleanup.Count -eq 2 -and $terminateCleanup.Count -eq 1) { $summary.cleanup = 'PASS' }
    $summary.result = 'FAIL'
    if ($correctTests -and $summary.passed -eq 11 -and $badConfigurations.Count -eq 0 -and $summary.cleanup -eq 'PASS') {
        $summary.result = 'PASS'
    }
}
$summary | ConvertTo-Json -Depth 4 | Set-Content "$evidence/pim-regression-summary.json" -Encoding UTF8
Write-Output "PIM regression: $($summary.result); total=$($summary.total); passed=$($summary.passed); failed=$($summary.failed); skipped=$($summary.skipped); cleanup=$($summary.cleanup)"
if ($summary.result -ne 'PASS') { throw 'PIM requires all 11 invocations and cleanup to pass; inspect local reports' }
