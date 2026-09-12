package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.UserManagePage;
import com.hrm.Util.ExelReader;
import com.hrm.Util.Log;

public class searchingUserWithInvalidData extends TestBase {
    @Test(dataProvider = "searchingUserInvalidData", description = "OHR4: searching user with invalid data from excel")
    public void searchUserUsingDataDriven(String userName, String role, String employee, String status) {
        Log.info("Bắt đầu test searching");
        TestUtil.adminUtil();
        UserManagePage userManagePage = new UserManagePage(driver);
        Assert.assertTrue(userManagePage.isUsserMangeTitleVisible(), "Không vào được trang Admin");
        userManagePage.searchSystemUser(userName, role, employee, status);

        Assert.assertTrue(userManagePage.isNoRecordFound());

        Log.info("Không tìm thấy bản ghi");
    }

    @DataProvider(name = "searchingUserInvalidData")
    public Object[][] getSearchingData() {
        String path = "src/test/resources/data/Datadriven-OrangeHrm.xlsx";
        String sheetName = "Search";
        return ExelReader.getDataFromExel(path, sheetName);
    }
}
