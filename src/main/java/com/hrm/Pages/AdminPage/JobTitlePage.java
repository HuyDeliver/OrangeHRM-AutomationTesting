package com.hrm.Pages.AdminPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hrm.Util.Log;
import com.hrm.Util.waitUtils;

public class JobTitlePage {
    private WebDriver driver;

    public JobTitlePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".orangehrm-header-container>h6")
    private WebElement jobTitle;

    @FindBy(css = ".orangehrm-paper-container .orangehrm-header-container button")
    private WebElement buttonAdd;

    public boolean isJobtitleVisible() {
        Log.info("kiểm tra đã vào được job title");
        return jobTitle.isDisplayed();
    }

    public AddJobTitlePage addUserPage() {
        Log.info("Click vào Add job title");
        waitUtils.waitElementClick(driver, buttonAdd);
        return new AddJobTitlePage(driver);
    }
}
