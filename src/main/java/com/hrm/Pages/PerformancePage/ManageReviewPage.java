package com.hrm.Pages.PerformancePage;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.componentLocator;

public class ManageReviewPage {
    private WebDriver driver;

    public ManageReviewPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isEmployeeReviewsVisible(String title) {
        return componentLocator.checkTitleH5(driver, title);
    }

    public void clickManageReview(String tab, String option) {
        componentLocator.clickDropdownNavi(driver, tab, option);
    }

    public AddReviewPage clickButtonAdd() {
        componentLocator.clickButtonAdd(driver);
        return new AddReviewPage(driver);
    }

    public WriteReviewPage goToWriteReviewPage(String name) {
        componentLocator.evaluateButtonTable(driver, name);
        return new WriteReviewPage(driver);
    }

    public boolean isWrietReviewSuccess(String status, String name) {
        return componentLocator.checkTable(driver, "Check after wirte review", Map.of("Name", name, "Status", status));
    }

    public void deleteReview(String date) {
        componentLocator.deleteButtonTable(driver, date);
    }

}
