package com.hrm.TestCase.PerformanceTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.PerformancePage.AddReviewPage;
import com.hrm.Pages.PerformancePage.ManageReviewPage;
import com.hrm.Util.TestConfig;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class AddReviewFunctionality extends TestBase {
    @Epic("Performance Tab")
    @Feature("Manage Review")
    @Test(description = "OHR38: Add new review")
    public void addReview() {
        TestUtil.performanceUtil();
        ManageReviewPage manageReviewPage = new ManageReviewPage(driver);
        manageReviewPage.clickManageReview("Manage Reviews", "Manage Reviews");
        Assert.assertTrue(manageReviewPage.isEmployeeReviewsVisible("Employee review"), "Không vào được trang");
        AddReviewPage addReviewPage = manageReviewPage.clickButtonAdd();
        Assert.assertTrue(addReviewPage.isAddReviewsVisible("Add Review"), "Không vào được trang");
        addReviewPage.fillAddReview(TestConfig.surbordinate, TestConfig.nameSupervisor, TestConfig.startDate,
                TestConfig.endDate, TestConfig.dueDate);
        addReviewPage.clickActivate();
        Assert.assertTrue(addReviewPage.isAddReviewSuccess("Activate"), "Add không thành công");

    }
}
