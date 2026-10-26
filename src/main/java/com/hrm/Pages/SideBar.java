package com.hrm.Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Pages.AdminPage.UserManagePage;
import com.hrm.Pages.ClaimPage.ClaimPage;
import com.hrm.Pages.MyInfoPage.PersonalDetailPage;
import com.hrm.Pages.PIMPage.EmployeeListPage;
import com.hrm.Pages.PerformancePage.ManageReviewPage;
import com.hrm.Pages.RecruitmentPage.CandidatesPage;
import com.hrm.Pages.TimePage.TimesheetPage;
import com.hrm.Util.componentLocator;
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
        return componentLocator.checkTitleH6(driver, "Dashboard")
                && "Dashboard".equals(dashBoardTitle.getText());
    }

    public UserManagePage clickAdminPage() {
        waitUtils.waitElementClick(driver, adminTab);
        return new UserManagePage(driver);
    }

    public EmployeeListPage clickEmployeeListPage(String tab) {
        componentLocator.clickSideBar(driver, tab);
        return new EmployeeListPage(driver);
    }

    public CandidatesPage clickRecruitmentPage(String tab) {
        componentLocator.clickSideBar(driver, tab);
        return new CandidatesPage(driver);
    }

    public PersonalDetailPage clickMyInfoPage(String tab) {
        componentLocator.clickSideBar(driver, tab);
        return new PersonalDetailPage(driver);
    }

    public ManageReviewPage clickPerformancePage(String tab) {
        componentLocator.clickSideBar(driver, tab);
        return new ManageReviewPage(driver);
    }

    public TimesheetPage clickTimePage(String tab) {
        componentLocator.clickSideBar(driver, tab);
        return new TimesheetPage(driver);
    }

    public ClaimPage clickClaimPage(String tab) {
        componentLocator.clickSideBar(driver, tab);
        return new ClaimPage(driver);
    }
}
