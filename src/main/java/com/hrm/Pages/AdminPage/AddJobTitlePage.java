package com.hrm.Pages.AdminPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.Log;
import com.hrm.Util.componentLocator;

public class AddJobTitlePage {
    private WebDriver driver;

    public AddJobTitlePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".orangehrm-card-container>h6")
    private WebElement titleAddJobTitle;

    public boolean isTitleAddJobTitleVisible() {
        Log.info("Kiểm tra vào được Add job");
        return titleAddJobTitle.isDisplayed();
    }

    public void addTitleJob(String title, String description, String file, String note) {
        componentLocator.fillInput(driver, "Job Title", title);
        componentLocator.fillTexArea(driver, "Job Description", description);
        componentLocator.fillFile(driver, "Job Specification", file);
        componentLocator.fillTexArea(driver, "Note", note);

        componentLocator.buttonSave(driver);
    }

    public boolean isAddTitleJobSuccess() {
        return componentLocator.checkToasstSuccess(driver, "Add Job title");
    }
}
