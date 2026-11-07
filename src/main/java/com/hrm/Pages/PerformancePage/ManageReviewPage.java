package com.hrm.Pages.PerformancePage;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

public class ManageReviewPage {
    private WebDriver driver;

    public ManageReviewPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isEmployeeReviewsVisible(String title) {
        return componentLocator.checkTitleH5(driver, title);
    }

    public void clickManageReview(String tab, String option) {
        componentLocator.clickDropdownNavi(driver, tab, option);
    }

    public AddReviewPage clickButtonAdd() {
        componentLocator.clickButtonAdd(driver);
        return new AddReviewPage(driver);
    }

    public WriteReviewPage goToWriteReviewPage(String name) {
        componentLocator.evaluateButtonTable(driver, name);
        return new WriteReviewPage(driver);
    }

    public boolean isWrietReviewSuccess(String status, String name) {
        return componentLocator.checkTable(driver, "Check after wirte review", Map.of("Name", name, "Status", status));
    }

    public void deleteReview(String date) {
        componentLocator.deleteButtonTable(driver, date);
    }

    public boolean isDeleteReviewSuccess() {
        return componentLocator.checkToasstSuccess(driver, "Delete");
    }

    public int countReviews(String value) {
        return componentLocator.countTableRows(driver, value);
    }

    public void openKpiPage() {
        componentLocator.clickDropdownNavi(driver, "Configure", "KPIs");
    }

    public int countKpis(String kpiTitle) {
        waitUtils.waitForTable(driver);
        java.time.Duration original = driver.manage().timeouts().getImplicitWaitTimeout();
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ZERO);
        try {
            java.util.List<org.openqa.selenium.WebElement> rows = driver.findElements(org.openqa.selenium.By.cssSelector(".oxd-table-card"));
            int matches = 0;
            for (org.openqa.selenium.WebElement row : rows) {
                java.util.List<org.openqa.selenium.WebElement> cells = row.findElements(org.openqa.selenium.By.cssSelector(".oxd-table-cell"));
                if (cells.size() > 1 && cells.get(1).getText().trim().equals(kpiTitle)) {
                    matches++;
                }
            }
            return matches;
        } finally {
            driver.manage().timeouts().implicitlyWait(original);
        }
    }

    public boolean addKpi(String kpiTitle, String jobTitle, String minRating, String maxRating) {
        componentLocator.clickButtonAdd(driver);
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(
                        org.openqa.selenium.By.xpath("//label[normalize-space()='Key Performance Indicator']")));
        componentLocator.fillInput(driver, "Key Performance Indicator", kpiTitle);
        componentLocator.chooseSelectAction(driver, "Job Title", jobTitle);
        componentLocator.fillInput(driver, "Minimum Rating", minRating);
        componentLocator.fillInput(driver, "Maximum Rating", maxRating);
        componentLocator.buttonSave(driver);
        boolean ok = componentLocator.checkToasstSuccess(driver, "Success");
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("searchKpi"));
        return ok;
    }
}
