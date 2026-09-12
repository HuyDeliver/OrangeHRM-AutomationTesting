package com.hrm.TestCase;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Pages.AdminPage.AlertResetPassSuccess;
import com.hrm.Pages.AdminPage.ForgotPage;
import com.hrm.Pages.AdminPage.LoginPage;
import com.hrm.Util.Config;
import com.hrm.Util.ExelReader;
import com.hrm.Util.Log;

public class LoginTest extends TestBase {

    @Test(description = "OHR1:login with right username and password")
    public void loginSuccess() {
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginTitleVisible(), "Không vào được trang Login");
        loginPage.loginToMainWeb(Config.get("username"), Config.get("password"));
        Log.info("Đăng nhập thành công");
    }

    @Test(dataProvider = "Datadriven-OrangeHrm", description = "OHR2: login with fail with data from excel")
    public void loginFail(String userName, String passWord) {
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginTitleVisible(), "Không vào được trang Login");
        loginPage.loginToMainWeb(userName, passWord);
        Assert.assertTrue(loginPage.isLoginFail());
        Log.info("Đăng nhập không thành công");

    }

    @Test(description = "OHR3:forgot Pass and reset pass")
    public void resetPassword() {
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginTitleVisible(), "Không vào được trang Login");

        ForgotPage forgotPage = loginPage.clickForgotPassPage();
        Assert.assertTrue(forgotPage.isForgotTitleVisible(), "Không vào được trang forgot");

        AlertResetPassSuccess alertResetPassSuccess = forgotPage.resetPassSuccess("Tushar");
        Assert.assertTrue(alertResetPassSuccess.isResetPassSuccess(), "reset không thành công");
        alertResetPassSuccess.backToLogin();
    }

    @DataProvider(name = "Datadriven-OrangeHrm")
    public Object[][] getLoginData() {
        String path = "src/test/resources/data/Datadriven-OrangeHrm.xlsx";
        String sheetName = "loginFail";
        return ExelReader.getDataFromExel(path, sheetName);
    }
}
