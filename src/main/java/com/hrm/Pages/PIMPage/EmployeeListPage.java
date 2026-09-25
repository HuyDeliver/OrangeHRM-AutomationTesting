package com.hrm.Pages.PIMPage;

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

}
