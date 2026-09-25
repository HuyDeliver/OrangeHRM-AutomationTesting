package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.EditUserPage;
import com.hrm.Pages.AdminPage.UserManagePage;
import com.hrm.Util.Log;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class EditUserFunctionality extends TestBase {
    @Epic("Module AdminTab")
    @Feature("Quản lý User")
    @Test(description = "OHR7: edit user with new pass", dependsOnGroups = { "add-user" })
    public void editUserWithNewPass() {
        Log.info("Bắt đầu test edit user");
        TestUtil.userUtil();

        UserManagePage userManagePage = new UserManagePage(driver);
        Assert.assertTrue(userManagePage.isUsserMangeTitleVisible(), "Không vào được trang Admin");

        EditUserPage editUserPage = userManagePage.clickEditUser(TestDataShare.USERNAME);

        Assert.assertTrue(editUserPage.isEditTilteVisible(), "Không vào được trang Edit");

        editUserPage.editUser("BachBinhBinh@01", "BachBinhBinh@01");

        Assert.assertTrue(editUserPage.isEditUserSuccess(), "Edit User không thành công");

        Log.info("Edit user thành công");
    }

}
