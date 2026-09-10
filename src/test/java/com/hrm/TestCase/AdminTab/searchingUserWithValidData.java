package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Pages.LoginPage;
import com.hrm.Pages.SideBar;
import com.hrm.Pages.UserManagePage;
import com.hrm.Util.Config;
import com.hrm.Util.Log;

public class searchingUserWithValidData extends TestBase {
    @Test(description = "OHR4: searching user with invalid data from excel")
    public void searchUserUsingDataDriven() {
        Log.info("Bắt đầu test searching");
        loginSuccess();

        UserManagePage userManagePage = new UserManagePage(driver);
        Assert.assertTrue(userManagePage.isUsserMangeTitleVisible(), "Không vào được trang Admin");
        userManagePage.searchSystemUser("Admin", "Admin", "a", "Enabled");

        Assert.assertTrue(userManagePage.isNoRecordFound());

        Log.info("Không tìm thấy bản ghi");
    }

    public void loginSuccess() {
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginTitleVisible(), "Không vào được trang Login");
        loginPage.loginToMainWeb(Config.get("username"), Config.get("password"));
        Log.info("Đăng nhập thành công");
        SideBar sideBar = new SideBar(driver);
        sideBar.clickAdminPage();

    }
}
