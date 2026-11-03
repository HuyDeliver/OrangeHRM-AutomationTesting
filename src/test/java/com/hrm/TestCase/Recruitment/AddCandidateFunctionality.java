package com.hrm.TestCase.Recruitment;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.RecruitmentPage.AddCandidatePage;
import com.hrm.Pages.RecruitmentPage.CandidatesPage;
import com.hrm.Pages.RecruitmentPage.ApplicationStage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class AddCandidateFunctionality extends TestBase {
    private CandidatesPage candidatesPage;
    private boolean candidateCreated;

    @Epic("Recruitment")
    @Feature("Candidate")
    @Test(description = "OHR29: Add new Candidate")
    public void addCandidate() {
        candidateCreated = false;
        TestUtil.recruitmentUtil();
        candidatesPage = new CandidatesPage(driver);
        Assert.assertTrue(candidatesPage.isCandidatePageVisisble("Candidates"), "Không vào được trang candidates");
        AddCandidatePage addCandidatePage = candidatesPage.openAddCandidatePage();
        Assert.assertTrue(addCandidatePage.isAddCandidatePageVisible("Add Candidate"),
                "không vào được trang add Candidate");
        addCandidatePage.addCandidateFunction(
                TestConfig.firstName,
                TestConfig.middleName,
                TestDataShare.LASTNAME,
                TestConfig.candidateVacancy,
                TestConfig.candidateEmail,
                TestConfig.phoneNumber,
                TestConfig.jobSpecification,
                TestConfig.skill,
                java.time.LocalDate.now().toString(),
                TestConfig.comment);
        ApplicationStage application = addCandidatePage.clickSave();
        candidateCreated = addCandidatePage.isAddCandidateSuccess();
        Assert.assertTrue(candidateCreated, "Add candidate không thành công");
        application.waitForApplicationStage();
        application.reloadApplicationStage();
        Assert.assertEquals(application.getCandidateStatus(), "Status: Application Initiated", "Sai trạng thái candidate sau refresh");
        application.goToCandidatesPage("Candidates", "");
        Assert.assertEquals(candidatesPage.countCandidates(TestConfig.firstName + " " + TestConfig.middleName + " " + TestDataShare.LASTNAME),
                1, "Candidate không được lưu đúng tên");
        Log.info("OHR29: Candidate đã lưu, trạng thái Application Initiated");
    }

    @AfterMethod(alwaysRun = true)
    public void deleteTestCandidate() {
        if (candidateCreated) {
            candidatesPage.deleteTestCandidate(TestDataShare.LASTNAME);
        }
    }
}
