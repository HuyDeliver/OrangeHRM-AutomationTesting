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
        return componentLocator.checkTitle(driver, "Skills");
    }

    public AddSkillPage goToAddSkillPage() {
        componentLocator.clickButtonAdd(driver);
        return new AddSkillPage(driver);
    }

    public EditSkillPage goToEditSkillPage() {
        componentLocator.editButtonTable(driver);
        return new EditSkillPage(driver);
    }

    public void deleteSkill() {
        componentLocator.deleteButtonTable(driver);
    }

    public boolean isDeleteSkillSuccess() {
        return componentLocator.checkToasstSuccess(driver, "Delete Skill");
    }

}
