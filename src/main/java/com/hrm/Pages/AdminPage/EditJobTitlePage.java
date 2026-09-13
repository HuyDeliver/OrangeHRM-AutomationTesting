package com.hrm.Pages.AdminPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.componentLocator;

public class EditJobTitlePage {
    private WebDriver driver;

    public EditJobTitlePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isTitleEditJobVisible() {
        return componentLocator.checkTitle(driver, "Edit Job Title");
    }

    public void editJobTitle(String jobTitle) {
        componentLocator.fillInput(driver, "Job Title", jobTitle);
        componentLocator.buttonSave(driver);
    }

    public boolean isEditJobTitleSuccess() {
        return componentLocator.checkToasstSuccess(driver, "Edit Job");
    }

}
