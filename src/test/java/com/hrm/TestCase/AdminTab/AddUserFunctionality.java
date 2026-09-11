package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AddUserPage;
import com.hrm.Pages.UserManagePage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

public class AddUserFunctionality extends TestBase {
    @Test(description = "OHR5: Add user with Valid Data")
    public void addUserFunctionality() {
        TestUtil.adminUtil();
        UserManagePage userManagePage = new UserManagePage(driver);
        Assert.assertTrue(userManagePage.isUsserMangeTitleVisible(), "Không vào được trang Admin");

        AddUserPage addUserPage = userManagePage.addUserPage();
        Log.info("Bắt đầu test add user");
        addUserPage.addUserWithValidData(TestConfig.userName, TestConfig.userRole, TestConfig.employeeName,
                TestConfig.status, TestConfig.password, TestConfig.confirmPassword);
        Assert.assertTrue(addUserPage.isAddUserSuccess(), "add user không thành công");

        Log.info("Tạo user thành công");
    }
}
