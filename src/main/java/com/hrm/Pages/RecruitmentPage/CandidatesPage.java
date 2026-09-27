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

    public VacanciesPage goToVacanciesPage(String navi, String option) {
        componentLocator.clickDropdownNavi(driver, navi, option);
        return new VacanciesPage(driver);
    }

}
