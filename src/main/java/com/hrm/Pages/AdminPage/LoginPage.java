package com.hrm.Pages.AdminPage;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.Log;
import com.hrm.Util.waitUtils;

public class LoginPage {
    private WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//input[@name='username']")
    private WebElement userName;

    @FindBy(xpath = "//input[@name='password']")
    private WebElement passWord;

    @FindBy(css = ".oxd-button.orangehrm-login-button")
    private WebElement loginButton;

    @FindBy(css = ".orangehrm-login-slot>h5")
    private List<WebElement> loginTitle;

    @FindBy(css = ".oxd-text.oxd-alert-content-text")
    private List<WebElement> alertLoginFail;

    @FindBy(css = ".oxd-text.orangehrm-login-forgot-header")
    private WebElement forgotButton;

    @FindBy(css = ".oxd-text.oxd-text--span.oxd-input-field-error-message.oxd-input-group__message")
    private List<WebElement> required;

    public boolean isLoginTitleVisible() {
        return !loginTitle.isEmpty();
    }

    public SideBar loginToMainWeb(String name, String pass) {
        Log.info("Nhập username");
        userName.sendKeys(name);
        Log.info("Nhập password");
        passWord.sendKeys(pass);
        Log.info("Click login");
        waitUtils.waitElementClick(driver, loginButton);
        return new SideBar(driver);
    }

    public boolean isLoginFail() {
        try {
            if (!alertLoginFail.isEmpty()) {
                return true;
            }
        } catch (Exception e) {

        }
        try {
            if (!required.isEmpty()) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public ForgotPage clickForgotPassPage() {
        waitUtils.waitElementClick(driver, forgotButton);
        return new ForgotPage(driver);
    }

    public boolean isPassRequired() {
        return !required.isEmpty();
    }
}
