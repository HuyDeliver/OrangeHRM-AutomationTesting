package com.hrm.Pages.PIMPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.Log;
import com.hrm.Util.componentLocator;

public class AddEmployeePage {
    private WebDriver driver;

    public AddEmployeePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//div[contains(@class,'oxd-input-group')]/following::input[@name='firstName']")
    private WebElement firstName;
    @FindBy(xpath = "//div[contains(@class,'oxd-input-group')]/following::input[@name='middleName']")
    private WebElement middleName;
    @FindBy(xpath = "//div[contains(@class,'oxd-input-group')]/following::input[@name='lastName']")
    private WebElement lastName;

    public boolean isAddEmployeeVisible() {
        return componentLocator.checkTitleH6(driver, "Add Employee");
    }

    public void infoNewEmployee(String firstname, String lastname, String middlename, String employeeID) {
        Log.info("Nhập firstname: " + firstname);
        firstName.sendKeys(firstname);
        Log.info("Nhập middlename: " + middlename);
        middleName.sendKeys(middlename);
        Log.info("Nhập lastname: " + lastname);
        lastName.sendKeys(lastname);
        componentLocator.fillInput(driver, "Employee Id", employeeID);
        componentLocator.buttonSave(driver);
    }
}
