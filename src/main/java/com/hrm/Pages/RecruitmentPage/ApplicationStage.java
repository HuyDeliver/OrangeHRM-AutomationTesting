package com.hrm.Pages.RecruitmentPage;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

public class ApplicationStage {
    private WebDriver driver;
    private final By status = By.xpath("//p[starts-with(normalize-space(),'Status:')]");

    public ApplicationStage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".orangehrm-recruitment-actions .oxd-button--danger")
    private WebElement rejectButton;

    @FindBy(xpath = "//button[normalize-space()='Shortlist']")
    private WebElement shortlistButton;

    public boolean isApplicationPagePageVisible(String title) {
        return componentLocator.checkTitleH6(driver, title);
    }

    public RejectCandidate goToRejectCandidate() {
        waitUtils.waitElementClick(driver, rejectButton);
        return new RejectCandidate(driver);
    }

    public CandidatesPage goToCandidatesPage(String navi, String option) {
        componentLocator.clickDropdownNavi(driver, navi, option);
        componentLocator.takeScreenshotTable(driver, "Reject Candidate");
        return new CandidatesPage(driver);
    }

    public void waitForApplicationStage() {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(status));
    }

    public void shortlistCandidate(String note) {
        waitUtils.waitElementClick(driver, shortlistButton);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h6[normalize-space()='Shortlist Candidate']")));
        componentLocator.fillTexArea(driver, "Notes", note);
        componentLocator.buttonSave(driver);
        // Dừng chờ khi lưu xong hoặc khi UI báo lỗi; không chỉ dựa vào toast success.
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(status),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".oxd-toast--error"))));
    }

    public String getCandidateStatus() {
        List<WebElement> labels = driver.findElements(status);
        return labels.isEmpty() ? "Không có trạng thái hồ sơ" : labels.get(0).getText();
    }

    public String getPageText() {
        return driver.findElement(By.tagName("body")).getText();
    }

    public void reloadApplicationStage() {
        driver.navigate().refresh();
        waitForApplicationStage();
    }

    public boolean hasShortlistHistory() {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(5)).until(d ->
                    componentLocator.checkTable(d, "History Shortlist", Map.of("action", "Shortlisted")));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean hasRejectHistory() {
        // History dùng chữ "rejected", khác với nhãn trạng thái "Rejected".
        return componentLocator.checkTable(driver, "History Reject", Map.of("action", "rejected"));
    }
}
