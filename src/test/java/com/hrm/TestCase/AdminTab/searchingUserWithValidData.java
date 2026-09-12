package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.SideBar;
import com.hrm.Pages.AdminPage.UserManagePage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

public class searchingUserWithValidData extends TestBase {
    @Test(description = "OHR4: searching user with invalid data from excel")
    public void searchUserUsingDataDriven() {
        Log.info("Bắt đầu test searching");
        TestUtil.userUtil();
        SideBar sideBar = new SideBar(driver);
        sideBar.clickAdminPage();

        UserManagePage userManagePage = new UserManagePage(driver);
        Assert.assertTrue(userManagePage.isUsserMangeTitleVisible(), "Không vào được trang Admin");
        userManagePage.searchSystemUser(TestConfig.userName, TestConfig.userRole, TestConfig.employeeName,
                TestConfig.status);

        Assert.assertTrue(userManagePage.isRecordFoundMatchKeySearch(TestConfig.userName), "không tìm thấy bản ghi");

        Log.info("TÌm thấy bản ghi");
    }
}
