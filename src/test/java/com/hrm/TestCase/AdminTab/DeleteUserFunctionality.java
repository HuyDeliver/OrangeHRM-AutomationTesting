package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.UserManagePage;
import com.hrm.Util.Log;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class DeleteUserFunctionality extends TestBase {
    @Epic("Module AdminTab")
    @Feature("Quản lý User")
    @Test(priority = 3, description = "OHR8: Delete user", dependsOnGroups = { "add-user" })
    public void deleteUserSuccess() {
        Log.info("Bắt đầu test delete");
        TestUtil.userUtil();
        UserManagePage userManagePage = new UserManagePage(driver);
        Assert.assertTrue(userManagePage.isUsserMangeTitleVisible(), "Không vào được trang Admin");
        Assert.assertEquals(userManagePage.searchByUsernameAndCount(TestDataShare.USERNAME), 1,
                "Phải tìm được đúng user của lượt chạy trước khi xóa");
        userManagePage.clickDeleteUser(TestDataShare.USERNAME);

        Assert.assertTrue(userManagePage.isDeleteSuccess(), "Xóa không thành công");

        Assert.assertEquals(userManagePage.searchByUsernameAndCount(TestDataShare.USERNAME), 0,
                "User vẫn tồn tại sau khi xóa");
        Log.info("OHR8 - Đã xóa, tìm lại trả về 0 record");
    }
}
