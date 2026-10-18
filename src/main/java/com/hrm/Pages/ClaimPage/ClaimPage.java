package com.hrm.Pages.ClaimPage;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

public class ClaimPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final By modal = By.cssSelector(".oxd-dialog-container-default .oxd-sheet");
    private final By attachmentRows = By.cssSelector(".orangehrm-card-table .oxd-table-card");

    public ClaimPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public void openTestClaim(String remarks) {
        if (!driver.findElements(modal).isEmpty()) {
            waitUtils.waitElementClick(driver, driver.findElement(modal)
                    .findElement(By.xpath(".//button[normalize-space()='Cancel']")));
        }
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//li[contains(@class,'oxd-topbar-body-nav-tab') and contains(.,'My Claims')]")));
        componentLocator.clickDropdownNavi(driver, "My Claims", "");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5[normalize-space()='My Claims']")));
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector(".oxd-loading-spinner"), 0));
        List<WebElement> rows = driver.findElements(By.xpath(
                "//div[@class='oxd-table-card'][contains(.,'" + remarks + "')]"));
        if (rows.size() != 1) throw new IllegalStateException("Phải có đúng một Claim fixture: " + remarks);
        // Claim dùng nút chữ View Details, khác nút icon của component viewButtonTable().
        waitUtils.waitElementClick(driver, rows.get(0).findElement(By.xpath(".//button[normalize-space()='View Details']")));
        waitForDetails();
    }

    public String getField(String label) {
        return driver.findElement(By.xpath("//label[normalize-space()='" + label + "']/following::input[1]"))
                .getAttribute("value");
    }

    public String getRemarks() {
        return driver.findElement(By.xpath("//label[normalize-space()='Remarks']/following::textarea[1]"))
                .getAttribute("value");
    }

    public void uploadAttachment(String file, String comment) {
        componentLocator.buttonAddMyInfo(driver, "Attachments");
        wait.until(ExpectedConditions.visibilityOfElementLocated(modal));
        componentLocator.fillFile(driver, "Select File", file);
        componentLocator.fillTexArea(driver, "Comment", comment);
        componentLocator.buttonSave(driver);
        wait.until(ExpectedConditions.or(ExpectedConditions.invisibilityOfElementLocated(modal),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".oxd-toast--error"))));
    }

    public boolean hasUploadError() {
        return !driver.findElements(By.cssSelector(".oxd-toast--error")).isEmpty();
    }

    public void reload() {
        driver.navigate().refresh();
        waitForDetails();
    }

    public boolean hasAttachment(String fileName, String comment) {
        return componentLocator.checkTable(driver, "Claim attachment", Map.of("file", fileName, "comment", comment));
    }

    public int getAttachmentCount() {
        return driver.findElements(attachmentRows).size();
    }

    public void showAttachments() {
        new Actions(driver).scrollToElement(driver.findElement(By.cssSelector(".orangehrm-card-table"))).perform();
    }

    public void deleteTestAttachment(String comment) {
        if (!componentLocator.checkTable(driver, "Own attachment", Map.of("comment", comment))) return;
        int before = getAttachmentCount();
        componentLocator.deleteButtonTable(driver, comment);
        wait.until(ExpectedConditions.numberOfElementsToBe(attachmentRows, before - 1));
        reload();
    }

    private void waitForDetails() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[normalize-space()='Attachments']")));
        wait.until(d -> !getField("Status").isBlank());
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector(".oxd-loading-spinner"), 0));
    }
}
