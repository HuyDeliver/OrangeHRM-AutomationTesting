package com.hrm.Pages.MyInfoPage;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.Log;

import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

public class PersonalDetailPage {
    private final WebDriver driver;

    public PersonalDetailPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".oxd-calendar-wrapper")
    private WebElement calendarDropdown;

    @FindBy(xpath = "//label[text()='Nationality']/following::div[contains(@class,'oxd-select-text')]")
    private WebElement nationalityInput;

    public boolean isPersonalDetailVisible(String title) {
        return componentLocator.checkTitleH6(driver, title);
    }

    public void fillNewInfo(String otherId, String licenseNo, String licenseExpiryDate, String nationality,
            String maritalStatus, String dateOfBirth, String gender) {

        componentLocator.fillInput(driver, "Other Id", otherId);
        componentLocator.fillInput(driver, "License Number", licenseNo);
        componentLocator.fillInput(driver, "License Expiry Date", licenseExpiryDate);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(nationalityInput));
        componentLocator.chooseSelectAction(driver, "Nationality", nationality);
        componentLocator.chooseSelectAction(driver, "Marital Status", maritalStatus);
        componentLocator.fillInput(driver, "Date of Birth", dateOfBirth);

        Log.info("Chọn gender: " + gender);
        WebElement label = driver.findElement(
                By.xpath("//label[contains(normalize-space(),'" + gender + "')]"));
        waitUtils.waitElementClick(driver, label);
    }

    public void clicksave() {
        componentLocator.buttonSave(driver);
        componentLocator.takeScreenshotResult(driver, "Add personal detail");
    }

    public boolean isAddPersonalDetailSuccess(String toast) {
        return componentLocator.checkToasstSuccess(driver, toast);
    }

    public ContactDetailPage goToContactDetailpage(String tab) {
        componentLocator.clickSideBarMyInfo(driver, tab);
        return new ContactDetailPage(driver);
    }

    public EmergencyContactPage goToEmergencyContactPage(String tab) {
        componentLocator.clickSideBarMyInfo(driver, tab);
        return new EmergencyContactPage(driver);
    }

}
