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

@Epic("PIM tab")
@Feature("Quản lý employee")
public class TerminateEmployeeFunctionality extends TestBase {
    private EmployeeListPage employeeListPage;
    private EditEmployeePage editEmployeePage;
    private boolean employeeCreated;
    private final String employeeId = "T" + TestDataShare.IDEMPLOYEE;

    @BeforeMethod
    public void addEmployee() {
        employeeCreated = false;
        TestUtil.employeeUtil();
        employeeListPage = new EmployeeListPage(driver);
        AddEmployeePage addEmployeePage = employeeListPage.goToAddEmployee();
        addEmployeePage.infoNewEmployee(TestConfig.image, TestConfig.firstName, TestDataShare.LASTNAME,
                TestConfig.middleName,
                employeeId);

        addEmployeePage.saveEmployee();
        employeeCreated = addEmployeePage.isCreateNewEmployeeSuccess();
        Assert.assertTrue(employeeCreated, "Setup: không tạo được employee riêng để Terminate");
        editEmployeePage = new EditEmployeePage(driver);
    }

    @Test(description = "OHR25: Terminate Employee")
    public void terminatEmployee() {
        Assert.assertTrue(editEmployeePage.isPersonalDetailVisible("Personal Details"),
                "Không vào được trang edit employee");
        editEmployeePage.goToJobDetail("Job");
        editEmployeePage.clickTerminateEmployee("Employee Termination / Activiation");
        editEmployeePage.fillTerrminateReason(TestConfig.terminateDate, TestConfig.terminateReason, "Nothing");
        editEmployeePage.saveTerminate("Terminate employee");
        editEmployeePage.reloadJobDetails();
        Assert.assertTrue(editEmployeePage.isEmploymentTerminated(),
                "Sau refresh chưa có nút Activate Employment");
        editEmployeePage.openTerminationDetails();
        Assert.assertEquals(editEmployeePage.getTerminationDate(), TestConfig.terminateDate,
                "Ngày Terminate đã lưu không đúng input");
        Assert.assertEquals(editEmployeePage.getTerminationReason(), TestConfig.terminateReason,
                "Lý do Terminate đã lưu không đúng input");
        Log.info("OHR25: Trạng thái, ngày và lý do Terminate được giữ sau refresh");
    }

    @AfterMethod(alwaysRun = true)
    public void deleteEmployee() {
        if (!employeeCreated) {
            return;
        }
        // Refresh đóng popup chi tiết, kể cả khi assertion phía trên FAIL.
        editEmployeePage.reloadJobDetails();
        employeeListPage = editEmployeePage.clickEmployeeList("Employee List");
        employeeListPage.fillSearchEmployee("", employeeId, "", "Current and Past Employees", "",
                "", "");
        employeeListPage.searchEmployee();
        employeeListPage.deleteEmployee(employeeId);
        Assert.assertTrue(employeeListPage.isDeleteEmployeeSuccess("Cleanup employee"),
                "Cleanup: không xóa được employee của OHR25");
    }
}
