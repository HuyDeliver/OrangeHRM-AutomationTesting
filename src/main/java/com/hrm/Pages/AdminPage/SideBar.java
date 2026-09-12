package com.hrm.Pages.AdminPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.waitUtils;

public class SideBar {
    private WebDriver driver;

    public SideBar(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".oxd-text.oxd-topbar-header-breadcrumb-module")
    private WebElement dashBoardTitle;

    @FindBy(css = "ul.oxd-main-menu > li:nth-child(1)")
    private WebElement adminTab;

    public boolean isDashBoardVisible() {
        return dashBoardTitle.isDisplayed();
    }

    public UserManagePage clickAdminPage() {
        waitUtils.waitElementClick(driver, adminTab);
        return new UserManagePage(driver);
    }

}
