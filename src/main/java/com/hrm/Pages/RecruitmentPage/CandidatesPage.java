package com.hrm.Pages.RecruitmentPage;

import org.openqa.selenium.WebDriver;

import com.hrm.Util.componentLocator;

public class CandidatesPage {
    private WebDriver driver;

    public CandidatesPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isCandidatePageVisisble(String title) {
        return componentLocator.checkTitleH5(driver, title);
    }

    public AddCandidatePage openAddCandidatePage() {
        componentLocator.clickButtonAdd(driver);
        return new AddCandidatePage(driver);
    }

    public VacanciesPage goToVacanciesPage(String navi, String option) {
        componentLocator.clickDropdownNavi(driver, navi, option);
        return new VacanciesPage(driver);
    }

    public ApplicationStage goToApplicationStage(String candidate) {
        componentLocator.viewButtonTable(driver, candidate);
        return new ApplicationStage(driver);
    }

    public void deleteCandidate(String candidate) {
        componentLocator.deleteButtonTable(driver, candidate);
    }

    public boolean isDeleteCandidateSuccess(String toast) {
        return componentLocator.checkToasstSuccess(driver, toast);
    }
}
