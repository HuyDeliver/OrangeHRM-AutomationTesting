$ErrorActionPreference = 'Stop'
$evidence = 'target/cd-evidence'
New-Item -ItemType Directory -Path $evidence -Force | Out-Null
$reportPath = 'target/cd-pim/surefire/testng-results.xml'
$expected = @('searchExistingEmployeeShouldReturnMatchingId',
    'searchMissingEmployeeShouldReturnNoRecords', 'employeeDetailsShouldMatchBaseline')
$summary = [ordered]@{
    environment = 'http://localhost:8081'
    total = 0
    passed = 0
    failed = 0
    skipped = 0
    result = 'NO_REPORT'
    tests = @()
}
if (Test-Path -LiteralPath $reportPath) {
    [xml]$report = Get-Content -LiteralPath $reportPath -Raw
    $methods = @($report.SelectNodes('//test-method[not(@is-config="true")]'))
    $badConfigurations = @($report.SelectNodes('//test-method[@is-config="true" and @status!="PASS"]'))
    $summary.total = $methods.Count
    $summary.passed = @($methods | Where-Object { $_.status -eq 'PASS' }).Count
    $summary.failed = @($methods | Where-Object { $_.status -eq 'FAIL' }).Count
    $summary.skipped = @($methods | Where-Object { $_.status -eq 'SKIP' }).Count
    $summary.tests = @($methods | ForEach-Object { [ordered]@{ name = $_.name; status = $_.status } })
    $wrongClasses = @($methods | Where-Object { $_.ParentNode.name -ne 'com.hrm.TestCase.PIMTab.CDPIMTest' })
    $actualNames = @($methods | ForEach-Object { $_.name })
    $correctNames = $methods.Count -eq 3 -and !(Compare-Object $expected $actualNames)
    $summary.result = 'FAIL'
    if ($correctNames -and $wrongClasses.Count -eq 0 -and $badConfigurations.Count -eq 0 -and $summary.passed -eq 3) {
        $summary.result = 'PASS'
    }
}
# Chỉ đưa tên test và kết quả lên GitHub; log/screenshot giữ trên runner.
$summary | ConvertTo-Json -Depth 4 | Set-Content "$evidence/pim-summary.json" -Encoding UTF8
Write-Output "PIM CD: $($summary.result); total=$($summary.total); passed=$($summary.passed); failed=$($summary.failed); skipped=$($summary.skipped)"
if ($summary.result -ne 'PASS') { throw 'PIM CD must run all three tests and pass without skips' }
