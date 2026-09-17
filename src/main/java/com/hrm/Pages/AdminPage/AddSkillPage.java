package com.hrm.Pages.AdminPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.componentLocator;

public class AddSkillPage {
    private WebDriver driver;

    public AddSkillPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isAddSkillPageVisible() {
        return componentLocator.checkTitle(driver, "Add Skill");
    }

    public void addSkillFunction(String name, String description) {
        componentLocator.fillInput(driver, "Name", name);
        componentLocator.fillTexArea(driver, "Description", description);
        componentLocator.buttonSave(driver);
    }

    public boolean isAddSkillSuccess() {
        return componentLocator.checkToasstSuccess(driver, "Add Skill");
    }
}
