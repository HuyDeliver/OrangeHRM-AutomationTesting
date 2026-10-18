package com.hrm.Pages.AdminPage;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.Log;
import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

public class PayGradePage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final By EDIT_GRADE = By.xpath("//h6[normalize-space()='Edit Pay Grade']");
    private static final By CURRENCY_FORM = By.xpath(
            "//h6[normalize-space()='Add Currency' or normalize-space()='Edit Currency']");

    public PayGradePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    public void openPayGrades() {
        componentLocator.clickDropdownNavi(driver, "Job", "Pay Grades");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[normalize-space()='Pay Grades']")));
        waitForLoading();
    }

    public boolean isCurrencyUnused(String currencyName) {
        String count = driver.findElement(By.xpath(
                "//div[contains(@class,'orangehrm-horizontal-padding')]/child::span")).getText().replaceAll("\\D", "");
        int total = count.isEmpty() ? 0 : Integer.parseInt(count);
        int visible = driver.findElements(By.cssSelector(".oxd-table-card")).size();
        Log.info("Kiểm tra toàn bộ Pay Grade: " + visible + "/" + total);
        return visible == total
                && !componentLocator.checkTable(driver, "Currency đã được dùng", Map.of("currency", currencyName));
    }

    public void addPayGrade(String name) {
        componentLocator.clickButtonAdd(driver);
        componentLocator.fillInput(driver, "Name", name);
        componentLocator.buttonSave(driver);
        wait.until(ExpectedConditions.visibilityOfElementLocated(EDIT_GRADE));
    }

    public void openPayGrade(String name) {
        componentLocator.editButtonTable(driver, name);
        wait.until(ExpectedConditions.visibilityOfElementLocated(EDIT_GRADE));
        waitForLoading();
    }

    public void addCurrency(String currency, String minimum, String maximum) {
        componentLocator.buttonAddMyInfo(driver, "Currencies");
        componentLocator.chooseSelectAction(driver, "Currency", currency);
        fillSalary(minimum, maximum);
        saveCurrency();
    }

    public void editCurrency(String currencyName, String minimum, String maximum) {
        componentLocator.editButtonTable(driver, currencyName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(CURRENCY_FORM));
        fillSalary(minimum, maximum);
        saveCurrency();
    }

    private void fillSalary(String minimum, String maximum) {
        componentLocator.fillInput(driver, "Minimum Salary", minimum);
        componentLocator.fillInput(driver, "Maximum Salary", maximum);
    }

    private void saveCurrency() {
        // buttonSave() chọn Save của form Name; ở đây cần Save của form Currency.
        By save = By.xpath("//h6[normalize-space()='Add Currency' or normalize-space()='Edit Currency']"
                + "/ancestor::div[contains(@class,'orangehrm-card-container')]//button[@type='submit']");
        waitUtils.waitElementClick(driver, driver.findElement(save));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(CURRENCY_FORM));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".oxd-table-body .oxd-table-card")));
    }

    public String getCurrencies() {
        return driver.findElement(By.cssSelector(".oxd-table-body")).getText();
    }

    public boolean hasSalaryRange(String currencyName, String minimum, String maximum) {
        // Bảng hiển thị 1,000.00 thay vì 1000.
        String minText = String.format(Locale.US, "%,.2f", Double.parseDouble(minimum));
        String maxText = String.format(Locale.US, "%,.2f", Double.parseDouble(maximum));
        return componentLocator.checkTable(driver, "Khoảng lương", Map.of(
                "currency", currencyName, "minimum", minText, "maximum", maxText));
    }

    public void reloadPayGrade() {
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(EDIT_GRADE));
        waitForLoading();
    }

    public void deletePayGradeIfPresent(String name) {
        openPayGrades();
        if (componentLocator.checkTable(driver, "Pay Grade test", Map.of("name", name))) {
            componentLocator.deleteButtonTable(driver, name);
            wait.until(d -> d.findElements(By.cssSelector(".oxd-loading-spinner")).isEmpty()
                    && !componentLocator.checkTable(d, "Cleanup Pay Grade", Map.of("name", name)));
            Log.info("Cleanup: đã xóa " + name);
        }
    }

    private void waitForLoading() {
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector(".oxd-loading-spinner"), 0));
    }
}
