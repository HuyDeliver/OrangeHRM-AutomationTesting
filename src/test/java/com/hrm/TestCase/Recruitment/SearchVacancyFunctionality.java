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

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class SearchVacancyFunctionality extends TestBase {
    @Epic("Recruitment")
    @Feature("Vacancies")
    @Test(description = "OHR27: Search vacancy using data-driven", dataProvider = "searchingVacancy")
    public void searchVacancy(String job, String vacancy, String manager, String status) {
        TestUtil.recruitmentUtil();
        CandidatesPage candidatesPage = new CandidatesPage(driver);
        Assert.assertTrue(candidatesPage.isCandidatePageVisisble("Candidates"), "Không vào được trang candidates");
        VacanciesPage vacanciesPage = candidatesPage.goToVacanciesPage("Vacancies", "");
        Assert.assertTrue(vacanciesPage.isVacanciesPageVisible("Vacancies"));
        vacanciesPage.searchVacancy(job, vacancy, manager, status);
        vacanciesPage.clickSearch();
        if (vacanciesPage.checkSearch(job, vacancy, manager, status)) {
            Log.info("Tìm thấy bản ghi");
        } else {
            Log.info("Không tìm thấy bản ghi");
        }
    }

    @DataProvider(name = "searchingVacancy")
    public Object[][] getSearchingData() {
        String path = "src/test/resources/data/Datadriven-OrangeHrm.xlsx";
        String sheetName = "Vacancy";
        return ExelReader.getDataFromExel(path, sheetName);
    }
}
