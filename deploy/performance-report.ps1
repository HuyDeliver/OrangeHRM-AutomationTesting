$ErrorActionPreference = 'Stop'
$evidence = 'target/cd-evidence'
New-Item -ItemType Directory -Path $evidence -Force | Out-Null
$expected = @{
    'AddReviewFunctionality.addReview' = 1
    'WriteReviewFunctionality.writeReview' = 1
    'DeleteReviewFunctionality.deleteReview' = 1
}
$summary = [ordered]@{
    environment = 'http://localhost:8081'
    total = 0; passed = 0; failed = 0; skipped = 0
    cleanup = 'NOT_VERIFIED'; result = 'NO_REPORT'; tests = @()
}
$path = 'target/cd-performance/surefire/testng-results.xml'
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
        $matches = @($methods | Where-Object { ($_.ParentNode.name + '.' + $_.name) -eq "com.hrm.TestCase.PerformanceTab.$key" })
        if ($matches.Count -ne $expected[$key]) { $correctTests = $false }
    }
    $cleanup = @($report.SelectNodes("//class[@name='com.hrm.Setup.CDPerformanceSetup']/test-method[@is-config='true' and @name='cleanupReviews' and @status='PASS']"))
    if ($cleanup.Count -eq 1) { $summary.cleanup = 'PASS' }
    $summary.result = 'FAIL'
    if ($correctTests -and $summary.passed -eq 3 -and $badConfigurations.Count -eq 0 -and $summary.cleanup -eq 'PASS') {
        $summary.result = 'PASS'
    }
}
$summary | ConvertTo-Json -Depth 4 | Set-Content "$evidence/performance-summary.json" -Encoding UTF8
Write-Output "Performance regression: $($summary.result); total=$($summary.total); passed=$($summary.passed); failed=$($summary.failed); skipped=$($summary.skipped); cleanup=$($summary.cleanup)"
if ($summary.result -ne 'PASS') { throw 'Performance requires all 3 invocations and cleanup to pass; inspect local reports' }
