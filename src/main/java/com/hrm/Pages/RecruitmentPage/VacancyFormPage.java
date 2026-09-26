package com.hrm.Pages.RecruitmentPage;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.TestConfig;
import com.hrm.Util.componentLocator;

public class VacancyFormPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public VacancyFormPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void addVacancy(String vacancyName) {
        componentLocator.fillInput(driver, "Vacancy Name", vacancyName);
        chooseFirstOption("Job Title");
        componentLocator.fillTexArea(driver, "Description", "Created by automated UI test " + vacancyName);
        chooseHiringManager(TestConfig.employeeName);
        componentLocator.fillInput(driver, "Number of Positions", "1");
        componentLocator.buttonSave(driver);
    }

    public void renameVacancy(String newName) {
        componentLocator.fillInput(driver, "Vacancy Name", newName);
        componentLocator.buttonSave(driver);
    }

    public boolean isSaveSuccessful() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".oxd-toast.oxd-toast--success.oxd-toast-container--toast"))).isDisplayed();
    }

    private void chooseFirstOption(String label) {
        By dropdown = By.xpath("//label[normalize-space()='" + label
                + "']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text')]");
        wait.until(ExpectedConditions.elementToBeClickable(dropdown)).click();

        By optionsLocator = By.cssSelector(".oxd-select-option");
        wait.until(ExpectedConditions.visibilityOfElementLocated(optionsLocator));
        List<WebElement> options = driver.findElements(optionsLocator);
        WebElement option = options.stream()
                .filter(WebElement::isDisplayed)
                .filter(item -> !item.getText().trim().equalsIgnoreCase("-- Select --"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Không có Job Title nào để chọn"));
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    private void chooseHiringManager(String keyword) {
        By managerInput = By.xpath("//label[normalize-space()='Hiring Manager']"
                + "/ancestor::div[contains(@class,'oxd-input-group')]//input");
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(managerInput));
        input.sendKeys(keyword);

        WebElement firstMatch = wait.until(d -> d.findElements(
                        By.cssSelector(".oxd-autocomplete-dropdown > div")).stream()
                .filter(WebElement::isDisplayed)
                .filter(option -> !option.getText().contains("Searching"))
                .filter(option -> !option.getText().contains("No Records Found"))
                .findFirst().orElse(null));
        wait.until(ExpectedConditions.elementToBeClickable(firstMatch)).click();
    }
}
