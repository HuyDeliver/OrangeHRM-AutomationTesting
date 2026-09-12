package com.hrm.Pages.AdminPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.Log;
import com.hrm.Util.waitButton;

public class EditUserPage {
    private WebDriver driver;

    public EditUserPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".oxd-text.orangehrm-main-title")
    private WebElement editTitle;

    @FindBy(xpath = "//label[normalize-space()='Password']/ancestor::div[contains(@class, 'oxd-input-group')]//input[contains(@class, 'oxd-input')]")
    private WebElement password;

    @FindBy(xpath = "//label[normalize-space()='Confirm Password']/ancestor::div[contains(@class, 'oxd-input-group')]//input[contains(@class, 'oxd-input')]")
    private WebElement passwordConfirm;

    @FindBy(css = ".oxd-form-actions button.oxd-button--secondary")
    private WebElement buttonSave;

    @FindBy(css = ".oxd-checkbox-wrapper label")
    private WebElement changePass;

    @FindBy(css = ".oxd-toast.oxd-toast--success.oxd-toast-container--toast")
    private WebElement toastEditSuccess;

    public boolean isEditTilteVisible() {
        Log.info("Kiểm tra trang edit có hiện");
        return editTitle.isDisplayed();
    }

    public void editUser(String pass, String confirmPass) {

        Log.info("Xác nhận đổi pass");
        waitButton.waitElementClick(driver, changePass);

        Log.info("Nhập pass");
        password.sendKeys(pass);

        Log.info("Nhập confirm pass");
        passwordConfirm.sendKeys(confirmPass);

        Log.info("Click save");
        waitButton.waitElementClick(driver, buttonSave);
    }

    public boolean isEditUserSuccess() {
        return toastEditSuccess.isDisplayed();
    }
}
