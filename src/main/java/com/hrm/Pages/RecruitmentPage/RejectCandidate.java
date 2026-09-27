package com.hrm.Pages.RecruitmentPage;

import org.openqa.selenium.WebDriver;

import com.hrm.Util.componentLocator;

public class RejectCandidate {
    private WebDriver driver;

    public RejectCandidate(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isRejectPagePageVisible(String title) {
        return componentLocator.checkTitleH6(driver, title);
    }

    public void fillNote(String note) {
        componentLocator.fillTexArea(driver, "Notes", note);
        componentLocator.buttonSave(driver);
    }

    public boolean isRejectSuccess(String toast) {
        return componentLocator.checkToasstSuccess(driver, toast);
    }
}
