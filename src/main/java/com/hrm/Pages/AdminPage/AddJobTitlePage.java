package com.hrm.Pages.AdminPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.Log;
import com.hrm.Util.waitUtils;

public class AddJobTitlePage {
    private WebDriver driver;

    public AddJobTitlePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".orangehrm-card-container>h6")
    private WebElement titleAddJobTitle;

    @FindBy(xpath = "//label[text()='Job Title']/following::input[1]")
    private WebElement jobTitleName;

    @FindBy(xpath = "//label[text()='Job Description']/following::textarea[1]")
    private WebElement jobDescription;

    @FindBy(xpath = "//input[@type='file']")
    private WebElement jobSpecification;

    @FindBy(xpath = "//label[text()='Note']/following::textarea[1]")
    private WebElement jobNote;

    @FindBy(xpath = "//div[@class='oxd-form-actions']/child::button[@type='submit']")
    private WebElement buttonSave;

    @FindBy(css = ".oxd-toast.oxd-toast--success.oxd-toast-container--toast")
    private WebElement toastAddSuccess;

    public boolean isTitleAddJobTitleVisible() {
        Log.info("Kiểm tra vào được Add job");
        return titleAddJobTitle.isDisplayed();
    }

    public void addTitleJob(String title, String description, String file, String note) {
        Log.info("Nhập job title");
        jobTitleName.sendKeys(title);
        Log.info("Nhập job description");
        jobDescription.sendKeys(description);
        Log.info("Nhập job specification");
        jobSpecification.sendKeys(file);
        Log.info("Nhập note");
        jobNote.sendKeys(note);
        Log.info("Nhấn save");
        waitUtils.waitElementClick(driver, buttonSave);
    }

    public boolean isAddTitleJobSuccess() {
        Log.info("Kiểm tra Add thành công chưa");
        return toastAddSuccess.isDisplayed();
    }
}
