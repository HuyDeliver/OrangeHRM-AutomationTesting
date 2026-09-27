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

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class AddVacancyFunctionality extends TestBase {
    @Epic("Recruitment")
    @Feature("Vacancies")
    @Test(description = "OHR25: Add new vacancy")
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
                TestConfig.jobDescription, TestConfig.nameSupervisor2, "2");
        addVancanciesPage.clickSave();
        addVancanciesPage.addAttachment(TestConfig.jobSpecification, "Nothing", "Attachments");
        Assert.assertTrue(addVancanciesPage.isAddvancanciesSuccess(), "Add không thành công");
        Log.info("adđ vancacies thành công");
    }
}
