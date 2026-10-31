package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.EditUserPage;
import com.hrm.Pages.AdminPage.LoginPage;
import com.hrm.Pages.AdminPage.UserManagePage;
import com.hrm.Util.Log;
import com.hrm.Util.TestDataShare;
import com.hrm.Util.Config;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class EditUserFunctionality extends TestBase {
    @Epic("Module AdminTab")
    @Feature("Quản lý User")
    @Test(priority = 2, description = "OHR7: edit user with new pass", dependsOnGroups = { "add-user" })
    public void editUserWithNewPass() {
        Log.info("Bắt đầu test edit user");
        TestUtil.userUtil();

        UserManagePage userManagePage = new UserManagePage(driver);
        Assert.assertTrue(userManagePage.isUsserMangeTitleVisible(), "Không vào được trang Admin");

        Assert.assertEquals(userManagePage.searchByUsernameAndCount(TestDataShare.USERNAME), 1,
                "Phải tìm được đúng user của lượt chạy trước khi sửa");
        EditUserPage editUserPage = userManagePage.clickEditUser(TestDataShare.USERNAME);

        Assert.assertTrue(editUserPage.isEditTilteVisible(), "Không vào được trang Edit");

        editUserPage.editUser(TestDataShare.USER_NEW_PASSWORD, TestDataShare.USER_NEW_PASSWORD);

        Assert.assertTrue(editUserPage.isEditUserSuccess(), "Edit User không thành công");

        Log.info("OHR7 - Xóa cookie phiên Admin, đăng nhập user vừa sửa bằng mật khẩu mới");
        driver.manage().deleteAllCookies();
        driver.get(Config.get("app.url"));
        LoginPage login = new LoginPage(driver);
        Assert.assertTrue(login.isLoginTitleVisible(), "Không mở được Login cho phiên mới");
        Assert.assertTrue(login.loginToMainWeb(TestDataShare.USERNAME, TestDataShare.USER_NEW_PASSWORD)
                .isDashBoardVisible(), "User không đăng nhập được bằng mật khẩu mới");
        Log.info("OHR7 - Đăng nhập bằng mật khẩu mới thành công");
    }

}
