package com.hrm.Pages.PerformancePage;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.Log;
import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

public class AddReviewPage {
    private WebDriver driver;

    public AddReviewPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//div[@class='orangehrm-button-row']/child::button[normalize-space()='Activate']")
    private WebElement activateButton;

    public boolean isAddReviewsVisible(String title) {
        return componentLocator.checkTitleH6(driver, title);
    }

    public void fillAddReview(String employee, String supervisor, String startDate, String endDate, String dueDate) {
        String keyName = employee.trim().split("\\s+")[0];
        componentLocator.inputDropdown(driver, "Employee Name", keyName, employee);
        componentLocator.inputDropdown(driver, "Supervisor Reviewer", keyName, supervisor);
        componentLocator.fillInput(driver, "Review Period Start Date", startDate);
        componentLocator.fillInput(driver, "Review Period End Date", endDate);
        componentLocator.fillInput(driver, "Due Date", dueDate);
    }

    public void clickActivate() {
        Log.info("Click activate");
        waitUtils.waitElementClick(driver, activateButton);
        componentLocator.takeScreenshotTable(driver, "add Review");
    }

    public boolean isAddReviewSuccess(String status) {
        return componentLocator.checkTable(driver, "Check status after add", Map.of("Status", status));
    }
}
