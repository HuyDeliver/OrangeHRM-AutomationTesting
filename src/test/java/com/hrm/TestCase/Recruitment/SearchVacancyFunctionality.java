package com.hrm.TestCase.Recruitment;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.RecruitmentPage.CandidatesPage;
import com.hrm.Pages.RecruitmentPage.VacanciesPage;
import com.hrm.Util.ExelReader;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class SearchVacancyFunctionality extends TestBase {
    @Epic("Recruitment")
    @Feature("Vacancies")
    @Test(priority = 1, dependsOnGroups = "recruitment-vacancy", description = "OHR27: Search vacancy using data-driven", dataProvider = "searchingVacancy")
    public void searchVacancy(String job, String vacancy, String manager, String status, String expectedFound) {
        Assert.assertTrue("true".equalsIgnoreCase(expectedFound) || "false".equalsIgnoreCase(expectedFound),
                "Excel: expectedFound phải là true hoặc false");
        TestUtil.recruitmentUtil();
        CandidatesPage candidatesPage = new CandidatesPage(driver);
        Assert.assertTrue(candidatesPage.isCandidatePageVisisble("Candidates"), "Không vào được trang candidates");
        VacanciesPage vacanciesPage = candidatesPage.goToVacanciesPage("Vacancies", "");
        Assert.assertTrue(vacanciesPage.isVacanciesPageVisible("Vacancies"));
        vacanciesPage.searchVacancy(job, vacancy, manager, status);
        vacanciesPage.clickSearch();
        Assert.assertTrue(vacanciesPage.checkSearch(job, vacancy, manager, status,
                Boolean.parseBoolean(expectedFound)), "Kết quả Vacancy không đúng expectedFound=" + expectedFound
                        + " | job=" + job + ", vacancy=" + vacancy + ", manager=" + manager + ", status=" + status);
        Log.info("OHR27: Kết quả tìm Vacancy đúng expectedFound=" + expectedFound);
    }

    @DataProvider(name = "searchingVacancy")
    public Object[][] getSearchingData() {
        String path = "src/test/resources/data/Datadriven-OrangeHrm.xlsx";
        String sheetName = "Vacancy";
        Object[][] data = ExelReader.getDataFromExel(path, sheetName);
        if (Boolean.getBoolean("cd.recruitment")) {
            for (Object[] row : data) {
                if (!row[0].toString().isBlank()) row[0] = TestConfig.jobTitleName;
                if (!row[1].toString().isBlank()) row[1] = TestDataShare.VACANCY_NAME;
                if (!row[2].toString().isBlank()) row[2] = TestConfig.hiringManager;
                // Giữ Status và expectedFound của Excel, kể cả dòng Closed/false.
            }
        }
        return data;
    }
}
