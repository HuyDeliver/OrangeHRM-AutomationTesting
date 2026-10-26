package com.hrm.TestCase.Recruitment;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.RecruitmentPage.AddCandidatePage;
import com.hrm.Pages.RecruitmentPage.ApplicationStage;
import com.hrm.Pages.RecruitmentPage.CandidatesPage;
import com.hrm.Pages.RecruitmentPage.RejectCandidate;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("Recruitment")
@Feature("Candidate")
public class RejectCandidateFunctionality extends TestBase {
    private CandidatesPage candidatesPage;
    private boolean candidateCreated;
    private final String candidateLastName = "REJECT_" + TestDataShare.LASTNAME;

    @BeforeMethod
    public void addCandidate() {
        candidateCreated = false;
        TestUtil.recruitmentUtil();
        candidatesPage = new CandidatesPage(driver);
        AddCandidatePage addCandidatePage = candidatesPage.openAddCandidatePage();
        Log.info("Tạo candidate");
        addCandidatePage.addCandidateFunction(
                TestConfig.firstName,
                TestConfig.middleName,
                candidateLastName,
                TestConfig.jobTitleName,
                TestConfig.email,
                TestConfig.phoneNumber,
                TestConfig.jobSpecification,
                TestConfig.skill,
                "2026-09-27",
                TestConfig.comment);
        ApplicationStage applicationStage = addCandidatePage.clickSave();
        applicationStage.waitForApplicationStage();
        candidateCreated = true;
        Assert.assertEquals(applicationStage.getCandidateStatus(), "Status: Application Initiated",
                "Setup: candidate mới phải ở trạng thái Application Initiated");
        applicationStage.goToCandidatesPage("Candidates", "");
    }

    @Test(description = "OHR30: Reject Candidate")
    public void rejectCandidate() {
        Assert.assertTrue(candidatesPage.isCandidatePageVisisble("Candidates"), "Không vào được trang candidates");
        String name = TestConfig.firstName + " " + TestConfig.middleName + " " + candidateLastName;
        ApplicationStage applicationStage = candidatesPage.goToApplicationStage(name);
        Assert.assertTrue(applicationStage.isApplicationPagePageVisible("Application stage"),
                "Không vào được Application stage");
        RejectCandidate rejectCandidate = applicationStage.goToRejectCandidate();
        Assert.assertTrue(rejectCandidate.isRejectPagePageVisible("Reject Candidates"),
                "Không vào được Rejectc Candidate");
        rejectCandidate.fillNote("Ứng viên ko đạt");
        applicationStage.waitForApplicationStage();
        Assert.assertEquals(applicationStage.getCandidateStatus(), "Status: Rejected",
                "Candidate chưa chuyển sang Rejected sau Save");

        applicationStage.reloadApplicationStage();
        Assert.assertEquals(applicationStage.getCandidateStatus(), "Status: Rejected",
                "Trạng thái Rejected không được giữ sau refresh");
        Assert.assertTrue(applicationStage.hasRejectHistory(), "History chưa có thao tác Rejected");
        Log.info("OHR30: Trạng thái Rejected và history được giữ sau refresh");
    }

    @AfterMethod(alwaysRun = true)
    public void deleteCandidate() {
        if (candidateCreated) {
            candidatesPage.deleteTestCandidate(candidateLastName);
        }
    }
}
