package com.hrm.Pages.PIMPage;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.TestDataShare;
import com.hrm.Util.componentLocator;

public class MyInfoPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public MyInfoPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean updatePersonalDetails() {
        componentLocator.clickSideBarMyInfo(driver, "Personal Details");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[normalize-space()='Other Id']")));
        componentLocator.fillInput(driver, "Other Id", TestDataShare.MYINFO_OTHER_ID);
        componentLocator.buttonSave(driver);
        return waitForSuccessToast()
                && inputValueMatches("Other Id", TestDataShare.MYINFO_OTHER_ID);
    }

    public boolean updateContactDetails() {
        componentLocator.clickSideBarMyInfo(driver, "Contact Details");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[normalize-space()='Street 1']")));
        componentLocator.fillInput(driver, "Street 1", TestDataShare.MYINFO_STREET);
        componentLocator.fillInput(driver, "City", TestDataShare.MYINFO_CITY);
        componentLocator.fillInput(driver, "Other Email", TestDataShare.MYINFO_OTHER_EMAIL);
        componentLocator.buttonSave(driver);
        return waitForSuccessToast()
                && inputValueMatches("Street 1", TestDataShare.MYINFO_STREET)
                && inputValueMatches("City", TestDataShare.MYINFO_CITY)
                && inputValueMatches("Other Email", TestDataShare.MYINFO_OTHER_EMAIL);
    }

    public boolean addEmergencyContact() {
        componentLocator.clickSideBarMyInfo(driver, "Emergency Contacts");
        By nameInput = By.xpath("//label[normalize-space()='Name']/following::input[1]");

        if (driver.findElements(nameInput).isEmpty()) {
            componentLocator.buttonAddMyInfo(driver, "Assigned Emergency Contacts");
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameInput));
        componentLocator.fillInput(driver, "Name", TestDataShare.EMERGENCY_NAME);
        componentLocator.fillInput(driver, "Relationship", "Sibling");
        componentLocator.fillInput(driver, "Mobile", "0900000000");
        componentLocator.buttonSave(driver);
        waitForSuccessToast();
        return wait.until(d -> d.findElements(By.cssSelector(".oxd-table-card")).stream()
                .anyMatch(row -> row.isDisplayed() && row.getText().contains(TestDataShare.EMERGENCY_NAME)));
    }

    private boolean inputValueMatches(String label, String expected) {
        By input = By.xpath("//label[normalize-space()='" + label + "']/following::input[1]");
        return wait.until(ExpectedConditions.visibilityOfElementLocated(input))
                .getDomProperty("value").equals(expected);
    }

    private boolean waitForSuccessToast() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".oxd-toast.oxd-toast--success.oxd-toast-container--toast"))).isDisplayed();
    }
}
