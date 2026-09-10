package com.hrm.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.Log;
import com.hrm.Util.waitButton;

public class UserManagePage {
    private WebDriver driver;

    public UserManagePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//span[@class='oxd-topbar-header-breadcrumb']/h6[2]")
    private WebElement userManageTitle;

    public boolean isUsserMangeTitleVisible() {
        return userManageTitle.isDisplayed();
    }

    // Searching test case
    @FindBy(css = ".oxd-table-filter-area .oxd-input.oxd-input--active")
    private WebElement userName;

    @FindBy(xpath = "//label[normalize-space()='User Role']/ancestor::div[contains(@class, 'oxd-input-group')]//div[contains(@class, 'oxd-select-text')]")
    private WebElement userRole;

    @FindBy(css = ".oxd-autocomplete-text-input.oxd-autocomplete-text-input--active>input")
    private WebElement employeeName;

    @FindBy(xpath = "//label[normalize-space()='Status']/ancestor::div[contains(@class, 'oxd-input-group')]//div[contains(@class, 'oxd-select-wrapper')]")
    private WebElement status;

    @FindBy(xpath = "//div[@class='oxd-form-actions']/button[normalize-space()='Search' and contains(@class,'oxd-button')]")
    private WebElement buttonSearch;

    @FindBy(css = ".oxd-text.oxd-text--span")
    private WebElement noRecordFound;

    public void searchSystemUser(String name, String role, String employee, String statusAcc) {
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

        Log.info("Click search");
        waitButton.waitElementClick(driver, buttonSearch);
    }

    public boolean isNoRecordFound() {
        return noRecordFound.isDisplayed();
    }

    // Add functionality
    @FindBy(css = ".orangehrm-paper-container .orangehrm-header-container button")
    private WebElement buttonAdd;

    public AddUserPage addUserPage() {
        waitButton.waitElementClick(driver, buttonAdd);
        return new AddUserPage(driver);
    }
}
