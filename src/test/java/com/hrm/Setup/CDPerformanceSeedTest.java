package com.hrm.Setup;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.SideBar;
import com.hrm.Pages.PIMPage.EditEmployeePage;
import com.hrm.Pages.PIMPage.EmployeeListPage;
import com.hrm.Pages.PerformancePage.ManageReviewPage;
import com.hrm.Util.Config;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.componentLocator;

// Giữ prerequisites đã có của PIM/Recruitment/MyInfo; chuẩn bị thêm KPI và gán Job Title + Supervisor cho subordinate.
public class CDPerformanceSeedTest extends CDMyInfoSeedTest {
    @Test(groups = "data-preparation", dependsOnMethods = "prepareEducationLevel",
            description = "CD setup: KPIs + Subordinate Job/Supervisor, không tính functional PASS")
    public void preparePerformanceFixtures() throws Exception {
        String empId = Config.get("seed.employee.id", "AUTOEMP001");
        String supervisor = TestConfig.performanceSupervisor;
        TestUtil.employeeUtil();
        EmployeeListPage employees = new EmployeeListPage(driver);
        Assert.assertEquals(employees.searchByEmployeeIdAndCount(empId), 1, "Thiếu employee baseline " + empId);
        EditEmployeePage edit = employees.goToeEditEmployeePage(empId);
        edit.goToJobDetail("Job");
        componentLocator.chooseSelectAction(driver, "Job Title", TestConfig.jobTitleName);
        edit.saveInfo("Seed Job Title Performance");
        edit.goToReportTo("Report-to");
        if (!edit.hasSupervisor(supervisor, TestConfig.reportMethod)) {
            edit.addSupervisor("Assigned Supervisors");
            String keySup = supervisor.trim().split("\\s+")[0];
            edit.fillSupervisor(keySup, supervisor, TestConfig.reportMethod);
            edit.saveInfo("Seed Supervisor Performance");
        }

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

        Properties result = new Properties();
        result.setProperty("kind", "DATA_PREPARATION");
        result.setProperty("result", "SUCCESS");
        result.setProperty("kpi.skill", TestConfig.skill);
        result.setProperty("kpi.jobTitle", TestConfig.jobTitleName);
        result.setProperty("subordinate", TestConfig.surbordinate);
        result.setProperty("supervisor", supervisor);
        try (Writer out = Files.newBufferedWriter(Path.of(System.getProperty("seed.output"), "performance-seed-result.properties"))) {
            result.store(out, "Setup only");
        }
        Log.info("[PERFORMANCE SEED] KPIs=" + TestConfig.skill + ", " + TestConfig.jobTitleName);
    }
}
