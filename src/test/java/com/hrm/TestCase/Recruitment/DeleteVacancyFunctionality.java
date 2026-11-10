package com.hrm.TestCase.Recruitment;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.RecruitmentPage.CandidatesPage;
import com.hrm.Pages.RecruitmentPage.VacanciesPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestDataShare;
import com.hrm.Util.componentLocator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class DeleteVacancyFunctionality extends TestBase {
    @Epic("Recruitment")
    @Feature("Vacancies")
    @Test(priority = 2, dependsOnGroups = "recruitment-vacancy", description = "OHR28: Delete vacancy")
    public void deleteVancancies() {
        TestUtil.recruitmentUtil();
        CandidatesPage candidatesPage = new CandidatesPage(driver);
        Assert.assertTrue(candidatesPage.isCandidatePageVisisble("Candidates"), "Không vào được trang candidates");
        VacanciesPage vacanciesPage = candidatesPage.goToVacanciesPage("Vacancies", "");
        Assert.assertTrue(vacanciesPage.isVacanciesPageVisible("Vacancies"));
        Assert.assertEquals(vacanciesPage.countVacancies(TestDataShare.VACANCY_NAME), 1, "Chưa có vacancy cần xóa");
        vacanciesPage.deleteVancancies(TestDataShare.VACANCY_NAME);
        Assert.assertTrue(vacanciesPage.isDeleteVancanciesSuccess("Delete Vancancies"), "Delete không thành công");
        Assert.assertEquals(vacanciesPage.countVacancies(TestDataShare.VACANCY_NAME), 0, "Vacancy vẫn còn sau Delete");
        componentLocator.takeScreenshotTable(driver, "Delete vacancy");
        Log.info("OHR28: Vacancy không còn trong danh sách");
    }
}
