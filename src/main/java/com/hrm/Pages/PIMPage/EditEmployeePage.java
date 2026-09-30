package com.hrm.Pages.PIMPage;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.Log;
import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

public class EditEmployeePage {
    private WebDriver driver;

    public EditEmployeePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//div[@role='dialog']/descendant::button[@type='submit']")
    private WebElement saveTerminate;

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

    public void saveInfo(String name) {
        componentLocator.buttonSave(driver);
        componentLocator.takeScreenshotResult(driver, name);
    }

    public boolean isEditSuccess(String toast) {
        return componentLocator.checkToasstSuccess(driver, toast);
    }

    public void addSalary(String title) {
        componentLocator.buttonAddMyInfo(driver, title);
    }

    // Salary
    public void goToSalary(String salary) {
        componentLocator.clickSideBarMyInfo(driver, salary);
    }

    public boolean isSalaryVisible(String salary) {
        return componentLocator.checkTitleH6(driver, salary);
    }

    public void fillSalaryInfo(String salary, String payGrade, String payFrequency, String currency, String amount,
            String comment) {
        componentLocator.fillInput(driver, "Salary Component", salary);
        componentLocator.chooseSelect(driver, "Pay Grade", payGrade);
        componentLocator.chooseSelectAction(driver, "Pay Frequency", payFrequency);
        componentLocator.chooseSelectAction(driver, "Currency", currency);
        componentLocator.fillInput(driver, "Amount", amount);
        componentLocator.fillTexArea(driver, "Comments", comment);
    }

    // Supervisor
    public void goToReportTo(String salary) {
        componentLocator.clickSideBarMyInfo(driver, salary);
    }

    public boolean isReportToVisible(String title) {
        return componentLocator.checkTitleH6(driver, title);
    }

    public void addSupervisor(String title) {
        componentLocator.buttonAddMyInfo(driver, title);
    }

    public void fillSupervisor(String keyword, String name, String method) {
        componentLocator.inputDropdown(driver, "Name", keyword, name);
        componentLocator.chooseSelectAction(driver, "Reporting Method", method);
    }

    // Terminate employee
    public void clickTerminateEmployee(String title) {
        Log.info("Click vào terminate");
        componentLocator.buttonAddMyInfo(driver, title);
    }

    public void fillTerrminateReason(String date, String reason, String note) {
        By terminateModal = By
                .cssSelector(".oxd-dialog-container-default .oxd-sheet");
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(terminateModal));

        componentLocator.fillInput(driver, "Termination Date", date);
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".oxd-date-input-calendar")));
        componentLocator.chooseSelectAction(driver, "Termination Reason", reason);
        componentLocator.fillTexArea(driver, "Note", note);
    }

    public boolean isTerminateEmployeeSuccess(String toast) {
        return componentLocator.checkToasstSuccess(driver, toast);
    }

    public void saveTerminate(String name) {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions
                        .invisibilityOfElementLocated(By.xpath("//div[contains(@class,'oxd-select-dropdown')]")));
        waitUtils.waitElementClick(driver, saveTerminate);
        componentLocator.takeScreenshotResult(driver, name);
    }

    public EmployeeListPage clickEmployeeList(String tab) {
        componentLocator.clickDropdownNavi(driver, tab, "");
        return new EmployeeListPage(driver);
    }
}
