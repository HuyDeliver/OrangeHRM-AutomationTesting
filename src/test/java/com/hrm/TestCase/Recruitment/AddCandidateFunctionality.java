package com.hrm.TestCase.Recruitment;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.RecruitmentPage.AddCandidatePage;
import com.hrm.Pages.RecruitmentPage.CandidatesPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class AddCandidateFunctionality extends TestBase {
    @Epic("Recruitment")
    @Feature("Candidate")
    @Test(description = "OHR29: Add new Candidate")
    public void addCandidate() {
        TestUtil.recruitmentUtil();
        CandidatesPage candidatesPage = new CandidatesPage(driver);
        Assert.assertTrue(candidatesPage.isCandidatePageVisisble("Candidates"), "Không vào được trang candidates");
        AddCandidatePage addCandidatePage = candidatesPage.openAddCandidatePage();
        Assert.assertTrue(addCandidatePage.isAddCandidatePageVisible("Add Candidate"),
                "không vào được trang add Candidate");
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
        addCandidatePage.clickSave();
        Assert.assertTrue(addCandidatePage.isAddCandidateSuccess(), "Add candidate không thành công");
        Log.info("Add candidate thành công");
    }
}
