package com.hrm.Pages.AdminPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.componentLocator;

public class EditLocationPage {
    private WebDriver driver;

    public EditLocationPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isTitleEditVisible() {
        return componentLocator.checkTitle(driver, "Edit Location");
    }

    public void editCity(String value) {
        componentLocator.fillInput(driver, "City", value);
        componentLocator.buttonSave(driver);
    }

    public boolean isEditLocationSuccess() {
        return componentLocator.checkToasstSuccess(driver, "Edit Location");
    }
}
