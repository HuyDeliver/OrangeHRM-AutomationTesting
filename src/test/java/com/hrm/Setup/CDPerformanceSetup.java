package com.hrm.Setup;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.PerformancePage.ManageReviewPage;
import com.hrm.Util.Config;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

// Configuration/cleanup cho Performance CD, không tính vào functional @Test.
public class CDPerformanceSetup extends TestBase {
    private boolean ready;

    @BeforeTest
    public void checkCDConfiguration() {
        if (!Boolean.getBoolean("cd.performance")) return;
        Assert.assertEquals(Config.get("app.url"), "http://localhost:8081/web/index.php/auth/login",
                "Performance CD chỉ được chạy trên 8081");
        Assert.assertTrue(TestConfig.skill.startsWith("AUTO CD "), "Thiếu KPI baseline CD");
        ready = true;
    }

    @AfterTest(alwaysRun = true)
    public void cleanupReviews() {
        if (!ready) return;
        try {
            super.setUp();
            TestUtil.performanceUtil();
            ManageReviewPage page = new ManageReviewPage(driver);
            page.clickManageReview("Manage Reviews", "Manage Reviews");
            String date = TestConfig.startDate + " - " + TestConfig.endDate;
            while (page.countReviews(date) > 0) {
                page.deleteReview(date);
                Assert.assertTrue(page.isDeleteReviewSuccess(), "Không xóa được review tạm");
            }
            Log.info("[CD PERFORMANCE] Review tạm của lượt chạy: 0 record");
        } finally {
            super.tearDown();
        }
    }
}
