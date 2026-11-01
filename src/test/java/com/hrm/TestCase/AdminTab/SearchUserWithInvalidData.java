package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.UserManagePage;
import com.hrm.Util.ExelReader;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class SearchUserWithInvalidData extends TestBase {
    @Epic("Module AdminTab")
    @Feature("Quản lý User")
    @Test(priority = 1, dataProvider = "searchingUserInvalidData", description = "OHR5: searching user with invalid data from excel", dependsOnGroups = {
            "add-user" })
    public void searchUserUsingDataDriven(String userName, String role, String employee, String status) {
        Log.info("Bắt đầu test searching");
        TestUtil.userUtil();
        UserManagePage userManagePage = new UserManagePage(driver);
        Assert.assertTrue(userManagePage.isUsserMangeTitleVisible(), "Không vào được trang Admin");
        userManagePage.searchSystemUser(userName, role, employee, status);

        Assert.assertTrue(userManagePage.isNoRecordFound(), "Bộ lọc âm tính phải hiển thị No Records Found");

        Log.info("Không tìm thấy bản ghi");
    }

    @DataProvider(name = "searchingUserInvalidData")
    public Object[][] getSearchingData() {
        String path = "src/test/resources/data/Datadriven-OrangeHrm.xlsx";
        String sheetName = "Search";
        Object[][] data = ExelReader.getDataFromExel(path, sheetName);
        if (Boolean.getBoolean("cd.admin")) {
            Assert.assertEquals(data.length, 2, "CD đang ánh xạ hai dòng Search trong Excel");
            // Dòng 1: username không tồn tại. Dòng 2: user có thật nhưng sai role/status.
            data[0][0] = TestDataShare.USERNAME + "_missing";
            data[1][0] = TestDataShare.USERNAME;
            for (Object[] row : data) row[2] = TestConfig.employeeName;
        }
        return data;
    }
}
