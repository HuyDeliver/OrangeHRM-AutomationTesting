$ErrorActionPreference = 'Stop'
$evidence = 'target/cd-evidence'
New-Item -ItemType Directory -Path $evidence -Force | Out-Null
$expected = @{
    'AddVacancyFunctionality.addVacancy' = 1
    'SearchVacancyFunctionality.searchVacancy' = 4
    'DeleteVacancyFunctionality.deleteVancancies' = 1
    'AddCandidateFunctionality.addCandidate' = 1
    'SearchCandidateFunctionality.searchExistingCandidateByName' = 1
    'SearchCandidateFunctionality.searchMissingCandidateShouldReturnNoRecords' = 1
    'RejectCandidateFunctionality.rejectCandidate' = 1
    'DeleteCandidateFunctionality.deleteVancancies' = 1
    'Issue1926RegressionTest.shortlistingCandidateShouldUpdateStatusAndHistory' = 3
}
$summary = [ordered]@{
    environment = 'http://localhost:8081'
    total = 0; passed = 0; failed = 0; skipped = 0
    cleanup = 'NOT_VERIFIED'; result = 'NO_REPORT'; tests = @()
}
$path = 'target/cd-recruitment/surefire/testng-results.xml'
if (Test-Path -LiteralPath $path) {
    [xml]$report = Get-Content -LiteralPath $path -Raw
    $methods = @($report.SelectNodes('//test-method[not(@is-config="true")]'))
    $badConfigurations = @($report.SelectNodes('//test-method[@is-config="true" and @status!="PASS"]'))
    $summary.total = $methods.Count
    $summary.passed = @($methods | Where-Object { $_.status -eq 'PASS' }).Count
    $summary.failed = @($methods | Where-Object { $_.status -eq 'FAIL' }).Count
    $summary.skipped = @($methods | Where-Object { $_.status -eq 'SKIP' }).Count
    $summary.tests = @($methods | ForEach-Object { [ordered]@{ name = $_.ParentNode.name + '.' + $_.name; status = $_.status } })
    $correctTests = $methods.Count -eq 14
    foreach ($key in $expected.Keys) {
        $matches = @($methods | Where-Object { ($_.ParentNode.name + '.' + $_.name) -eq "com.hrm.TestCase.Recruitment.$key" })
        if ($matches.Count -ne $expected[$key]) { $correctTests = $false }
    }
    $cleanup = @($report.SelectNodes("//class[@name='com.hrm.Setup.CDRecruitmentSetup']/test-method[@is-config='true' and (@name='cleanupCandidates' or @name='cleanupVacancy') and @status='PASS']"))
    $candidateCleanup = @($report.SelectNodes("//class[@name='com.hrm.TestCase.Recruitment.AddCandidateFunctionality']/test-method[@is-config='true' and @name='deleteTestCandidate' and @status='PASS']"))
    $rejectCleanup = @($report.SelectNodes("//class[@name='com.hrm.TestCase.Recruitment.RejectCandidateFunctionality']/test-method[@is-config='true' and @name='deleteCandidate' and @status='PASS']"))
    $issueCleanup = @($report.SelectNodes("//class[@name='com.hrm.TestCase.Recruitment.Issue1926RegressionTest']/test-method[@is-config='true' and @name='cleanupTestCandidate' and @status='PASS']"))
    $searchCleanup = @($report.SelectNodes("//class[@name='com.hrm.TestCase.Recruitment.SearchCandidateFunctionality']/test-method[@is-config='true' and @name='cleanupSearchCandidate' and @status='PASS']"))
    if ($cleanup.Count -eq 2 -and $candidateCleanup.Count -eq 1 -and $searchCleanup.Count -eq 2 -and $rejectCleanup.Count -eq 1 -and $issueCleanup.Count -eq 3) {
        $summary.cleanup = 'PASS'
    }
    $summary.result = 'FAIL'
    if ($correctTests -and $summary.passed -eq 14 -and $badConfigurations.Count -eq 0 -and $summary.cleanup -eq 'PASS') {
        $summary.result = 'PASS'
    }
}
$summary | ConvertTo-Json -Depth 4 | Set-Content "$evidence/recruitment-summary.json" -Encoding UTF8
Write-Output "Recruitment regression: $($summary.result); total=$($summary.total); passed=$($summary.passed); failed=$($summary.failed); skipped=$($summary.skipped); cleanup=$($summary.cleanup)"
if ($summary.result -ne 'PASS') { throw 'Recruitment requires all 14 invocations and cleanup to pass; inspect local reports' }
