package com.hrm.Pages.AdminPage;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.Log;
import com.hrm.Util.waitUtils;

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
        waitUtils.waitElementClick(driver, userRole);
        By userRoleSelect = By.xpath("//div[contains(@class, 'oxd-select-option')]/span[text()='" + role + "']");
        waitUtils.waitElementClick(driver, driver.findElement(userRoleSelect));

        Log.info("Nhập tên employee");
        employeeName.sendKeys(employee);
        By employeeSelect = By
                .xpath("//div[contains(@class, 'oxd-autocomplete-dropdown')]/div[1]/span[not(contains(text(),'Searching'))]");
        waitUtils.waitElementClick(driver, driver.findElement(employeeSelect));

        Log.info("Chọn status");
        waitUtils.waitElementClick(driver, status);
        By userStatusSelect = By.xpath("//div[contains(@class, 'oxd-select-option')]/span[text()='" + statusAcc + "']");
        waitUtils.waitElementClick(driver, driver.findElement(userStatusSelect));

        Log.info("Click search");
        waitUtils.waitElementClick(driver, buttonSearch);
    }

    public boolean isNoRecordFound() {
        return noRecordFound.isDisplayed();
    }

    public boolean isRecordFoundMatchKeySearch(String userName) {
        By nameFound = By.xpath("//div[@class='oxd-table-card']//div[2]/div[contains(text(),'HuyDeliver')]");
        return driver.findElement(nameFound).getText().contains(userName);
    }

    // Add functionality
    @FindBy(css = ".orangehrm-paper-container .orangehrm-header-container button")
    private WebElement buttonAdd;

    public AddUserPage addUserPage() {
        waitUtils.waitElementClick(driver, buttonAdd);
        return new AddUserPage(driver);
    }

    // Delete functionality
    @FindBy(xpath = "//div[@class='oxd-table-card']//div[contains(@class,'oxd-table-cell')][6]//button[1]")
    private WebElement buttonDelete;

    @FindBy(css = ".orangehrm-modal-header .oxd-text--card-title")
    private WebElement deleteTitle;

    @FindBy(css = ".orangehrm-modal-footer>button:nth-child(2)")
    private WebElement confirmDelete;

    @FindBy(css = ".oxd-text.oxd-text--p.oxd-text--toast-message.oxd-toast-content-text")
    private WebElement deleteSuccess;

    public void clickDeleteUser() {
        Log.info("Click nút delete");
        waitUtils.waitElementClick(driver, buttonDelete);

        By deleteModal = By
                .cssSelector(".oxd-dialog-container-default .oxd-sheet");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(deleteModal));
    }

    public boolean isModalDeleteVisible() {
        Log.info("kiểm tra modal có hiện ko");
        return deleteTitle.isDisplayed();
    }

    public void deleteUserInModal() {
        Log.info("Xác nhận xóa");
        waitUtils.waitElementClick(driver, confirmDelete);
    }

    public boolean isDeleteSuccess() {
        return deleteSuccess.isDisplayed();
    }

    // Edit user
    @FindBy(xpath = "//div[@class='oxd-table-card']//div[contains(@class,'oxd-table-cell')][6]//button[2]")
    private WebElement editUser;

    public EditUserPage clickEditUser() {
        Log.info("Click nút edit user");
        waitUtils.waitElementClick(driver, editUser);
        return new EditUserPage(driver);
    }

    // Switch to job
    @FindBy(xpath = "//nav[@class='oxd-topbar-body-nav']/ul/li[2]")
    private WebElement clickJob;

    @FindBy(css = ".oxd-topbar-body-nav ul>li.--active ul.oxd-dropdown-menu")
    private WebElement dropdownJob;

    @FindBy(css = ".oxd-dropdown-menu>li:nth-child(1)")
    private WebElement clickJobtitle;

    public void clickJobNav() {
        Log.info("Click navi Job title");
        waitUtils.waitElementClick(driver, clickJob);
        Log.info("Đợi dropdown xuất hiện");
        waitUtils.waitElementVisibility(driver, dropdownJob);
    }

    public JobTitlePage goToJobTitlePage() {
        Log.info("Click vào job title");
        waitUtils.waitElementClick(driver, clickJobtitle);
        return new JobTitlePage(driver);
    }
}
