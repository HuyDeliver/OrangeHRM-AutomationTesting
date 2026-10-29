package com.hrm.Pages.PIMPage;

import java.util.Map;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.componentLocator;

public class EmployeeListPage {
    private WebDriver driver;

    public EmployeeListPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isEmployeeListVisible() {
        return componentLocator.checkTitleH5(driver, "EmployeeList");
    }

    public AddEmployeePage goToAddEmployee() {
        componentLocator.clickButtonAdd(driver);
        return new AddEmployeePage(driver);
    }

    public void deleteEmployee(String id) {
        componentLocator.deleteButtonTable(driver, id);
    }

    public boolean isDeleteEmployeeSuccess(String name) {
        return componentLocator.checkToasstSuccess(driver, name);
    }

    public EditEmployeePage goToeEditEmployeePage(String id) {
        componentLocator.editButtonTable(driver, id);
        return new EditEmployeePage(driver);
    }

    // Check search
    public void fillSearchEmployee(String name, String id, String status,
            String include, String supervisor,
            String jobTitle, String subUnit) {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        if (isValid(name)) {
            String keyName = name.trim().split("\\s+")[0];
            componentLocator.inputDropdown(driver, "Employee Name", keyName, name);
        }
        if (isValid(id)) {
            componentLocator.fillInput(driver, "Employee Id", id);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (isValid(status)) {
            componentLocator.chooseSelectAction(driver, "Employment Status", status);
        }
        if (isValid(include) && !include.equalsIgnoreCase("Current Employees Only")) {
            componentLocator.chooseSelectAction(driver, "Include", include);
        }
        if (isValid(supervisor)) {
            String keySupervisor = supervisor.trim().split("\\s+")[0]; // Lấy từ đầu tiên của tên Supervisor
            componentLocator.inputDropdown(driver, "Supervisor Name", keySupervisor, supervisor);
        }
        if (isValid(jobTitle)) {
            componentLocator.chooseSelectAction(driver, "Job Title", jobTitle);
        }
        if (isValid(subUnit)) {
            componentLocator.chooseSelectAction(driver, "Sub Unit", subUnit);
        }
    }

    public boolean checkSearch(String name, String id, String status,
            String supervisor, String jobTitle, boolean expectedFound) {
        if (!expectedFound) {
            return componentLocator.checkNoRecordFound(driver);
        }
        // Include không phải cột dữ liệu; Sub Unit cha có thể trả về các đơn vị con.
        return componentLocator.checkTable(driver, "Search employee", Map.of("name", name, "id", id, "status",
                status, "supervisor", supervisor, "jobTitle", jobTitle));
    }

    private boolean isValid(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public void searchEmployee() {
        componentLocator.clickSearch(driver);
    }

    // Tìm cả Current/Past để không tạo lại một employee đã terminated.
    public int searchByEmployeeIdAndCount(String employeeId) {
        fillSearchEmployee("", employeeId, "", "Current and Past Employees", "", "", "");
        By summary = By.xpath("//div[contains(@class,'orangehrm-horizontal-padding')]/child::span");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement oldResult = wait.until(ExpectedConditions.visibilityOfElementLocated(summary));
        searchEmployee();
        wait.until(ExpectedConditions.stalenessOf(oldResult));
        String result = wait.until(ExpectedConditions.visibilityOfElementLocated(summary)).getText();
        if (result.equals("No Records Found")) return 0;
        if (!result.matches("\\(\\d+\\) Records? Found")) {
            throw new IllegalStateException("Không đọc được kết quả Employee: " + result);
        }
        return Integer.parseInt(result.replaceAll("\\D", ""));
    }

    public boolean checkSearchTerminate(String note) {
        return componentLocator.checkTable(driver, "Search pass empolyee", Map.of("note", note));
    }

}
