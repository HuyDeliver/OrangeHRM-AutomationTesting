package com.hrm.Pages.AdminPage;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.Log;
import com.hrm.Util.waitUtils;

public class ForgotPage {
    private WebDriver driver;

    public ForgotPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//input[@name='username']")
    private WebElement userName;

    @FindBy(css = ".oxd-text.orangehrm-forgot-password-title")
    private List<WebElement> forgotTitle;

    @FindBy(css = ".oxd-button.orangehrm-forgot-password-button--reset")
    private WebElement resetPass;

    public boolean isForgotTitleVisible() {
        Log.info("Kiểm tra chuyển hướng đến forgotPass");
        return !forgotTitle.isEmpty();
    }

    public AlertResetPassSuccess resetPassSuccess(String name) {
        Log.info("Nhập username muốn reset pass");
        userName.sendKeys(name);
        Log.info("Nhấn reset");
        waitUtils.waitElementClick(driver, resetPass);
        return new AlertResetPassSuccess(driver);
    }
}
