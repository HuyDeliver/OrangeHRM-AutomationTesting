package com.hrm.Pages.PIMPage;

import java.time.Duration;
import java.util.Map;

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

    public String getEmployeeNumber() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlMatches(".*/empNumber/[0-9]+.*"));
        return driver.getCurrentUrl().split("/empNumber/")[1].split("[/?#]")[0];
    }

    public boolean hasEmployeeIdentity(String employeeId, String firstName, String lastName) {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.attributeToBe(
                By.cssSelector("input[name='firstName']"), "value", firstName));
        return lastName.equals(driver.findElement(By.cssSelector("input[name='lastName']")).getAttribute("value"))
                && employeeId.equals(driver.findElement(By.xpath(
                        "//label[normalize-space()='Employee Id']/following::input[1]")).getAttribute("value"));
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

    public void reloadJobDetails() {
        driver.navigate().refresh();
    }

    public boolean hasJobTitle(String title) {
        try {
            return title.equals(selectValue("Job Title"));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasJobDetails(String joined, String title, String category, String unit, String location, String status) {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.attributeToBe(
                By.xpath("//label[normalize-space()='Joined Date']/following::input[1]"), "value", joined));
        return selectValue("Job Title").equals(title) && selectValue("Job Category").equals(category)
                && selectValue("Sub Unit").equals(unit) && selectValue("Location").equals(location)
                && selectValue("Employment Status").equals(status);
    }

    private String selectValue(String label) {
        return driver.findElement(By.xpath("//label[normalize-space()='" + label
                + "']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text-input')]"))
                .getText().trim();
    }

    public boolean hasSalary(String name, String amount, String currency, String frequency) {
        waitUtils.waitForTable(driver);
        // Amount nằm sau checkbox và Salary Component (đã đối chiếu source EmployeeSalary.vue).
        for (WebElement row : driver.findElements(By.cssSelector(".oxd-table-card"))) {
            var cells = row.findElements(By.cssSelector(".oxd-table-cell"));
            if (cells.size() > 4 && cells.get(1).getText().trim().equals(name)) {
                return new java.math.BigDecimal(cells.get(2).getText().trim().replace(",", ""))
                        .compareTo(new java.math.BigDecimal(amount)) == 0
                        && cells.get(3).getText().equals(currency) && cells.get(4).getText().equals(frequency);
            }
        }
        return false;
    }

    public boolean hasSupervisor(String name, String method) {
        Duration original = driver.manage().timeouts().getImplicitWaitTimeout();
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        try {
            if (driver.findElements(By.cssSelector(".oxd-table-header")).isEmpty()) {
                return false;
            }
            return componentLocator.checkTable(driver, "Assigned Supervisors", Map.of("name", name, "method", method));
        } finally {
            driver.manage().timeouts().implicitlyWait(original);
        }
    }

    public boolean isEmploymentTerminated() {
        return driver.findElement(By.xpath("//button[normalize-space()='Activate Employment']")).isDisplayed();
    }

    public void openTerminationDetails() {
        waitUtils.waitElementClick(driver, driver.findElement(By.cssSelector(".orangehrm-terminate-date")));
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> !getTerminationDate().isBlank());
    }

    public String getTerminationDate() {
        return driver.findElement(By.xpath("//label[normalize-space()='Termination Date']/following::input[1]"))
                .getDomProperty("value");
    }

    public String getTerminationReason() {
        return driver.findElement(By.xpath("//label[normalize-space()='Termination Reason']/following::div[contains(@class,'oxd-select-text-input')][1]"))
                .getText();
    }

    public EmployeeListPage clickEmployeeList(String tab) {
        componentLocator.clickDropdownNavi(driver, tab, "");
        return new EmployeeListPage(driver);
    }
}
