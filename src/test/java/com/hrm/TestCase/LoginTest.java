package com.hrm.TestCase;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Pages.AlertResetPassSuccess;
import com.hrm.Pages.ForgotPage;
import com.hrm.Pages.LoginPage;
import com.hrm.Util.Config;
import com.hrm.Util.Log;

public class LoginTest extends TestBase {

    @Test
    public void loginSuccess() {
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginTitleVisible(), "Không vào được trang Login");
        loginPage.loginToMainWeb(Config.get("username"), Config.get("password"));
        Log.info("Đăng nhập thành công");
    }

    @Test
    public void loginFail() {
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginTitleVisible(), "Không vào được trang Login");
        loginPage.loginToMainWeb("admin", "0000");
        Assert.assertTrue(loginPage.isLoginFail());
        Log.info("Đăng nhập không thành công");
    }

    @Test
    public void resetPassword() {
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginTitleVisible(), "Không vào được trang Login");

        ForgotPage forgotPage = loginPage.clickForgotPassPage();
        Assert.assertTrue(forgotPage.isForgotTitleVisible(), "Không vào được trang forgot");

        AlertResetPassSuccess alertResetPassSuccess = forgotPage.resetPassSuccess("Tushar");
        Assert.assertTrue(alertResetPassSuccess.isResetPassSuccess(), "reset không thành công");
        alertResetPassSuccess.backToLogin();
    }
}
