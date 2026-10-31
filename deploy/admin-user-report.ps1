$ErrorActionPreference = 'Stop'
$evidence = 'target/cd-evidence'
New-Item -ItemType Directory -Path $evidence -Force | Out-Null
$reportPath = 'target/cd-admin-user/surefire/testng-results.xml'
$expected = @(
    'com.hrm.TestCase.AdminTab.AddUserFunctionality.addUserFunctionality',
    'com.hrm.TestCase.AdminTab.SearchUserWithValidData.searchUserUsingDataDriven',
    'com.hrm.TestCase.AdminTab.EditUserFunctionality.editUserWithNewPass',
    'com.hrm.TestCase.AdminTab.DeleteUserFunctionality.deleteUserSuccess'
)
$summary = [ordered]@{
    environment = 'http://localhost:8081'
    total = 0; passed = 0; failed = 0; skipped = 0
    cleanup = 'NOT_VERIFIED'
    result = 'NO_REPORT'
    tests = @()
}
if (Test-Path -LiteralPath $reportPath) {
    [xml]$report = Get-Content -LiteralPath $reportPath -Raw
    $methods = @($report.SelectNodes('//test-method[not(@is-config="true")]'))
    $badConfigurations = @($report.SelectNodes('//test-method[@is-config="true" and @status!="PASS"]'))
    $cleanup = @($report.SelectNodes('//class[@name="com.hrm.Setup.CDAdminUserSetup"]/test-method[@is-config="true" and @name="cleanupRunUser" and @status="PASS"]'))
    if ($cleanup.Count -eq 1) { $summary.cleanup = 'PASS' }
    $summary.total = $methods.Count
    $summary.passed = @($methods | Where-Object { $_.status -eq 'PASS' }).Count
    $summary.failed = @($methods | Where-Object { $_.status -eq 'FAIL' }).Count
    $summary.skipped = @($methods | Where-Object { $_.status -eq 'SKIP' }).Count
    $summary.tests = @($methods | ForEach-Object { [ordered]@{ name = $_.name; status = $_.status } })
    $actual = @($methods | ForEach-Object { $_.ParentNode.name + '.' + $_.name })
    $summary.result = 'FAIL'
    if ($methods.Count -eq 4 -and !(Compare-Object $expected $actual) -and $summary.passed -eq 4 `
        -and $badConfigurations.Count -eq 0 -and $summary.cleanup -eq 'PASS') {
        $summary.result = 'PASS'
    }
}
$summary | ConvertTo-Json -Depth 4 | Set-Content "$evidence/admin-user-summary.json" -Encoding UTF8
Write-Output "Admin User CD: $($summary.result); total=$($summary.total); passed=$($summary.passed); failed=$($summary.failed); skipped=$($summary.skipped); cleanup=$($summary.cleanup)"
if ($summary.result -ne 'PASS') { throw 'All four Admin User tests and cleanup must pass without skips' }
