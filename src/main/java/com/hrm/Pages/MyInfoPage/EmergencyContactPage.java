package com.hrm.Pages.MyInfoPage;

import java.time.Duration;
import java.util.Map;
import com.hrm.Util.waitUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.Log;
import com.hrm.Util.componentLocator;

public class EmergencyContactPage {
    private final WebDriver driver;

    public EmergencyContactPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isEmergencyContactVisible(String title) {
        return componentLocator.checkTitleH6(driver, title);
    }

    public void clickAdd(String title) {
        componentLocator.buttonAddMyInfo(driver, title);
    }

    public void fillEmergencyContacts(String name, String relationship, String homePhone, String mobilePhone,
            String workPhone) {

        // Address
        componentLocator.fillInput(driver, "Name", name);
        componentLocator.fillInput(driver, "Relationship", relationship);
        componentLocator.fillInput(driver, "Home", homePhone);
        componentLocator.fillInput(driver, "Mobile", mobilePhone);
        componentLocator.fillInput(driver, "Work Telephone", workPhone);
    }

    public void clicksave() {
        componentLocator.buttonSave(driver);
        componentLocator.takeScreenshotResult(driver, "Add Emergency contact");
    }

    public boolean isCallToActionSuccess(String toast) {
        return componentLocator.checkToasstSuccess(driver, toast);
    }

    public boolean hasContact(String name) {
        driver.navigate().refresh();
        waitUtils.waitForTable(driver);
        Duration original = driver.manage().timeouts().getImplicitWaitTimeout();
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        try { return componentLocator.checkTable(driver, "Saved record", Map.of("Name", name)); }
        finally { driver.manage().timeouts().implicitlyWait(original); }
    }

    public boolean isToastDeleteVisible() {
        Log.info("Kiểm tra Delete thành công hay chưa");

        try {
            WebElement toastSuccess = new WebDriverWait(
                    driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".oxd-toast-content.oxd-toast-content--success")));
            return toastSuccess.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }

    }

    public void DeleteEmergencyContact(String name) {
        componentLocator.deleteButtonTable(driver, name);
        componentLocator.takeScreenshotResult(driver, "Delete Emergency contact");
    }

}
