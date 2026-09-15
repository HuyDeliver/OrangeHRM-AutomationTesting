package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.EditUserPage;
import com.hrm.Pages.AdminPage.UserManagePage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("Module AdminTab")
@Feature("Quản lý User")
public class EditUserFunctionality extends TestBase {
    @Test(description = "OHR6: edit user with new pass", dependsOnGroups = { "add-user" }, priority = 4)
    public void editUserWithNewPass() {
        Log.info("Bắt đầu test edit user");
        TestUtil.userUtil();

        UserManagePage userManagePage = new UserManagePage(driver);
        Assert.assertTrue(userManagePage.isUsserMangeTitleVisible(), "Không vào được trang Admin");

        userManagePage.searchSystemUser(TestConfig.userName, TestConfig.userRole, TestConfig.employeeName,
                TestConfig.status);

        EditUserPage editUserPage = userManagePage.clickEditUser();

        Assert.assertTrue(editUserPage.isEditTilteVisible(), "Không vào được trang Edit");

        editUserPage.editUser("Huy1234", "Huy1234");

        Assert.assertTrue(editUserPage.isEditUserSuccess(), "Edit User không thành công");

        Log.info("Edit user thành công");
    }

}
