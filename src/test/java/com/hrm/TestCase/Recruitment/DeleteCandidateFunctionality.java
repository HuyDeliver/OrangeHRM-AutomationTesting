package com.hrm.TestCase.Recruitment;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.RecruitmentPage.AddCandidatePage;
import com.hrm.Pages.RecruitmentPage.ApplicationStage;
import com.hrm.Pages.RecruitmentPage.CandidatesPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class DeleteCandidateFunctionality extends TestBase {
    private CandidatesPage candidatesPage;

    @Epic("Recruitment")
    @Feature("Candidate")
    @BeforeMethod
    public void addCandidate() {
        TestUtil.recruitmentUtil();
        candidatesPage = new CandidatesPage(driver);
        AddCandidatePage addCandidatePage = candidatesPage.openAddCandidatePage();
        Log.info("Tạo candidate");
        addCandidatePage.addCandidateFunction(
                TestConfig.firstName,
                TestConfig.middleName,
                TestDataShare.LASTNAME,
                TestConfig.jobTitleName,
                TestConfig.email,
                TestConfig.phoneNumber,
                TestConfig.jobSpecification,
                TestConfig.skill,
                "2026-09-27",
                TestConfig.comment);
        ApplicationStage applicationStage = addCandidatePage.clickSave();
        applicationStage.goToCandidatesPage("Candidates", "");
    }

    @Test(description = "OHR30: Delete candidate")
    public void deleteVancancies() {
        String name = TestConfig.firstName + " " + TestConfig.middleName + " " + TestDataShare.LASTNAME;
        Assert.assertTrue(candidatesPage.isCandidatePageVisisble("Candidates"), "Không vào được trang candidates");
        candidatesPage.deleteCandidate(name);
        Assert.assertTrue(candidatesPage.isDeleteCandidateSuccess("Delete candidate"), "Delete không thành công");
        Log.info("Delete candidate thành công");
    }
}
