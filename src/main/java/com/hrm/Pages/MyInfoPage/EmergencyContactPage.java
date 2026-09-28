package com.hrm.Pages.MyInfoPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

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

    public boolean isAddEmergencyContactSuccess(String toast) {
        return componentLocator.checkToasstSuccess(driver, toast);
    }

}
