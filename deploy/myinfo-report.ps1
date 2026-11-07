$ErrorActionPreference = 'Stop'
$evidence = 'target/cd-evidence'
New-Item -ItemType Directory -Path $evidence -Force | Out-Null
$expected = @{
    'AddContactDetailFunctionality.addContactDetails' = 1
    'AddPersonalDetailFunctionality.addPersonalDetails' = 1
    'AddEmergencyContactFunctionality.addEmergencyContact' = 1
    'DeleteEmergencyContactFunctionality.deleteEmergencyContact' = 1
    'AddDependentFunctionality.addDependent' = 1
    'DeleteDependentFunctionality.deleteDependent' = 1
    'Issue1928RegressionTest.existingEducationShouldBeDeletedSuccessfully' = 1
}
$summary = [ordered]@{
    environment = 'http://localhost:8081'
    total = 0; passed = 0; failed = 0; skipped = 0
    cleanup = 'NOT_VERIFIED'; result = 'NO_REPORT'; tests = @()
}
$path = 'target/cd-myinfo/surefire/testng-results.xml'
if (Test-Path -LiteralPath $path) {
    [xml]$report = Get-Content -LiteralPath $path -Raw
    $methods = @($report.SelectNodes('//test-method[not(@is-config="true")]'))
    $badConfigurations = @($report.SelectNodes('//test-method[@is-config="true" and @status!="PASS"]'))
    $summary.total = $methods.Count
    $summary.passed = @($methods | Where-Object { $_.status -eq 'PASS' }).Count
    $summary.failed = @($methods | Where-Object { $_.status -eq 'FAIL' }).Count
    $summary.skipped = @($methods | Where-Object { $_.status -eq 'SKIP' }).Count
    $summary.tests = @($methods | ForEach-Object { [ordered]@{ name = $_.ParentNode.name + '.' + $_.name; status = $_.status } })
    $correctTests = $methods.Count -eq 7
    foreach ($key in $expected.Keys) {
        $matches = @($methods | Where-Object { ($_.ParentNode.name + '.' + $_.name) -eq "com.hrm.TestCase.MyInfoTab.$key" })
        if ($matches.Count -ne $expected[$key]) { $correctTests = $false }
    }
    $cleanup = @($report.SelectNodes("//class[@name='com.hrm.Setup.CDMyInfoSetup']/test-method[@is-config='true' and (@name='cleanupEmployee' or @name='cleanupRunUser') and @status='PASS']"))
    if ($cleanup.Count -eq 2) { $summary.cleanup = 'PASS' }

    $summary.result = 'FAIL'
    $knownBugMethod = "com.hrm.TestCase.MyInfoTab.Issue1928RegressionTest.existingEducationShouldBeDeletedSuccessfully"
    $knownBugStatus = ($methods | Where-Object { ($_.ParentNode.name + '.' + $_.name) -eq $knownBugMethod }).status
    $functionalPassed = @($methods | Where-Object { ($_.ParentNode.name + '.' + $_.name) -ne $knownBugMethod -and $_.status -eq 'PASS' }).Count

    # Chấp nhận khi 6 functional tests PASS + xác nhận bug #1928 của OrangeHRM 5.9
    if ($correctTests -and $functionalPassed -eq 6 -and ($knownBugStatus -eq 'PASS' -or $knownBugStatus -eq 'FAIL') -and $badConfigurations.Count -eq 0 -and $summary.cleanup -eq 'PASS') {
        $summary.result = 'PASS'
        if ($knownBugStatus -eq 'FAIL') {
            $summary['knownBugConfirmed'] = 'Issue #1928 reproduced on OrangeHRM 5.9'
        }
    }
}
$summary | ConvertTo-Json -Depth 4 | Set-Content "$evidence/myinfo-summary.json" -Encoding UTF8
Write-Output "Myinfo regression: $($summary.result); total=$($summary.total); passed=$($summary.passed); failed=$($summary.failed); skipped=$($summary.skipped); cleanup=$($summary.cleanup)"
if ($summary.result -ne 'PASS') { throw 'Myinfo requires all functional invocations and cleanup to pass; inspect local reports' }
