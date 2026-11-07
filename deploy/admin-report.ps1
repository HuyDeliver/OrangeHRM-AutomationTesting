$ErrorActionPreference = 'Stop'
$evidence = 'target/cd-evidence'
New-Item -ItemType Directory -Path $evidence -Force | Out-Null
# Số lượt của từng method: Search User có 2 dòng Excel, Search Location có 3.
$expected = @{
    'AddUserFunctionality.addUserFunctionality' = 1
    'SearchUserWithValidData.searchUserUsingDataDriven' = 1
    'SearchUserWithInvalidData.searchUserUsingDataDriven' = 2
    'EditUserFunctionality.editUserWithNewPass' = 1
    'DeleteUserFunctionality.deleteUserSuccess' = 1
    'AddJobTitlesFunctionality.addJobTitleSuccess' = 1
    'EditJobTitleFunctionality.editJobTileSuccess' = 1
    'DeleteJobTitlesFunctionality.deleteJob' = 1
    'AddLocationFunctionality.addLocationSuccess' = 1
    'SearchLocationFunctionality.searchLocationWithCity' = 3
    'EditLocationFunctionality.editCityinLocation' = 1
    'DeleteLocationFunctionality.deleteLocationSuccess' = 1
    'AddSkillFunctionality.addSkillSuccess' = 1
    'EditSkillFunctionality.editSkillSuccess' = 1
    'DeleteSkillFunctionality.editSkillSuccess' = 1
    'Issue1897RegressionTest.updatingOnePayGradeShouldNotChangeAnotherPayGrade' = 1
}
$summary = [ordered]@{
    environment = 'http://localhost:8081'
    total = 0; passed = 0; failed = 0; skipped = 0
    cleanup = 'NOT_VERIFIED'
    result = 'NO_REPORT'
    tests = @()
}
$reportPath = 'target/cd-admin/surefire/testng-results.xml'
if (Test-Path -LiteralPath $reportPath) {
    [xml]$report = Get-Content -LiteralPath $reportPath -Raw
    $methods = @($report.SelectNodes('//test-method[not(@is-config="true")]'))
    $badConfigurations = @($report.SelectNodes('//test-method[@is-config="true" and @status!="PASS"]'))
    $summary.total = $methods.Count
    $summary.passed = @($methods | Where-Object { $_.status -eq 'PASS' }).Count
    $summary.failed = @($methods | Where-Object { $_.status -eq 'FAIL' }).Count
    $summary.skipped = @($methods | Where-Object { $_.status -eq 'SKIP' }).Count
    $summary.tests = @($methods | ForEach-Object {
        [ordered]@{ name = $_.ParentNode.name + '.' + $_.name; status = $_.status }
    })
    $correctTests = $methods.Count -eq 19
    foreach ($key in $expected.Keys) {
        $matches = @($methods | Where-Object { ($_.ParentNode.name + '.' + $_.name) -eq "com.hrm.TestCase.AdminTab.$key" })
        if ($matches.Count -ne $expected[$key]) { $correctTests = $false }
    }
    $cleanupNames = @('cleanupJobTitle', 'cleanupLocation', 'cleanupSkill', 'cleanupRunUser')
    $cleanupOK = $true
    foreach ($name in $cleanupNames) {
        $matches = @($report.SelectNodes("//class[@name='com.hrm.Setup.CDAdminSetup']/test-method[@is-config='true' and @name='$name' and @status='PASS']"))
        if ($matches.Count -ne 1) { $cleanupOK = $false }
    }
    $payGradeCleanup = @($report.SelectNodes("//class[@name='com.hrm.TestCase.AdminTab.Issue1897RegressionTest']/test-method[@is-config='true' and @name='cleanupTestPayGrades' and @status='PASS']"))
    if ($cleanupOK -and $payGradeCleanup.Count -eq 1) { $summary.cleanup = 'PASS' }

    $summary.result = 'FAIL'
    $knownBugMethod = "com.hrm.TestCase.AdminTab.Issue1897RegressionTest.updatingOnePayGradeShouldNotChangeAnotherPayGrade"
    $knownBugStatus = ($methods | Where-Object { ($_.ParentNode.name + '.' + $_.name) -eq $knownBugMethod }).status
    $functionalPassed = @($methods | Where-Object { ($_.ParentNode.name + '.' + $_.name) -ne $knownBugMethod -and $_.status -eq 'PASS' }).Count

    # Chấp nhận khi 18 functional tests PASS + xác nhận bug #1897 của OrangeHRM 5.9
    if ($correctTests -and $functionalPassed -eq 18 -and ($knownBugStatus -eq 'PASS' -or $knownBugStatus -eq 'FAIL') -and $badConfigurations.Count -eq 0 -and $summary.cleanup -eq 'PASS') {
        $summary.result = 'PASS'
        if ($knownBugStatus -eq 'FAIL') {
            $summary['knownBugConfirmed'] = 'Issue #1897 reproduced on OrangeHRM 5.9'
        }
    }
}
$summary | ConvertTo-Json -Depth 4 | Set-Content "$evidence/admin-summary.json" -Encoding UTF8
Write-Output "Admin CD: $($summary.result); total=$($summary.total); passed=$($summary.passed); failed=$($summary.failed); skipped=$($summary.skipped); cleanup=$($summary.cleanup)"
if ($summary.result -ne 'PASS') { throw 'Admin requires all functional invocations and cleanup to pass; inspect local reports' }
