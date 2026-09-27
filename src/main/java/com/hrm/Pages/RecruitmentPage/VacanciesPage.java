package com.hrm.Pages.RecruitmentPage;

import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.hrm.Util.componentLocator;

public class VacanciesPage {
    private final WebDriver driver;

    public VacanciesPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isVacanciesPageVisible(String title) {
        return componentLocator.checkTitleH5(driver, title);
    }

    public AddVancanciesPage openAddVancancies() {
        componentLocator.clickButtonAdd(driver);
        return new AddVancanciesPage(driver);
    }

    public void deleteVancancies(String vancancies) {
        componentLocator.deleteButtonTable(driver, vancancies);
    }

    public void searchVacancy(String job, String vacancy, String manager, String status) {
        if (!job.isEmpty()) {
            componentLocator.chooseSelectAction(driver, "Job Title", job);
        }

        if (!vacancy.isEmpty()) {
            componentLocator.chooseSelectAction(driver, "Vacancy", vacancy);
        }

        if (!manager.isEmpty()) {
            componentLocator.chooseSelectAction(driver, "Hiring Manager", manager);
        }

        if (!status.isEmpty()) {
            componentLocator.chooseSelectAction(driver, "Status", status);
        }
    }

    public void clickSearch() {
        componentLocator.clickSearch(driver);
    }

    public boolean checkSearch(String job, String vacancy, String manager, String status) {
        return componentLocator.checkSearchAdvanced(driver, status,
                Map.of("Job Title", job, "Vacancies", vacancy, "Hiring Manager", manager, "Status", status));
    }

    public boolean isDeleteVancanciesSuccess(String toast) {
        return componentLocator.checkToasstSuccess(driver, toast);
    }
}
