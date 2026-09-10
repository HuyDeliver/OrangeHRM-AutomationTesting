package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Pages.LoginPage;
import com.hrm.Pages.SideBar;
import com.hrm.Pages.UserManagePage;
import com.hrm.Util.Config;
import com.hrm.Util.ExelReader;
import com.hrm.Util.Log;

public class searchingUserWithInvalidData extends TestBase {
    @Test(dataProvider = "searchingUserInvalidData", description = "OHR4: searching user with invalid data from excel")
    public void searchUserUsingDataDriven(String userName, String role, String employee, String status) {
        Log.info("Bắt đầu test searching");
        loginSuccess();

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

    public void loginSuccess() {
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginTitleVisible(), "Không vào được trang Login");
        loginPage.loginToMainWeb(Config.get("username"), Config.get("password"));
        Log.info("Đăng nhập thành công");
        SideBar sideBar = new SideBar(driver);
        sideBar.clickAdminPage();

    }
}
