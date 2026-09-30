package com.hrm.TestCase.PerformanceTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.PerformancePage.ManageReviewPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class DeleteReviewFunctionality extends TestBase {
    @Epic("Performance Tab")
    @Feature("Manage Review")
    @Test(description = "OHR40: Delete review")
    public void deleteReview() {
        String date = TestConfig.startDate + " - " + TestConfig.endDate;
        TestUtil.performanceUtil();
        ManageReviewPage manageReviewPage = new ManageReviewPage(driver);
        manageReviewPage.clickManageReview("Manage Reviews", "Manage Reviews");
        Assert.assertTrue(manageReviewPage.isEmployeeReviewsVisible("Employee review"), "Không vào được trang");
        manageReviewPage.deleteReview(date);
        Log.info("Xóa thành công");
    }
}
