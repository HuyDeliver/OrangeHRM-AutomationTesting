package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.AddUserPage;
import com.hrm.Pages.AdminPage.UserManagePage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class AddUserFunctionality extends TestBase {

    @Epic("Module AdminTab")
    @Feature("Quản lý User")
    @Test(description = "OHR6: Add user with Valid Data", groups = { "add-user" })
    public void addUserFunctionality() {
        TestUtil.userUtil();
        UserManagePage userManagePage = new UserManagePage(driver);
        Assert.assertTrue(userManagePage.isUsserMangeTitleVisible(), "Không vào được trang Admin");

        AddUserPage addUserPage = userManagePage.addUserPage();
        Log.info("OHR6 - Tạo user của lượt chạy: " + TestDataShare.USERNAME);
        addUserPage.addUserWithValidData(TestDataShare.USERNAME, TestConfig.userRole, TestConfig.employeeName,
                TestConfig.status, TestDataShare.USER_PASSWORD, TestDataShare.USER_PASSWORD);
        Assert.assertTrue(addUserPage.isAddUserSuccess(), "add user không thành công");
        Assert.assertEquals(userManagePage.searchByUsernameAndCount(TestDataShare.USERNAME), 1,
                "Sau khi tạo phải tìm thấy đúng một user");
        Log.info("OHR6 - Đã lưu và tìm lại được user");
    }
}
