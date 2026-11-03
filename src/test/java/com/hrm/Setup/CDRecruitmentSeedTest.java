package com.hrm.Setup;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.RecruitmentPage.AddVancanciesPage;
import com.hrm.Pages.RecruitmentPage.CandidatesPage;
import com.hrm.Pages.RecruitmentPage.VacanciesPage;
import com.hrm.Util.Config;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

// Setup only: tái sử dụng employee/job của PIM, không cộng vào regression PASS.
@Listeners(com.hrm.Util.TestListener.class)
public class CDRecruitmentSeedTest extends CDPIMSeedTest {
    @Test(groups = "data-preparation", dependsOnMethods = "preparePIMCatalogs",
            description = "CD setup: chuẩn bị vacancy cho Candidate và Shortlist")
    public void prepareRecruitmentVacancies() throws Exception {
        Assert.assertEquals(TestConfig.hiringManager, Config.get("admin.employee.name"),
                "Hiring Manager phải là employee baseline CD");
        Assert.assertEquals(TestConfig.shortlistVacancies.length, 3, "CD cần ba vacancy cho Issue #1926");
        TestUtil.recruitmentUtil();
        Properties result = new Properties();
        for (int i = 0; i < TestConfig.shortlistVacancies.length; i++) {
            String name = TestConfig.shortlistVacancies[i];
            Assert.assertTrue(name.startsWith("AUTO CD "), "Chỉ dùng fixture Recruitment CD riêng");
            VacanciesPage page = new CandidatesPage(driver).goToVacanciesPage("Vacancies", "");
            int count = page.countVacancies(name);
            Assert.assertTrue(count <= 1, "Duplicate vacancy: " + name);
            if (count == 0) {
                AddVancanciesPage add = page.openAddVancancies();
                add.addVancanciesFunction(name, TestConfig.jobTitleName, "CD Recruitment fixture",
                        TestConfig.hiringManager, "2");
                add.clickSave();
                page = new CandidatesPage(driver).goToVacanciesPage("Vacancies", "");
            }
            Assert.assertEquals(page.countVacancies(name), 1, "Phải có đúng một vacancy " + name);
            page.searchVacancy(TestConfig.jobTitleName, name, TestConfig.hiringManager, "Active");
            page.clickSearch();
            Assert.assertEquals(page.countVacancies(name), 1, "Fixture sai job/manager/status; không tự ghi đè");
            page.openVacancy(name);
            String id = driver.getCurrentUrl().substring(driver.getCurrentUrl().lastIndexOf('/') + 1);
            Assert.assertTrue(id.matches("\\d+"), "Không đọc được vacancy ID từ UI");
            result.setProperty("vacancy." + i + ".key", name);
            result.setProperty("vacancy." + i + ".id", id);
            result.setProperty("vacancy." + i + ".matches", "1");
            Log.info("[RECRUITMENT SEED] " + name + " | id=" + id + " | matches=1");
        }
        result.setProperty("kind", "DATA_PREPARATION");
        result.setProperty("result", "SUCCESS");
        try (Writer writer = Files.newBufferedWriter(Path.of(System.getProperty("seed.output"), "recruitment-seed-result.properties"))) {
            result.store(writer, "Setup only; excluded from regression totals");
        }
    }
}
