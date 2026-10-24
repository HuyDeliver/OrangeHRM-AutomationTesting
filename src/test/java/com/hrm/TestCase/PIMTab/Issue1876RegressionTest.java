package com.hrm.TestCase.PIMTab;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Util.Log;
import com.hrm.Util.componentLocator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;

@Epic("PIM tab")
@Feature("Issue #1876 regression")
public class Issue1876RegressionTest extends TestBase {
    // Regression test cho GitHub Issue #1876 (báo cáo trên OrangeHRM 5.7, tái hiện
    // trên bản local 5.9):
    // chọn "Past Employees Only" bằng bàn phím rồi nhấn TAB thì UI hiện Past nhưng
    // Search vẫn trả kết quả Current.
    private static final String PAST = "Past Employees Only";
    private static final By INCLUDE = select("Include");
    private static final By EMPLOYMENT_STATUS = select("Employment Status");
    private static final By RESULT_SUMMARY = By.xpath(
            "//div[contains(@class,'orangehrm-horizontal-padding')]/child::span");
    private WebDriverWait wait;

    // Tìm ô dropdown theo chữ nhãn: đi lên khối cha của nhãn, rồi xuống ô chọn bên
    // trong
    private static By select(String label) {
        return By.xpath("//label[normalize-space()='" + label + "']"
                + "/ancestor::div[contains(@class,'oxd-input-group')]"
                + "//div[contains(@class,'oxd-select-text-input')]");
    }

    @BeforeMethod
    public void prepareEmployeeList() {
        TestUtil.employeeUtil();
        driver.manage().timeouts().implicitlyWait(Duration.ZERO); // chỉ dùng chờ tường minh
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.visibilityOfElementLocated(INCLUDE));
        Log.info("Version: " + driver.findElement(By.cssSelector(".orangehrm-copyright")).getText());
    }

    @Issue("1876")
    @Test(description = "OHR41: Issue #1876 - TAB phải áp dụng bộ lọc Past giống ENTER")
    public void tabShouldApplyPastFilterLikeEnter() {
        // Arrange: baseline Current, và Past lấy bằng ENTER (cách xác nhận đã biết là
        // đúng)
        openEmployeeList();
        int currentCount = searchAndGetResultCount();
        int pastCountEnter = searchPast(Keys.ENTER);
        Log.info("Current=" + currentCount + " | Past (ENTER)=" + pastCountEnter);
        if (currentCount == pastCountEnter) {
            throw new SkipException("Current và Past cùng số lượng, chưa đủ dữ liệu để phân biệt lỗi filter");
        }

        // Act: cùng thao tác nhưng xác nhận bằng TAB
        int pastCountTab = searchPast(Keys.TAB);
        Log.info("Past (TAB)=" + pastCountTab);

        // Assert
        Assert.assertEquals(driver.findElement(INCLUDE).getText(), PAST, "UI phải giữ lựa chọn Past sau Search");
        Assert.assertEquals(pastCountTab, pastCountEnter,
                "Bug #1876: Include hiển thị Past nhưng kết quả sau TAB khác baseline ENTER");
    }

    private void openEmployeeList() {
        driver.navigate().refresh();
        Assert.assertEquals(wait.until(ExpectedConditions.visibilityOfElementLocated(INCLUDE)).getText(),
                "Current Employees Only", "Trang mới phải bắt đầu bằng Current Employees Only");
        wait.until(ExpectedConditions.visibilityOfElementLocated(RESULT_SUMMARY));
    }

    // Tải lại trang, chọn Past bằng bàn phím, xác nhận bằng phím cho trước
    // (ENTER/TAB), Search, trả số kết quả
    private int searchPast(Keys confirmKey) {
        openEmployeeList();
        WebElement employmentStatus = driver.findElement(EMPLOYMENT_STATUS);
        employmentStatus.click();
        employmentStatus.sendKeys(Keys.ESCAPE, Keys.TAB); // đóng danh sách, nhảy sang ô Include
        WebElement include = driver.findElement(INCLUDE);
        include.sendKeys(Keys.ARROW_DOWN, Keys.ARROW_DOWN, Keys.ARROW_DOWN);
        Assert.assertEquals(include.getText(), PAST, "Phải chọn đúng Past trước khi nhấn " + confirmKey);
        include.sendKeys(confirmKey);
        return searchAndGetResultCount();
    }

    private int searchAndGetResultCount() {
        WebElement previousResult = wait.until(ExpectedConditions.visibilityOfElementLocated(RESULT_SUMMARY));
        componentLocator.clickSearch(driver);
        // Đợi dòng kết quả cũ bị gỡ, tránh đọc nhầm số cũ trước khi kết quả mới tải
        // xong
        wait.until(ExpectedConditions.stalenessOf(previousResult));
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(RESULT_SUMMARY)).getText();
        return text.equals("No Records Found") ? 0 : Integer.parseInt(text.replaceAll("\\D", ""));
    }
}
