package com.hrm.Pages.MyInfoPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import java.time.Duration;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.componentLocator;

public class ContactDetailPage {
    private final WebDriver driver;

    public ContactDetailPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isContactDetailVisible(String title) {
        return componentLocator.checkTitleH6(driver, title);
    }

    public void fillContactDetails(String street1, String street2, String city, String state, String zipCode,
            String country, String homePhone, String mobilePhone, String otherEmail) {

        // Address
        componentLocator.fillInput(driver, "Street 1", street1);
        componentLocator.fillInput(driver, "Street 2", street2);
        componentLocator.fillInput(driver, "City", city);
        componentLocator.fillInput(driver, "State/Province", state);
        componentLocator.fillInput(driver, "Zip/Postal Code", zipCode);

        // Country dropdown
        componentLocator.chooseSelect(driver, "Country", country);

        // Telephone
        componentLocator.fillInput(driver, "Home", homePhone);
        componentLocator.fillInput(driver, "Mobile", mobilePhone);

        // Email
        componentLocator.fillInput(driver, "Other Email", otherEmail);
    }

    public void clicksave() {
        componentLocator.buttonSave(driver);
        componentLocator.takeScreenshotResult(driver, "Add contact detail");
    }

    public boolean hasSavedContact(String street, String city, String email) {
        driver.navigate().refresh();
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.attributeToBe(
                By.xpath("//label[normalize-space()='Street 1']/following::input[1]"), "value", street));
        return city.equals(driver.findElement(By.xpath("//label[normalize-space()='City']/following::input[1]")).getAttribute("value"))
                && email.equals(driver.findElement(By.xpath("//label[normalize-space()='Other Email']/following::input[1]")).getAttribute("value"));
    }

    public boolean isAddContactDetailSuccess(String toast) {
        return componentLocator.checkToasstSuccess(driver, toast);
    }

}
