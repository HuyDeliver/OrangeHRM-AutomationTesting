package com.hrm.Pages.AdminPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.componentLocator;

public class SkillPage {
    private WebDriver driver;

    public SkillPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isSkillTitleVisible() {
        return componentLocator.checkTitleH6(driver, "Skills");
    }

    public AddSkillPage goToAddSkillPage() {
        componentLocator.clickButtonAdd(driver);
        return new AddSkillPage(driver);
    }

    public EditSkillPage goToEditSkillPage(String skill) {
        componentLocator.editButtonTable(driver, skill);
        return new EditSkillPage(driver);
    }

    public void deleteSkill(String skill) {
        componentLocator.deleteButtonTable(driver, skill);
    }

    public boolean isDeleteSkillSuccess() {
        return componentLocator.checkToasstSuccess(driver, "Delete Skill");
    }

}
