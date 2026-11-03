package com.hrm.Pages.RecruitmentPage;

import java.time.Duration;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.Log;
import org.testng.Assert;
import com.hrm.Util.waitUtils;
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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".orangehrm-paper-container .orangehrm-header-container button")));
        componentLocator.clickButtonAdd(driver);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h6[normalize-space()='Add Candidate']")));
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

    public int countCandidates(String fullName) {
        return componentLocator.countTableRows(driver, fullName);
    }

    public void removeCandidateIfPresent(String fullName) {
        componentLocator.clickDropdownNavi(driver, "Candidates", "");
        int count = countCandidates(fullName);
        Assert.assertTrue(count <= 1, "Cleanup: nhiều candidate trùng tên " + fullName);
        if (count == 1) {
            deleteCandidate(fullName);
            Assert.assertTrue(isDeleteCandidateSuccess("Cleanup candidate"), "Không xóa được candidate " + fullName);
        }
        Assert.assertEquals(countCandidates(fullName), 0, "Candidate vẫn còn sau cleanup");
    }

    public void deleteTestCandidate(String marker) {
        componentLocator.clickDropdownNavi(driver, "Candidates", "");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5[normalize-space()='Candidates']")));
        wait.until(d -> componentLocator.checkTable(d, "Candidate test", Map.of("marker", marker)));
        waitUtils.waitForTable(driver);
        By rows = By.cssSelector(".oxd-table-body .oxd-table-card");
        int before = driver.findElements(rows).size();
        deleteCandidate(marker);
        // Chờ bảng tải lại đủ số dòng, tránh hiểu nhầm bảng đang rỗng là đã xóa xong.
        Duration original = driver.manage().timeouts().getImplicitWaitTimeout();
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        try {
            wait.until(d -> d.findElements(rows).size() == before - 1
                    && d.findElements(By.cssSelector(".oxd-loading-spinner")).isEmpty()
                    && !componentLocator.checkTable(d, "Cleanup candidate", Map.of("marker", marker)));
        } finally {
            driver.manage().timeouts().implicitlyWait(original);
        }
        Log.info("Cleanup: đã xóa candidate " + marker);
    }
}
