package com.hrm.Pages.RecruitmentPage;

import java.util.Map;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.hrm.Util.waitUtils;

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
        By summary = By.xpath("//div[contains(@class,'orangehrm-horizontal-padding')]/child::span");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement old = wait.until(ExpectedConditions.visibilityOfElementLocated(summary));
        componentLocator.clickSearch(driver);
        wait.until(ExpectedConditions.stalenessOf(old));
        wait.until(ExpectedConditions.visibilityOfElementLocated(summary));
        waitUtils.waitForTable(driver);
    }

    public int countVacancies(String name) {
        return componentLocator.countTableRows(driver, name);
    }

    public void openVacancy(String name) {
        componentLocator.editButtonTable(driver, name);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[normalize-space()='Edit Vacancy']")));
    }

    public boolean checkSearch(String job, String vacancy, String manager, String status, boolean expectedFound) {
        if (!expectedFound) {
            return componentLocator.checkNoRecordFound(driver);
        }
        return componentLocator.checkTable(driver, status,
                Map.of("Job Title", job, "Vacancies", vacancy, "Hiring Manager", manager, "Status", status));
    }

    public boolean isDeleteVancanciesSuccess(String toast) {
        return componentLocator.checkToasstSuccess(driver, toast);
    }
}
