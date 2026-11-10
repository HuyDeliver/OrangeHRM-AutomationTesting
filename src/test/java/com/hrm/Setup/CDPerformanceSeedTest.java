package com.hrm.Setup;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.SideBar;
import com.hrm.Pages.PIMPage.EditEmployeePage;
import com.hrm.Pages.PIMPage.EmployeeListPage;
import com.hrm.Pages.PerformancePage.ManageReviewPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.componentLocator;

// Giữ prerequisites đã có của PIM/Recruitment/MyInfo; chuẩn bị thêm KPI và gán Job Title + Supervisor cho subordinate.
public class CDPerformanceSeedTest extends CDMyInfoSeedTest {
    @Test(groups = "data-preparation", dependsOnMethods = "prepareEducationLevel", description = "CD setup: KPIs + Subordinate Job/Supervisor, không tính functional PASS")
    public void preparePerformanceFixtures() throws Exception {
        Properties fixture = new Properties();
        try (Reader reader = Files.newBufferedReader(Path.of("deploy/seed.properties"))) {
            fixture.load(reader);
        }
        String empId = fixture.getProperty("employee.id");
        Assert.assertTrue(empId != null && !empId.isBlank(), "Thiếu employee.id trong deploy/seed.properties");
        String supervisor = TestConfig.performanceSupervisor;

        TestUtil.employeeUtil();
        EmployeeListPage employees = new EmployeeListPage(driver);
        Assert.assertEquals(employees.searchByEmployeeIdAndCount(empId), 1, "Thiếu employee baseline " + empId);
        EditEmployeePage edit = employees.goToeEditEmployeePage(empId);

        edit.goToJobDetail("Job");
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".oxd-loading-spinner, .oxd-form-loader")));
        if (!edit.hasJobTitle(TestConfig.jobTitleName)) {
            componentLocator.chooseSelectAction(driver, "Job Title", TestConfig.jobTitleName);
            edit.saveInfo("Seed Job Title Performance");
        }
        Assert.assertTrue(edit.hasJobTitle(TestConfig.jobTitleName), "Lưu Job Title cho subordinate không thành công");

        edit.goToReportTo("Report-to");
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".oxd-loading-spinner, .oxd-form-loader")));
        if (!edit.hasSupervisor(supervisor, TestConfig.reportMethod)) {
            edit.addSupervisor("Assigned Supervisors");
            String keySup = supervisor.trim().split("\\s+")[0];
            edit.fillSupervisor(keySup, supervisor, TestConfig.reportMethod);
            edit.saveInfo("Seed Supervisor Performance");
        }
        Assert.assertTrue(edit.hasSupervisor(supervisor, TestConfig.reportMethod),
                "Chưa gán được Supervisor cho subordinate");

        ManageReviewPage perf = new SideBar(driver).clickPerformancePage("Performance");
        perf.openKpiPage();
        if (perf.countKpis(TestConfig.skill) == 0) {
            Assert.assertTrue(perf.addKpi(TestConfig.skill, TestConfig.jobTitleName, "1", "5"),
                    "Không tạo được KPI skill");
        }
        if (perf.countKpis(TestConfig.jobTitleName) == 0) {
            Assert.assertTrue(perf.addKpi(TestConfig.jobTitleName, TestConfig.jobTitleName, "1", "8"),
                    "Không tạo được KPI jobTitle");
        }
        Assert.assertEquals(perf.countKpis(TestConfig.skill), 1, "KPI skill phải có đúng 1 bản ghi");
        Assert.assertEquals(perf.countKpis(TestConfig.jobTitleName), 1, "KPI jobTitle phải có đúng 1 bản ghi");

        Properties result = new Properties();
        result.setProperty("kind", "DATA_PREPARATION");
        result.setProperty("result", "SUCCESS");
        result.setProperty("kpi.skill", TestConfig.skill);
        result.setProperty("kpi.jobTitle", TestConfig.jobTitleName);
        result.setProperty("subordinate", TestConfig.surbordinate);
        result.setProperty("supervisor", supervisor);
        try (Writer out = Files
                .newBufferedWriter(Path.of(System.getProperty("seed.output"), "performance-seed-result.properties"))) {
            result.store(out, "Setup only");
        }
        Log.info("[PERFORMANCE SEED] KPIs=" + TestConfig.skill + ", " + TestConfig.jobTitleName);
    }
}
