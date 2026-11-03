$ErrorActionPreference = 'Stop'
$evidence = 'target/cd-evidence'
New-Item -ItemType Directory -Path $evidence -Force | Out-Null
$expected = @{
    'Issue1923RegressionTest.clickingAvatarAgainShouldCloseProfileMenu' = 1
    'Issue1923RegressionTest.clickingOutsideShouldCloseProfileMenu' = 1
}
$summary = [ordered]@{
    environment = 'http://localhost:8081'
    total = 0; passed = 0; failed = 0; skipped = 0
    cleanup = 'NOT_VERIFIED'; result = 'NO_REPORT'; tests = @()
}
$path = 'target/cd-profile/surefire/testng-results.xml'
if (Test-Path -LiteralPath $path) {
    [xml]$report = Get-Content -LiteralPath $path -Raw
    $methods = @($report.SelectNodes('//test-method[not(@is-config="true")]'))
    $badConfigurations = @($report.SelectNodes('//test-method[@is-config="true" and @status!="PASS"]'))
    $summary.total = $methods.Count
    $summary.passed = @($methods | Where-Object { $_.status -eq 'PASS' }).Count
    $summary.failed = @($methods | Where-Object { $_.status -eq 'FAIL' }).Count
    $summary.skipped = @($methods | Where-Object { $_.status -eq 'SKIP' }).Count
    $summary.tests = @($methods | ForEach-Object { [ordered]@{ name = $_.ParentNode.name + '.' + $_.name; status = $_.status } })
    $correctTests = $methods.Count -eq 2
    foreach ($key in $expected.Keys) {
        $matches = @($methods | Where-Object { ($_.ParentNode.name + '.' + $_.name) -eq "com.hrm.TestCase.ProfileTab.$key" })
        if ($matches.Count -ne $expected[$key]) { $correctTests = $false }
    }
    # Profile chỉ thao tác menu; không tạo test data. tearDown vẫn được kiểm tra như config.
    $summary.cleanup = 'NOT_REQUIRED'
    $summary.result = 'FAIL'
    if ($correctTests -and $summary.passed -eq 2 -and $badConfigurations.Count -eq 0 -and $summary.cleanup -eq 'NOT_REQUIRED') {
        $summary.result = 'PASS'
    }
}
$summary | ConvertTo-Json -Depth 4 | Set-Content "$evidence/profile-summary.json" -Encoding UTF8
Write-Output "Profile regression: $($summary.result); total=$($summary.total); passed=$($summary.passed); failed=$($summary.failed); skipped=$($summary.skipped); cleanup=$($summary.cleanup)"
if ($summary.result -ne 'PASS') { throw 'Profile requires all 2 invocations and cleanup to pass; inspect local reports' }
