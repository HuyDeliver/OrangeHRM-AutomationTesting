package com.hrm.Pages.AdminPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.componentLocator;

public class EditSkillPage {
    private WebDriver driver;

    public EditSkillPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isEditPageVisible() {
        return componentLocator.checkTitle(driver, "Edit Skill");
    }

    public void editSkill(String name, String description) {
        componentLocator.fillInput(driver, "Name", name);
        componentLocator.fillTexArea(driver, "Description", description);
        componentLocator.buttonSave(driver);
    }

    public boolean isEditSkillSuccess() {
        return componentLocator.checkToasstSuccess(driver, "Edit Skill");
    }
}
