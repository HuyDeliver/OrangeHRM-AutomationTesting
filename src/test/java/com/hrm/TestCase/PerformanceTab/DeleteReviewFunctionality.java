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
    @Test(description = "OHR40: Delete review", dependsOnGroups = "performance-add")
    public void deleteReview() {
        String date = TestConfig.startDate + " - " + TestConfig.endDate;
        TestUtil.performanceUtil();
        ManageReviewPage manageReviewPage = new ManageReviewPage(driver);
        manageReviewPage.clickManageReview("Manage Reviews", "Manage Reviews");
        Assert.assertTrue(manageReviewPage.isEmployeeReviewsVisible("Employee review"), "Không vào được trang");
        int beforeCount = manageReviewPage.countReviews(date);
        Assert.assertTrue(beforeCount > 0, "Không tìm thấy review cần xóa: " + date);
        manageReviewPage.deleteReview(date);
        Assert.assertTrue(manageReviewPage.isDeleteReviewSuccess(), "Không hiển thị thông báo xóa thành công");
        Assert.assertTrue(manageReviewPage.countReviews(date) < beforeCount, "Xóa review không thành công");
        Log.info("Xóa thành công");
    }
}
