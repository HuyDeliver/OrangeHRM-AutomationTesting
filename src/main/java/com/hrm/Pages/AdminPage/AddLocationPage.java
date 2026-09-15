package com.hrm.Pages.AdminPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.componentLocator;

public class AddLocationPage {
    private WebDriver driver;

    public AddLocationPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".orangehrm-card-container>h6")
    private WebElement titleLocation;

    public boolean isTitleAddLocationVisible() {
        return titleLocation.isDisplayed();
    }

    public void fillLocatinSpecific(String name, String city, String province, String postal, String country,
            String phone, String fax, String address, String note) {
        componentLocator.fillInput(driver, "Name", name);

        componentLocator.fillInput(driver, "City", city);
        componentLocator.fillInput(driver, "Name", name);

        componentLocator.fillInput(driver, "State/Province", province);
        componentLocator.fillInput(driver, "Zip/Postal Code", postal);
        componentLocator.chooseSelect(driver, "Country", country);
        componentLocator.fillInput(driver, "Phone", phone);
        componentLocator.fillInput(driver, "Fax", fax);
        componentLocator.fillTexArea(driver, "Address", address);
        componentLocator.fillTexArea(driver, "Notes", note);
    }

    public void saveLocation() {
        componentLocator.buttonSave(driver);
    }

    public boolean isAddLocationSucess() {
        return componentLocator.checkToasstSuccess(driver, "Add Location");
    }
}
