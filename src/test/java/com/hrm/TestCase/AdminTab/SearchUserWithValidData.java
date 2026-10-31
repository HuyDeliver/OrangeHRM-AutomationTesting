package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.UserManagePage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class SearchUserWithValidData extends TestBase {
    @Epic("Module AdminTab")
    @Feature("Quản lý User")
    @Test(priority = 1, description = "OHR4: Search user with valid data", dependsOnGroups = {
            "add-user" })
    public void searchUserUsingDataDriven() {
        TestUtil.userUtil();

        UserManagePage userManagePage = new UserManagePage(driver);
        Assert.assertTrue(userManagePage.isUsserMangeTitleVisible(), "Không vào được trang Admin");
        userManagePage.searchSystemUser(TestDataShare.USERNAME, TestConfig.userRole, TestConfig.employeeName,
                TestConfig.status);

        Assert.assertTrue(userManagePage.isRecordFoundMatchKeySearch(TestDataShare.USERNAME,
                TestConfig.userRole, TestConfig.employeeName, TestConfig.status),
                "Không tìm thấy user khớp username, role, employee và status đã nhập");
        Log.info("OHR4: Kết quả tìm user khớp dữ liệu đã nhập");
    }
}
