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
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.TestDataShare;
import com.hrm.Util.componentLocator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("Recruitment")
@Feature("Candidate Search")
public class SearchCandidateFunctionality extends TestBase {
    private final String candidateLastName = "SEARCH_" + TestDataShare.LASTNAME;
    private final String candidateName = TestConfig.firstName + " " + TestConfig.middleName + " " + candidateLastName;
    private CandidatesPage candidatesPage;
    private boolean recruitmentReady;

    @BeforeMethod
    public void createCandidateForSearch() {
        recruitmentReady = false;
        Log.info("Bước 1: Tạo candidate riêng để kiểm tra chức năng Search");
        TestUtil.recruitmentUtil();
        candidatesPage = new CandidatesPage(driver);
        Assert.assertTrue(candidatesPage.isCandidatePageVisisble("Candidates"), "Không vào được Candidates");
        recruitmentReady = true;

        Assert.assertEquals(candidatesPage.countCandidates(candidateName), 0,
                "Candidate marker đã tồn tại trước khi test");

        AddCandidatePage addCandidatePage = candidatesPage.openAddCandidatePage();
        addCandidatePage.addCandidateFunction(
                TestConfig.firstName,
                TestConfig.middleName,
                candidateLastName,
                TestConfig.candidateVacancy,
                TestDataShare.MYINFO_OTHER_EMAIL,
                TestConfig.phoneNumber,
                TestConfig.jobSpecification,
                TestConfig.skill,
                java.time.LocalDate.now().toString(),
                TestConfig.comment);

        ApplicationStage application = addCandidatePage.clickSave();
        Assert.assertTrue(addCandidatePage.isAddCandidateSuccess(), "Không tạo được candidate dùng cho Search");
        application.waitForApplicationStage();
        application.goToCandidatesPage("Candidates", "");
        Assert.assertEquals(candidatesPage.countCandidates(candidateName), 1,
                "Không tìm thấy candidate fixture sau khi tạo");
    }

    @Test(description = "OHR70: Search tìm thấy candidate theo tên")
    public void searchExistingCandidateByName() {
        // Arrange: candidate được tạo riêng trong BeforeMethod.
        // Act
        Log.info("Bước 2: Tìm candidate theo tên: " + candidateName);
        candidatesPage.searchCandidate(candidateName);

        // Assert
        Assert.assertEquals(candidatesPage.countCandidates(candidateName), 1,
                "Search phải trả về đúng candidate đã nhập");
        componentLocator.takeScreenshotTable(driver, "Search existing candidate");
        Log.info("OHR70: Search trả về đúng một candidate");
    }

    @Test(description = "OHR71: Search không tìm thấy candidate không tồn tại")
    public void searchMissingCandidateShouldReturnNoRecords() {
        // Arrange: marker chưa từng được tạo trong lượt chạy.
        String missingCandidate = "NO_MATCH_" + TestDataShare.LASTNAME;

        // Act
        Log.info("Bước 2: Tìm candidate không tồn tại: " + missingCandidate);
        candidatesPage.searchCandidate(missingCandidate);

        // Assert
        Assert.assertEquals(candidatesPage.countCandidates(missingCandidate), 0,
                "Search candidate không tồn tại phải trả về 0 record");
        componentLocator.takeScreenshotTable(driver, "Search missing candidate");
        Log.info("OHR71: Search candidate không tồn tại trả về 0 record");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupSearchCandidate() {
        if (recruitmentReady) {
            candidatesPage.removeCandidateIfPresent(candidateName);
            Log.info("Cleanup: candidate phục vụ Search đã được xóa");
        }
    }
}
