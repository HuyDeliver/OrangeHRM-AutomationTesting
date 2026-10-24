package com.hrm.TestCase.TimeTab;

import java.time.Duration;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.TimePage.TimesheetPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("Time")
@Feature("Issue #1890 - Timesheet có dòng trống")
public class Issue1890RegressionTest extends TestBase {
    private TimesheetPage timesheetPage;
    private boolean fixtureReady;

    @BeforeMethod
    public void openTestTimesheet() {
        // Arrange
        fixtureReady = false;
        Log.info("Bước 1: Đăng nhập Time và mở đúng tuần fixture");
        TestUtil.timeUtil();
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        timesheetPage = new TimesheetPage(driver);
        timesheetPage.openMyTimesheet(TestConfig.timesheetStartDate);
        Assert.assertEquals(timesheetPage.getPeriod(), TestConfig.timesheetPeriod, "Đang mở sai tuần fixture");
        Assert.assertEquals(timesheetPage.getStatus(), "Status: Not Submitted", "Không sửa timesheet đã Submit");
        Assert.assertTrue(timesheetPage.isZeroBaseline(TestConfig.timesheetProjectOption, TestConfig.timesheetActivityA),
                "Setup lỗi: phải chỉ có dòng A của fixture, tổng giờ 00:00 và chưa có dòng B");
        fixtureReady = true;
        Log.info("Baseline: dòng A = 00:00 | Chưa có dòng B | Not Submitted");
        timesheetPage.openEdit();
    }

    @Test(description = "OHR47: Một dòng hợp lệ phải lưu được 01:00")
    public void validRowShouldKeepOneHourAfterSave() {
        // Act
        Log.info("Bước 2 CONTROL: Nhập 01:00 cho A, không thêm dòng trống");
        timesheetPage.fillFirstDay(TestConfig.timesheetHours);
        timesheetPage.saveAndReload();

        // Assert
        String actual = timesheetPage.getFirstDayHours(TestConfig.timesheetActivityA);
        Log.info("CONTROL | Expected A: 01:00 | Actual A sau refresh: " + actual);
        Assert.assertEquals(actual, TestConfig.timesheetHours, "Control lỗi: dòng hợp lệ không lưu được giờ");
        takeScreenshot("Issue1890 - Control saved 01:00");
    }

    @Test(description = "OHR48: Dòng B trống không được làm mất 01:00 của A")
    public void emptyRowShouldNotDiscardValidHours() {
        // Act
        Log.info("Bước 2 REGRESSION: Nhập 01:00 cho A, thêm B nhưng không chạm ô giờ của B");
        timesheetPage.fillFirstDay(TestConfig.timesheetHours);
        timesheetPage.addEmptyRow(TestConfig.timesheetProjectName,
                TestConfig.timesheetProjectOption, TestConfig.timesheetActivityB);
        Assert.assertTrue(timesheetPage.secondRowHoursAreBlank(), "Setup lỗi: bảy ô giờ của B phải còn trống");
        takeScreenshot("Issue1890 - A 01:00 and B blank before Save");
        timesheetPage.saveAndReload();

        // Assert
        String actual = timesheetPage.getFirstDayHours(TestConfig.timesheetActivityA);
        Log.info("REGRESSION | Expected A: 01:00 | Actual A sau refresh: " + actual);
        Assert.assertEquals(actual, TestConfig.timesheetHours, "Bug #1890: dòng B trống làm giờ của A không được lưu");
    }

    @AfterMethod(alwaysRun = true)
    public void resetFixtureHours() {
        if (!fixtureReady) return;
        Log.info("Reset fixture: bỏ B nếu đã lưu và trả A về 00:00; giữ fixture đã được phép");
        timesheetPage.resetToZero(TestConfig.timesheetProjectOption,
                TestConfig.timesheetActivityA, TestConfig.timesheetActivityB);
        Assert.assertTrue(timesheetPage.isZeroBaseline(TestConfig.timesheetProjectOption, TestConfig.timesheetActivityA),
                "Reset chưa thành công: fixture phải trở lại một dòng A, tổng 00:00");
        Log.info("Reset PASS: A = 00:00 | B không tồn tại | Fixture được giữ lại");
    }
}
