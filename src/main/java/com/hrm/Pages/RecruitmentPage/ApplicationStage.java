package com.hrm.Pages.RecruitmentPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

public class ApplicationStage {
    private WebDriver driver;

    public ApplicationStage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".orangehrm-recruitment-actions .oxd-button--danger")
    private WebElement rejectButton;

    public boolean isApplicationPagePageVisible(String title) {
        return componentLocator.checkTitleH6(driver, title);
    }

    public RejectCandidate goToRejectCandidate() {
        waitUtils.waitElementClick(driver, rejectButton);
        return new RejectCandidate(driver);
    }

    public CandidatesPage goToCandidatesPage(String navi, String option) {
        componentLocator.clickDropdownNavi(driver, navi, option);
        return new CandidatesPage(driver);
    }
}
