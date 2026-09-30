package com.hrm.Base;

import org.testng.Assert;

import com.hrm.Pages.SideBar;
import com.hrm.Pages.AdminPage.LoginPage;
import com.hrm.Pages.AdminPage.UserManagePage;
import com.hrm.Util.Config;
import com.hrm.Util.Log;

public class TestUtil extends TestBase {
    public static void userUtil() {
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginTitleVisible(), "Không vào được trang Login");
        loginPage.loginToMainWeb(Config.get("username"), Config.get("password"));
        Log.info("Đăng nhập thành công");
        SideBar sideBar = new SideBar(driver);
        sideBar.clickAdminPage();
    }

    public static void jobUtil() {
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginTitleVisible(), "Không vào được trang Login");
        loginPage.loginToMainWeb(Config.get("username"), Config.get("password"));
        Log.info("Đăng nhập thành công");
        SideBar sideBar = new SideBar(driver);
        UserManagePage userManagePage = sideBar.clickAdminPage();
        Assert.assertTrue(userManagePage.isUsserMangeTitleVisible(), "Không vào được trang Admin");
        userManagePage.clickJobNav();
        userManagePage.goToJobTitlePage();
    }

    public static void organizeUtil() {
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginTitleVisible(), "Không vào được trang Login");
        loginPage.loginToMainWeb(Config.get("username"), Config.get("password"));
        Log.info("Đăng nhập thành công");
        SideBar sideBar = new SideBar(driver);
        UserManagePage userManagePage = sideBar.clickAdminPage();
        Assert.assertTrue(userManagePage.isUsserMangeTitleVisible(), "Không vào được trang Admin");
        userManagePage.goToLocationPage("Organization", "Locations");
    }

    public static void qualificationUtil() {
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginTitleVisible(), "Không vào được trang Login");
        loginPage.loginToMainWeb(Config.get("username"), Config.get("password"));
        Log.info("Đăng nhập thành công");
        SideBar sideBar = new SideBar(driver);
        UserManagePage userManagePage = sideBar.clickAdminPage();
        Assert.assertTrue(userManagePage.isUsserMangeTitleVisible(), "Không vào được trang Admin");
        userManagePage.goToSkillPage();
    }

    public static void employeeUtil() {
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginTitleVisible(), "Không vào được trang Login");
        loginPage.loginToMainWeb(Config.get("username"), Config.get("password"));
        Log.info("Đăng nhập thành công");
        SideBar sideBar = new SideBar(driver);
        sideBar.clickEmployeeListPage("PIM");

    }

    public static void recruitmentUtil() {
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginTitleVisible(), "Không vào được trang Login");
        loginPage.loginToMainWeb(Config.get("username"), Config.get("password"));
        Log.info("Đăng nhập thành công");
        SideBar sideBar = new SideBar(driver);
        sideBar.clickRecruitmentPage("Recruitment");
    }

    public static void myInfoUtil() {
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginTitleVisible(), "Không vào được trang Login");
        loginPage.loginToMainWeb(Config.get("username"), Config.get("password"));
        Log.info("Đăng nhập thành công");
        SideBar sideBar = new SideBar(driver);
        sideBar.clickMyInfoPage("My Info");
    }

    public static void performanceUtil() {
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginTitleVisible(), "Không vào được trang Login");
        loginPage.loginToMainWeb(Config.get("username"), Config.get("password"));
        Log.info("Đăng nhập thành công");
        SideBar sideBar = new SideBar(driver);
        sideBar.clickPerformancePage("Performance");
    }
}
