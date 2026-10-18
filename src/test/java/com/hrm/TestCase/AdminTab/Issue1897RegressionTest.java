package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.PayGradePage;
import com.hrm.Pages.AdminPage.UserManagePage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("Module AdminTab")
@Feature("Issue #1897 - Pay Grades")
public class Issue1897RegressionTest extends TestBase {
    private PayGradePage payGradePage;
    private String gradeA;
    private String gradeB;

    @Test(description = "Issue #1897 - Sửa lương A không được làm thay đổi lương B")
    public void updatingOnePayGradeShouldNotChangeAnotherPayGrade() {
        // Arrange
        Log.info("Bước 1: Đăng nhập Admin và kiểm tra currency chưa được sử dụng");
        TestUtil.userUtil();
        payGradePage = new UserManagePage(driver).goToPayGradePage();
        String currency = TestConfig.payGradeTestCurrencyName;
        Assert.assertTrue(payGradePage.isCurrencyUnused(currency),
                "Setup dừng: currency đã được dùng hoặc danh sách chưa hiển thị đủ; cần chọn currency riêng");

        String suffix = String.valueOf(System.currentTimeMillis());
        gradeA = "ISS1897_A_" + suffix;
        gradeB = "ISS1897_B_" + suffix;
        Log.info("Bước 2: Tạo Pay Grade A: " + gradeA);
        payGradePage.addPayGrade(gradeA);
        payGradePage.addCurrency(TestConfig.payGradeTestCurrency,
                TestConfig.payGradeAMinimum, TestConfig.payGradeAMaximum);
        Assert.assertTrue(payGradePage.hasSalaryRange(currency, TestConfig.payGradeAMinimum,
                TestConfig.payGradeAMaximum), "Setup lỗi: khoảng lương A chưa đúng");

        Log.info("Bước 3: Tạo Pay Grade B cùng currency: " + gradeB);
        payGradePage.openPayGrades();
        payGradePage.addPayGrade(gradeB);
        payGradePage.addCurrency(TestConfig.payGradeTestCurrency,
                TestConfig.payGradeBMinimum, TestConfig.payGradeBMaximum);
        Assert.assertTrue(payGradePage.hasSalaryRange(currency, TestConfig.payGradeBMinimum,
                TestConfig.payGradeBMaximum), "Setup lỗi: khoảng lương B chưa đúng");
        String beforeB = payGradePage.getCurrencies();
        Log.info("Baseline B: " + beforeB);

        // Act
        Log.info("Bước 4: Chỉ sửa khoảng lương của A");
        payGradePage.openPayGrades();
        payGradePage.openPayGrade(gradeA);
        payGradePage.editCurrency(currency, TestConfig.payGradeANewMinimum, TestConfig.payGradeANewMaximum);
        payGradePage.reloadPayGrade();
        Assert.assertTrue(payGradePage.hasSalaryRange(currency, TestConfig.payGradeANewMinimum,
                TestConfig.payGradeANewMaximum), "Sửa A chưa thành công; chưa thể kiểm tra ảnh hưởng đến B");
        Log.info("A sau sửa: " + payGradePage.getCurrencies());

        // Assert
        Log.info("Bước 5: Mở lại B và so sánh với baseline");
        payGradePage.openPayGrades();
        payGradePage.openPayGrade(gradeB);
        String afterB = payGradePage.getCurrencies();
        Log.info("Expected B: " + beforeB + " | Actual B: " + afterB);
        Assert.assertEquals(afterB, beforeB, "Bug #1897: khoảng lương B bị thay đổi khi chỉ sửa A");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTestPayGrades() {
        if (payGradePage == null) return;
        try {
            if (gradeA != null) payGradePage.deletePayGradeIfPresent(gradeA);
        } finally {
            if (gradeB != null) payGradePage.deletePayGradeIfPresent(gradeB);
        }
    }
}
