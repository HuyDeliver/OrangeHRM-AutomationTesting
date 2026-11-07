package com.hrm.Setup;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.RecruitmentPage.CandidatesPage;
import com.hrm.Pages.RecruitmentPage.VacanciesPage;
import com.hrm.Util.Config;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.TestDataShare;

// Configuration/cleanup, không có functional @Test.
public class CDRecruitmentSetup extends TestBase {
    private boolean ready;

    @BeforeTest
    public void checkCDConfiguration() {
        if (!Boolean.getBoolean("cd.recruitment")) return;
        Assert.assertEquals(Config.get("app.url"), "http://localhost:8081/web/index.php/auth/login",
                "Recruitment CD chỉ được chạy trên 8081");
        Assert.assertTrue(TestConfig.candidateVacancy.startsWith("AUTO CD "), "Thiếu vacancy baseline CD");
        ready = true;
    }

    @AfterTest(alwaysRun = true)
    public void cleanupCandidates() {
        if (!ready) return;
        try {
            super.setUp();
            TestUtil.recruitmentUtil();
            CandidatesPage page = new CandidatesPage(driver);
            for (String lastName : new String[]{TestDataShare.LASTNAME, "SEARCH_" + TestDataShare.LASTNAME,
                    "REJECT_" + TestDataShare.LASTNAME, "DELETE_" + TestDataShare.LASTNAME}) {
                String name = TestConfig.firstName + " " + TestConfig.middleName + " " + lastName;
                page.removeCandidateIfPresent(name);
            }
            Log.info("[CD RECRUITMENT] Candidate tạm của lượt chạy: 0 record");
        } finally { super.tearDown(); }
    }

    @AfterTest(alwaysRun = true, dependsOnMethods = "cleanupCandidates")
    public void cleanupVacancy() {
        if (!ready) return;
        try {
            super.setUp();
            TestUtil.recruitmentUtil();
            VacanciesPage page = new CandidatesPage(driver).goToVacanciesPage("Vacancies", "");
            int count = page.countVacancies(TestDataShare.VACANCY_NAME);
            Assert.assertTrue(count <= 1, "Cleanup: nhiều vacancy khớp tên của lượt chạy");
            if (count == 1) {
                page.deleteVancancies(TestDataShare.VACANCY_NAME);
                Assert.assertTrue(page.isDeleteVancanciesSuccess("Cleanup vacancy"), "Không xóa được vacancy tạm");
            }
            Assert.assertEquals(page.countVacancies(TestDataShare.VACANCY_NAME), 0, "Vacancy tạm chưa được xóa");
            Log.info("[CD RECRUITMENT] Vacancy tạm: 0 record; giữ ba vacancy baseline");
        } finally { super.tearDown(); }
    }
}
