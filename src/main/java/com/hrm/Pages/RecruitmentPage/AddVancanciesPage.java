package com.hrm.Pages.RecruitmentPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

public class AddVancanciesPage {
    private WebDriver driver;

    public AddVancanciesPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//h6[normalize-space(.)='Add Attachment']/following::button[@type='submit']")
    private WebElement addAttach;

    public boolean isAddVancanciesPageVisible(String title) {
        return componentLocator.checkTitleH6(driver, title);
    }

    public void addVancanciesFunction(String name, String optionJob, String description, String optionHiring,
            String position) {
        componentLocator.fillInput(driver, "Vacancy Name", name);
        componentLocator.chooseSelect(driver, "Job Title", optionJob);
        componentLocator.fillTexArea(driver, "Description", description);
        String keyName = optionHiring.trim().split("\\s+")[0];
        componentLocator.inputDropdown(driver, "Hiring Manager", keyName, optionHiring);
        componentLocator.fillInput(driver, "Number of Positions", position);
    }

    public void clickSave() {
        componentLocator.buttonSave(driver);
    }

    public void addAttachment(String file, String comment, String title) {
        componentLocator.buttonAddMyInfo(driver, title);
        componentLocator.fillFile(driver, "Select File", file);
        componentLocator.fillTexArea(driver, "Comment", comment);
        waitUtils.waitElementClick(driver, addAttach);
    }

    public boolean isAddvancanciesSuccess() {
        return componentLocator.checkToasstSuccess(driver, "Add vancancies");
    }
}
