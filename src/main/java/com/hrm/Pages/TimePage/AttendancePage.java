package com.hrm.Pages.TimePage;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

public class AttendancePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public AttendancePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public void openMyRecords() {
        componentLocator.clickDropdownNavi(driver, "Attendance", "My Records");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h5[normalize-space()='My Attendance Records']")));
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector(".oxd-loading-spinner"), 0));
    }

    public void openDatePicker() {
        waitUtils.waitElementClick(driver, driver.findElement(By.cssSelector(".oxd-date-input-icon")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".oxd-calendar-wrapper")));
    }

    public List<String> getWeekdays() {
        return driver.findElements(By.cssSelector(".oxd-calendar-week-grid .oxd-calendar-day"))
                .stream().map(WebElement::getText).toList();
    }
}
