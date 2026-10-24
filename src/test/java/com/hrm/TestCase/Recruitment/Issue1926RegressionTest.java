package com.hrm.TestCase.Recruitment;

import java.time.Duration;
import java.time.LocalDate;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.RecruitmentPage.AddCandidatePage;
import com.hrm.Pages.RecruitmentPage.ApplicationStage;
import com.hrm.Pages.RecruitmentPage.CandidatesPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("Recruitment")
@Feature("Issue #1926 - Shortlist Candidate")
public class Issue1926RegressionTest extends TestBase {
    private CandidatesPage candidatesPage;
    private String candidateMarker;

    @Test(dataProvider = "localVacancies", description = "OHR46: Shortlist phải lưu đúng trạng thái và history")
    public void shortlistingCandidateShouldUpdateStatusAndHistory(String vacancy) {
        // Arrange
        candidateMarker = null;
        Log.info("Bước 1: Đăng nhập Recruitment | Vacancy: " + vacancy);
        TestUtil.recruitmentUtil();
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        candidatesPage = new CandidatesPage(driver);
        AddCandidatePage addCandidatePage = candidatesPage.openAddCandidatePage();
        String marker = "ISS1926_" + System.currentTimeMillis();

        Log.info("Bước 2: Tạo candidate riêng: " + marker);
        addCandidatePage.addCandidateFunction(TestConfig.firstName, TestConfig.middleName, marker,
                vacancy, marker + TestConfig.shortlistEmailDomain, TestConfig.phoneNumber,
                TestConfig.jobSpecification, TestConfig.skill, LocalDate.now().toString(), TestConfig.comment);
        candidateMarker = marker;
        ApplicationStage applicationStage = addCandidatePage.clickSave();
        applicationStage.waitForApplicationStage();
        Assert.assertEquals(applicationStage.getCandidateStatus(), "Status: Application Initiated",
                "Setup lỗi: candidate mới chưa ở trạng thái Application Initiated");

        // Act
        Log.info("Bước 3: Shortlist rồi Save | Vacancy: " + vacancy);
        applicationStage.shortlistCandidate("Issue #1926 | " + marker);
        String actualStatus = applicationStage.getCandidateStatus();
        Log.info("Expected: Status: Shortlisted | Actual: " + actualStatus);
        Assert.assertEquals(actualStatus, "Status: Shortlisted",
                "Shortlist không thành công. Nội dung UI: " + applicationStage.getPageText());

        // Assert
        Log.info("Bước 4: Refresh để kiểm tra trạng thái đã lưu và history");
        applicationStage.reloadApplicationStage();
        Assert.assertEquals(applicationStage.getCandidateStatus(), "Status: Shortlisted",
                "Trạng thái Shortlisted không được giữ sau refresh");
        Assert.assertTrue(applicationStage.hasShortlistHistory(), "History không có thao tác Shortlisted");
        Log.info("Đã xác nhận sau refresh: Status: Shortlisted | History: Shortlisted | Vacancy: " + vacancy);
        takeScreenshot("Shortlisted - " + vacancy);
    }

    @DataProvider(name = "localVacancies")
    public Object[][] localVacancies() {
        Object[][] data = new Object[TestConfig.shortlistVacancies.length][1];
        for (int i = 0; i < data.length; i++) {
            data[i][0] = TestConfig.shortlistVacancies[i];
        }
        return data;
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTestCandidate() {
        if (candidatesPage != null && candidateMarker != null) {
            candidatesPage.deleteTestCandidate(candidateMarker);
        }
    }
}
