package com.hrm.Pages.MyInfoPage;

import java.time.Duration;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.Log;
import com.hrm.Util.componentLocator;

public class QualificationPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public QualificationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    public boolean isQualificationVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h6[normalize-space()='Education']"))).isDisplayed();
    }

    public void clickAdd(String title) {
        componentLocator.buttonAddMyInfo(driver, title);
    }

    public void fillEducation(String level, String institute, String score) {
        componentLocator.chooseSelectAction(driver, "Level", level);
        componentLocator.fillInput(driver, "Institute", institute);
        componentLocator.fillInput(driver, "GPA/Score", score);
    }

    public void clicksave() {
        componentLocator.buttonSave(driver);
    }

    public boolean isCallToActionSuccess(String toast) {
        return componentLocator.checkToasstSuccess(driver, toast);
    }

    public boolean isEducationVisible(String score) {
        return componentLocator.checkTable(driver, "Education", Map.of("score", score));
    }

    public void waitForEducation(String score) {
        wait.until(d -> isEducationVisible(score));
    }

    public void deleteEducation(String score) {
        // Không để toast Save cũ bị nhầm với kết quả Delete.
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".oxd-toast--success")));
        componentLocator.deleteButtonTable(driver, score);
        String toast = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".oxd-toast-container--toast"))).getText();
        Log.info("Kết quả Delete trên UI: " + toast);
    }

    public void reloadQualification() {
        driver.navigate().refresh();
        isQualificationVisible();
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector(".oxd-loading-spinner"), 0));
    }
}
