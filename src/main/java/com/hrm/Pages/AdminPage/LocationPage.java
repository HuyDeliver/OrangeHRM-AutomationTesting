package com.hrm.Pages.AdminPage;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.componentLocator;

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

}
