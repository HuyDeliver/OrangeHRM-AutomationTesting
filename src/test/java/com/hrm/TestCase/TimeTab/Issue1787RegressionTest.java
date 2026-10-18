package com.hrm.TestCase.TimeTab;

import java.time.Duration;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.TimePage.AttendancePage;
import com.hrm.Pages.TimePage.TimesheetPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("Time")
@Feature("Attendance - Calendar")
public class Issue1787RegressionTest extends TestBase {

    @Test(description = "Lịch Attendance bắt đầu Monday theo mô tả issue 1787")
    public void attendanceCalendarShouldStartOnMonday() {
        // Arrange
        Log.info("Bước 1: Đăng nhập và mở Time bằng TestUtil");
        TestUtil.timeUtil();
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);

        Log.info("Bước 2: Xác nhận Timesheet hiện bắt đầu Monday");
        TimesheetPage timesheetPage = new TimesheetPage(driver);
        timesheetPage.openMyTimesheet(TestConfig.timesheetStartDate);
        String firstWeekday = timesheetPage.getFirstWeekday();
        Log.info("Timesheet period: " + timesheetPage.getPeriod() + " | Ngày đầu: " + firstWeekday);
        Assert.assertEquals(firstWeekday, "Mon", "Setup lỗi: Timesheet phải bắt đầu Monday để kiểm tra issue 1787");
        takeScreenshot("Timesheet starts Monday");

        // Act
        Log.info("Bước 3: Mở Attendance > My Records > lịch ở ô Date");
        AttendancePage attendancePage = new AttendancePage(driver);
        attendancePage.openMyRecords();
        attendancePage.openDatePicker();

        // Assert
        List<String> weekdays = attendancePage.getWeekdays();
        Log.info("Bước 4: Đọc các thứ trên lịch: " + weekdays);
        Assert.assertEquals(weekdays.size(), 7, "Không đọc được đủ bảy thứ trên lịch");
        Log.info("Expected theo issue 1787: M (Monday) | Actual: " + weekdays.get(0));
        Log.info("Giới hạn: chưa xác nhận cấu hình Timesheet phải áp dụng cho lịch Attendance");
        takeScreenshot("Attendance calendar weekdays");
        Assert.assertEquals(weekdays.get(0), "M",
                "Tái hiện khác biệt trong issue 1787: lịch không bắt đầu Monday; cần xác nhận phạm vi yêu cầu");
    }
}
