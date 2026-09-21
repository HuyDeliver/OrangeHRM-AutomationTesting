package com.hrm.TestCase.PIMTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.PIMPage.AddEmployeePage;
import com.hrm.Pages.PIMPage.EmployeeListPage;
import com.hrm.Util.TestConfig;

public class AddEmployeeFunctionality extends TestBase {
    @Test
    public void addNewEmployee() {
        TestUtil.employeeUtil();
        EmployeeListPage employeeListPage = new EmployeeListPage(driver);
        Assert.assertTrue(employeeListPage.isEmployeeListVisible(), "Không vào được employee list");
        AddEmployeePage addEmployeePage = employeeListPage.goToAddEmployee();
        Assert.assertTrue(addEmployeePage.isAddEmployeeVisible(), "Không vào được trang add employee");
        addEmployeePage.infoNewEmployee(TestConfig.firstName, TestConfig.lastName, TestConfig.middleName,
                TestConfig.employeeID);

        addEmployeePage.createLoginDetail(TestConfig.userName, TestConfig.statusDisabled, TestConfig.password,
                TestConfig.confirmPassword);

        Assert.assertTrue(addEmployeePage.isCreateNewEmployeeSuccess(), "Add new employee not success");
    }

}
