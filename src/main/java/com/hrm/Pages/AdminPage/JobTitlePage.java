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
import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

public class JobTitlePage {
    private WebDriver driver;

    public JobTitlePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".orangehrm-header-container>h6")
    private WebElement jobTitle;

    @FindBy(css = ".orangehrm-paper-container .orangehrm-header-container button")
    private WebElement buttonAdd;

    public boolean isJobtitleVisible() {
        Log.info("kiểm tra đã vào được job title");
        return jobTitle.isDisplayed();
    }

    public AddJobTitlePage addUserPage() {
        Log.info("Click vào Add job title");
        waitUtils.waitElementClick(driver, buttonAdd);
        return new AddJobTitlePage(driver);
    }

    // Edit job title
    public EditJobTitlePage goToEditPage() {
        waitUtils.waitElementClick(driver, componentLocator.editButtonTable(driver));
        return new EditJobTitlePage(driver);
    }

    // Delete Job title

    @FindBy(css = ".orangehrm-modal-header .oxd-text--card-title")
    private WebElement deleteTitle;

    @FindBy(css = ".orangehrm-modal-footer>button:nth-child(2)")
    private WebElement confirmDelete;

    @FindBy(css = ".oxd-text.oxd-text--p.oxd-text--toast-message.oxd-toast-content-text")
    private WebElement deleteSuccess;

    public void clickDelete() {
        waitUtils.waitElementClick(driver, componentLocator.deleteButtonTable(driver));

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
}
