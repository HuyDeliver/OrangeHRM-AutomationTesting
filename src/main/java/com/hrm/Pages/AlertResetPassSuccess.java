package com.hrm.Pages;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.Log;

public class AlertResetPassSuccess {
    private WebDriver driver;

    public AlertResetPassSuccess(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".oxd-text.oxd-text--h6.orangehrm-forgot-password-title")
    private List<WebElement> resetPassSuccess;

    public boolean isResetPassSuccess() {
        Log.info("Kiểm tra Thông báo reset thành công");
        return !resetPassSuccess.isEmpty();
    }

    public void backToLogin() {
        Log.info("Chuyển hướng về trang login");
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
    }

}
