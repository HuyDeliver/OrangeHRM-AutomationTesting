package com.hrm.Pages.MyInfoPage;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.Log;
import com.hrm.Util.componentLocator;

public class DependentPage {
    private final WebDriver driver;

    public DependentPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isDependentVisible(String title) {
        return componentLocator.checkTitleH6(driver, title);
    }

    public void clickAdd(String title) {
        componentLocator.buttonAddMyInfo(driver, title);
    }

    public void fillDependents(String name, String relationship, String Date) {

        // Address
        componentLocator.fillInput(driver, "Name", name);
        componentLocator.chooseSelect(driver, "Relationship", relationship);
        componentLocator.fillInput(driver, "Date of Birth", Date);
    }

    public void clicksave() {
        componentLocator.buttonSave(driver);
        componentLocator.takeScreenshotResult(driver, "Add Dependent");
    }

    public boolean isCallToActionSuccess(String toast) {
        return componentLocator.checkToasstSuccess(driver, toast);
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

    public void DeleteDependent(String name) {

        componentLocator.deleteButtonTable(driver, name);
        componentLocator.takeScreenshotResult(driver, "Delete Dependent");
    }

}
