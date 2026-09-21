package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.UserManagePage;
import com.hrm.Util.Log;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class DeleteUserFunctionality extends TestBase {
    @Epic("Module AdminTab")
    @Feature("Quản lý User")
    @Test(description = "OHR8: Delete user", dependsOnGroups = { "add-user" }, priority = 5)
    public void deleteUserSuccess() {
        Log.info("Bắt đầu test delete");
        TestUtil.userUtil();
        UserManagePage userManagePage = new UserManagePage(driver);
        Assert.assertTrue(userManagePage.isUsserMangeTitleVisible(), "Không vào được trang Admin");
        userManagePage.clickDeleteUser();

        Assert.assertTrue(userManagePage.isDeleteSuccess(), "Xóa không thành công");

        Log.info("Xóa user thành công");
    }
}
