package com.hrm.Pages.PIMPage;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

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
            String include, String supervisor,
            String jobTitle, String subUnit) {
        return componentLocator.checkTable(driver, "Search employee", Map.of("name", name, "id", id, "status",
                status, "include", include, "supervisor", supervisor, "jobTitle", jobTitle, "subUnit", subUnit));
    }

    private boolean isValid(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public void searchEmployee() {
        componentLocator.clickSearch(driver);
    }

    public boolean checkSearchTerminate(String note) {
        return componentLocator.checkTable(driver, "Search pass empolyee", Map.of("note", note));
    }

}
