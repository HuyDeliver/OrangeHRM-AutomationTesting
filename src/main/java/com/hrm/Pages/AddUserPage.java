package com.hrm.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.Log;
import com.hrm.Util.waitButton;

public class AddUserPage {
    private WebDriver driver;

    public AddUserPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".oxd-form-row .oxd-input.oxd-input--active")
    private WebElement userName;

    @FindBy(xpath = "//label[normalize-space()='User Role']/ancestor::div[contains(@class, 'oxd-input-group')]//div[contains(@class, 'oxd-select-text')]")
    private WebElement userRole;

    @FindBy(css = ".oxd-autocomplete-text-input.oxd-autocomplete-text-input--active>input")
    private WebElement employeeName;

    @FindBy(xpath = "//label[normalize-space()='Status']/ancestor::div[contains(@class, 'oxd-input-group')]//div[contains(@class, 'oxd-select-wrapper')]")
    private WebElement status;

    @FindBy(xpath = "//label[normalize-space()='Password']/ancestor::div[contains(@class, 'oxd-input-group')]//input[contains(@class, 'oxd-input')]")
    private WebElement password;

    @FindBy(xpath = "//label[normalize-space()='Confirm Password']/ancestor::div[contains(@class, 'oxd-input-group')]//input[contains(@class, 'oxd-input')]")
    private WebElement passwordConfirm;

    @FindBy(xpath = "//div[@class='oxd-form-actions']/button[normalize-space()='Save' and contains(@class,'oxd-button')]")
    private WebElement buttonSave;

    @FindBy(css = ".orangehrm-card-container>h6")
    private WebElement titleAddUser;

    @FindBy(css = ".oxd-toast.oxd-toast--success.oxd-toast-container--toast")
    private WebElement toastAddSuccess;

    public boolean isTitleAduserVisible() {
        return titleAddUser.isDisplayed();
    }

    public void addUserWithValidData(String name, String role, String employee, String statusAcc, String pass,
            String confirmPass) {
        Log.info("Nhập tên");
        userName.sendKeys(name);

        Log.info("Chọn role");
        waitButton.waitElementClick(driver, userRole);
        By userRoleSelect = By.xpath("//div[contains(@class, 'oxd-select-option')]/span[text()='" + role + "']");
        waitButton.waitElementClick(driver, driver.findElement(userRoleSelect));

        Log.info("Nhập tên employee");
        employeeName.sendKeys(employee);
        By employeeSelect = By
                .xpath("//div[contains(@class, 'oxd-autocomplete-dropdown')]/div[1]/span[not(contains(text(),'Searching'))]");
        waitButton.waitElementClick(driver, driver.findElement(employeeSelect));

        Log.info("Chọn status");
        waitButton.waitElementClick(driver, status);
        By userStatusSelect = By.xpath("//div[contains(@class, 'oxd-select-option')]/span[text()='" + statusAcc + "']");
        waitButton.waitElementClick(driver, driver.findElement(userStatusSelect));

        Log.info("Nhập pass");
        password.sendKeys(pass);

        Log.info("Nhập confirm pass");
        passwordConfirm.sendKeys(confirmPass);

        Log.info("Click save");
        waitButton.waitElementClick(driver, buttonSave);
    }

    public boolean isAddUserSuccess() {
        return toastAddSuccess.isDisplayed();
    }
}
