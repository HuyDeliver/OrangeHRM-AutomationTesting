package com.hrm.Pages.RecruitmentPage;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.componentLocator;

public class VacanciesPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public VacanciesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open() {
        By vacanciesTab = By.xpath("//a[normalize-space()='Vacancies']");
        wait.until(ExpectedConditions.elementToBeClickable(vacanciesTab)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".oxd-text.oxd-text--h5.oxd-table-filter-title")));
    }

    public boolean isOpen() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".oxd-text.oxd-text--h5.oxd-table-filter-title"))).isDisplayed();
    }

    public void goToAddVacancy() {
        componentLocator.clickButtonAdd(driver);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h6[contains(normalize-space(),'Vacancy')]") ));
    }

    public void search(String vacancyName) {
        selectFilterOption("Vacancy", vacancyName);
        componentLocator.clickSearch(driver);
    }

    public boolean hasVacancy(String vacancyName) {
        return wait.until(d -> d.findElements(By.cssSelector(".oxd-table-card")).stream()
                .anyMatch(row -> row.isDisplayed() && row.getText().contains(vacancyName)));
    }

    public void edit(String vacancyName) {
        componentLocator.editButtonTable(driver, vacancyName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h6[contains(normalize-space(),'Vacancy') and contains(normalize-space(),'Edit')]") ));
    }

    private void selectFilterOption(String label, String option) {
        By dropdown = By.xpath("//label[normalize-space()='" + label
                + "']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text')]");
        wait.until(ExpectedConditions.elementToBeClickable(dropdown)).click();

        By optionLocator = By.xpath("//div[contains(@class,'oxd-select-option')]/span[normalize-space()='"
                + option + "']");
        wait.until(ExpectedConditions.elementToBeClickable(optionLocator)).click();
    }
}
