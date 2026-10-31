package com.hrm.Pages.AdminPage;

import java.util.Map;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.Log;
import com.hrm.Util.componentLocator;
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
        searchAndCount();
    }

    public int searchByUsernameAndCount(String username) {
        // Sau Save, đợi ô của trang danh sách thay vì nhập vào form Add còn đang tải.
        waitUtils.waitElementVisibility(driver, userName);
        componentLocator.fillInput(driver, "Username", username);
        return searchAndCount();
    }

    private int searchAndCount() {
        By summary = By.xpath("//div[contains(@class,'orangehrm-horizontal-padding')]/child::span");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement oldResult = wait.until(ExpectedConditions.visibilityOfElementLocated(summary));
        componentLocator.clickSearch(driver);
        wait.until(ExpectedConditions.stalenessOf(oldResult));
        String result = wait.until(ExpectedConditions.visibilityOfElementLocated(summary)).getText();
        if (result.equals("No Records Found")) return 0;
        if (!result.matches("\\(\\d+\\) Records? Found")) {
            throw new IllegalStateException("Không đọc được kết quả User: " + result);
        }
        return Integer.parseInt(result.replaceAll("\\D", ""));
    }

    public boolean isNoRecordFound() {
        return noRecordFound.isDisplayed();
    }

    public boolean isRecordFoundMatchKeySearch(String name, String role, String employee, String statusAcc) {
        return componentLocator.checkTable(driver, "Search user",
                Map.of("Username", name, "User Role", role, "Employee Name", employee, "Status", statusAcc))
                && driver.findElements(By.cssSelector(".oxd-table-card")).size() == 1;
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

    public void clickDeleteUser(String user) {
        componentLocator.deleteButtonTable(driver, user);
    }

    public boolean isDeleteSuccess() {
        return componentLocator.checkToasstSuccess(driver, "Delete user");
    }

    // Edit user
    @FindBy(xpath = "//div[@class='oxd-table-card']//div[contains(@class,'oxd-table-cell')][6]//button[2]")
    private WebElement editUser;

    public EditUserPage clickEditUser(String username) {
        componentLocator.editButtonTable(driver, username);
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

    public LocationPage goToLocationPage(String navi, String option) {
        componentLocator.clickDropdownNavi(driver, navi, option);
        return new LocationPage(driver);
    }

    public SkillPage goToSkillPage() {
        componentLocator.clickDropdownNavi(driver, "Qualifications", "Skills");
        return new SkillPage(driver);
    }

    public PayGradePage goToPayGradePage() {
        PayGradePage payGradePage = new PayGradePage(driver);
        payGradePage.openPayGrades();
        return payGradePage;
    }
}
