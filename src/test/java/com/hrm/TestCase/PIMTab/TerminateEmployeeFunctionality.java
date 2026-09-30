package com.hrm.TestCase.PIMTab;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.PIMPage.AddEmployeePage;
import com.hrm.Pages.PIMPage.EditEmployeePage;
import com.hrm.Pages.PIMPage.EmployeeListPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class TerminateEmployeeFunctionality extends TestBase {
    private EmployeeListPage employeeListPage;
    private EditEmployeePage editEmployeePage;

    @Epic("PIM tab")
    @Feature("Quản lý employee")
    @BeforeMethod
    public void addEmployee() {
        TestUtil.employeeUtil();
        employeeListPage = new EmployeeListPage(driver);
        AddEmployeePage addEmployeePage = employeeListPage.goToAddEmployee();
        addEmployeePage.infoNewEmployee(TestConfig.image, TestConfig.firstName, TestDataShare.LASTNAME,
                TestConfig.middleName,
                TestDataShare.IDEMPLOYEE);

        addEmployeePage.createLoginDetail(TestDataShare.USERNAME, TestConfig.statusDisabled, TestConfig.password,
                TestConfig.confirmPassword);
    }

    @Test(description = "OHR25: Terminate Employee")
    public void terminatEmployee() {
        editEmployeePage = new EditEmployeePage(driver);
        Assert.assertTrue(editEmployeePage.isPersonalDetailVisible("Personal Details"),
                "Không vào được trang edit employee");
        editEmployeePage.goToJobDetail("Job");
        editEmployeePage.clickTerminateEmployee("Employee Termination / Activiation");
        editEmployeePage.fillTerrminateReason(TestConfig.terminateDate, TestConfig.terminateReason, "Nothing");
        editEmployeePage.saveTerminate("Terminate employee");
        Log.info("Terminate employee success");
    }

    @AfterMethod
    public void deleteEmployee() {
        employeeListPage = editEmployeePage.clickEmployeeList("Employee List");
        employeeListPage.fillSearchEmployee("", "", "", "Past Employees Only", "", "", "");
        employeeListPage.searchEmployee();
        employeeListPage.deleteEmployee(TestDataShare.IDEMPLOYEE);
    }
}
