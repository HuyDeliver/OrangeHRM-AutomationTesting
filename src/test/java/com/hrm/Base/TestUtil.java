package com.hrm.Base;

import org.testng.Assert;

import com.hrm.Pages.LoginPage;
import com.hrm.Pages.SideBar;
import com.hrm.Util.Config;
import com.hrm.Util.Log;

public class TestUtil extends TestBase {
    public static void adminUtil() {
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginTitleVisible(), "Không vào được trang Login");
        loginPage.loginToMainWeb(Config.get("username"), Config.get("password"));
        Log.info("Đăng nhập thành công");
        SideBar sideBar = new SideBar(driver);
        sideBar.clickAdminPage();

    }
}
