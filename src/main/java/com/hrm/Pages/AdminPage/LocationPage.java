package com.hrm.Pages.AdminPage;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

public class LocationPage {
    private WebDriver driver;

    public LocationPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//div[contains(@class,'oxd-table-filter-header-title')]/child::h5")
    private WebElement titleLocation;

    public boolean isTitleLocationVisible() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOf(titleLocation)).isDisplayed();
    }

    public AddLocationPage goToAddLocationPage() {
        componentLocator.clickButtonAdd(driver);
        return new AddLocationPage(driver);
    }

    public void deleteLocation(String location) {
        componentLocator.deleteButtonTable(driver, location);
    }

    public boolean isDeleteLocationSuccess() {
        return componentLocator.checkToasstSuccess(driver, "Delete Location");
    }

    public EditLocationPage clickEditLocation(String location) {
        componentLocator.editButtonTable(driver, location);
        return new EditLocationPage(driver);
    }

    public void searchLocation(String name, String City, String country) {
        waitUtils.waitForTable(driver);
        componentLocator.fillInput(driver, "Name", name);
        componentLocator.fillInput(driver, "City", City);
        componentLocator.chooseSelect(driver, "Country", country);
        By summary = By.xpath("//div[contains(@class,'orangehrm-horizontal-padding')]/child::span");
        WebElement oldResult = driver.findElement(summary);
        componentLocator.clickSearch(driver);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.stalenessOf(oldResult));
        waitUtils.waitForTable(driver);
    }

    public int countLocations(String name) {
        return componentLocator.countTableRows(driver, name);
    }

    public boolean isSearchLocationSuccess(String name, String city, String country, boolean expectedFound) {
        if (!expectedFound) {
            return componentLocator.checkNoRecordFound(driver);
        }
        return componentLocator.checkSearch(driver, "Locations", name, city, country);
    }
}
