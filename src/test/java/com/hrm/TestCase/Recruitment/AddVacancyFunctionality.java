package com.hrm.TestCase.Recruitment;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.RecruitmentPage.AddVancanciesPage;
import com.hrm.Pages.RecruitmentPage.CandidatesPage;
import com.hrm.Pages.RecruitmentPage.VacanciesPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.TestDataShare;
import com.hrm.Util.componentLocator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class AddVacancyFunctionality extends TestBase {
    @Epic("Recruitment")
    @Feature("Vacancies")
    @Test(groups = "recruitment-vacancy", description = "OHR26: Add new vacancy")
    public void addVacancy() {
        TestUtil.recruitmentUtil();
        CandidatesPage candidatesPage = new CandidatesPage(driver);
        Assert.assertTrue(candidatesPage.isCandidatePageVisisble("Candidates"), "Không vào được trang candidates");
        VacanciesPage vacanciesPage = candidatesPage.goToVacanciesPage("Vacancies", "");
        Assert.assertTrue(vacanciesPage.isVacanciesPageVisible("Vacancies"));
        AddVancanciesPage addVancanciesPage = vacanciesPage.openAddVancancies();
        Assert.assertTrue(addVancanciesPage.isAddVancanciesPageVisible("Add Vacancy"),
                "không vào được trang add vancancies");
        addVancanciesPage.addVancanciesFunction(TestDataShare.VACANCY_NAME, TestConfig.jobTitleName,
                TestConfig.jobDescription, TestConfig.hiringManager, "2");
        addVancanciesPage.clickSave();
        addVancanciesPage.addAttachment(TestConfig.jobSpecification, "Nothing", "Attachments");
        Assert.assertTrue(addVancanciesPage.isAddvancanciesSuccess(), "Add không thành công");
        vacanciesPage = candidatesPage.goToVacanciesPage("Vacancies", "");
        vacanciesPage.searchVacancy(TestConfig.jobTitleName, TestDataShare.VACANCY_NAME, TestConfig.hiringManager, "Active");
        vacanciesPage.clickSearch();
        Assert.assertEquals(vacanciesPage.countVacancies(TestDataShare.VACANCY_NAME), 1, "Vacancy không được lưu đúng");
        componentLocator.takeScreenshotTable(driver, "Add vacancy");
        Log.info("OHR26: Vacancy đã lưu đúng tên, job, manager và Active");
    }
}
