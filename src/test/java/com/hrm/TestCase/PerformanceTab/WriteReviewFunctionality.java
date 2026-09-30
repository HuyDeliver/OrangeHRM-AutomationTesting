package com.hrm.TestCase.PerformanceTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.PerformancePage.ManageReviewPage;
import com.hrm.Pages.PerformancePage.WriteReviewPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class WriteReviewFunctionality extends TestBase {
    private ManageReviewPage manageReviewPage;

    @Epic("Performance Tab")
    @Feature("Manage Review")
    @Test(description = "OHR39: write review ")
    public void writeReview() {
        String name = TestConfig.shortenName(TestConfig.surbordinate);
        TestUtil.performanceUtil();
        manageReviewPage = new ManageReviewPage(driver);
        manageReviewPage.clickManageReview("Manage Reviews", "Manage Reviews");
        Assert.assertTrue(manageReviewPage.isEmployeeReviewsVisible("Employee Reviews"), "Không vào được trang");
        WriteReviewPage writeReviewPage = manageReviewPage.goToWriteReviewPage(name);
        Assert.assertTrue(writeReviewPage.isAddReviewsVisible("Performance Review"), "Không vào được trang");
        writeReviewPage.fillKpiEvaluation(TestConfig.skill, 5, "Good", 1, 5);
        writeReviewPage.fillKpiEvaluation(TestConfig.jobTitleName, 3, "Good", 1, 8);
        writeReviewPage.enterGeneralComment("Good");
        writeReviewPage.reviewFinalization(TestConfig.dueDate, "5", "OKe");
        writeReviewPage.clickComplete();
        manageReviewPage = writeReviewPage.goToManageReviewPage("Manage Reviews", "Manage Reviews");
        Assert.assertTrue(manageReviewPage.isWrietReviewSuccess(name, "Completed"),
                "Write review not success");
        Log.info("Write review thành công");
    }
}
