package com.hrm.Pages.RecruitmentPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.Log;
import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

public class AddCandidatePage {
    private WebDriver driver;

    public AddCandidatePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//div[contains(@class,'oxd-input-group')]/following::input[@name='firstName']")
    private WebElement firstName;
    @FindBy(xpath = "//div[contains(@class,'oxd-input-group')]/following::input[@name='middleName']")
    private WebElement middleName;
    @FindBy(xpath = "//div[contains(@class,'oxd-input-group')]/following::input[@name='lastName']")
    private WebElement lastName;

    @FindBy(xpath = "//div[@class='oxd-checkbox-wrapper']/descendant::span[contains(@class,'oxd-checkbox-input')]")
    private WebElement keepCheckbox;

    public boolean isAddCandidatePageVisible(String title) {
        return componentLocator.checkTitleH6(driver, title);
    }

    public void addCandidateFunction(
            String firstName,
            String middleName,
            String lastName,
            String vacancy,
            String email,
            String phone,
            String resume,
            String keyword,
            String date,
            String note) {

        Log.info("Nhập firstname: " + firstName);
        this.firstName.sendKeys(firstName);
        Log.info("Nhập firstname: " + middleName);
        this.middleName.sendKeys(middleName);
        Log.info("Nhập firstname: " + lastName);
        this.lastName.sendKeys(lastName);

        componentLocator.chooseSelectAction(driver, "Vacancy", vacancy);

        componentLocator.fillInput(driver, "Email", email);
        componentLocator.fillInput(driver, "Contact Number", phone);
        componentLocator.fillFile(driver, "Resume", resume);
        componentLocator.fillInput(driver, "Keywords", keyword);
        componentLocator.fillInput(driver, "Date of Application", date);
        componentLocator.fillTexArea(driver, "Notes", note);

        waitUtils.waitElementClick(driver, keepCheckbox);

    }

    public ApplicationStage clickSave() {
        componentLocator.buttonSave(driver);
        return new ApplicationStage(driver);
    }

    public boolean isAddCandidateSuccess() {
        return componentLocator.checkToasstSuccess(driver, "Add Candidate");
    }
}
