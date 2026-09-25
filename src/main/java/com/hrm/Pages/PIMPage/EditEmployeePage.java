package com.hrm.Pages.PIMPage;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.componentLocator;

public class EditEmployeePage {
    private WebDriver driver;

    public EditEmployeePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isPersonalDetailVisible(String title) {
        return componentLocator.checkTitleH6(driver, title);
    }

    public void goToJobDetail(String job) {
        componentLocator.clickSideBarMyInfo(driver, job);
    }

    public boolean isJobdetailVisible(String title) {
        return componentLocator.checkTitleH6(driver, title);
    }

    public void fillJobDetailInfo(String joinDate, String title, String catergory,
            String subUnit, String location, String status) {
        componentLocator.fillInput(driver, "Joined Date", joinDate);
        componentLocator.chooseSelectAction(driver, "Job Title", title);
        componentLocator.chooseSelectAction(driver, "Job Category", catergory);
        componentLocator.chooseSelectAction(driver, "Sub Unit", subUnit);
        componentLocator.chooseSelectAction(driver, "Location", location);
        componentLocator.chooseSelectAction(driver, "Employment Status", status);
    }

    public void saveJobDetail() {
        componentLocator.buttonSave(driver);
    }

    public boolean isFillJobDetailSuccess(String toast) {
        return componentLocator.checkToasstSuccess(driver, toast);
    }

}
