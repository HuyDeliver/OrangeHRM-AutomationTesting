package com.hrm.TestCase.PIMTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.PIMPage.AddEmployeePage;
import com.hrm.Pages.PIMPage.EmployeeListPage;
import com.hrm.Util.TestConfig;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class AddEmployeeFunctionality extends TestBase {
    @Epic("PIM tab")
    @Feature("Quản lý employee")
    @Test(description = "OHR19: Add new employee")
    public void addNewEmployee() {
        TestUtil.employeeUtil();
        EmployeeListPage employeeListPage = new EmployeeListPage(driver);
        Assert.assertTrue(employeeListPage.isEmployeeListVisible(), "Không vào được employee list");
        AddEmployeePage addEmployeePage = employeeListPage.goToAddEmployee();
        Assert.assertTrue(addEmployeePage.isAddEmployeeVisible(), "Không vào được trang add employee");
        addEmployeePage.infoNewEmployee(TestConfig.image, TestConfig.firstName, TestDataShare.LASTNAME,
                TestConfig.middleName,
                TestDataShare.IDEMPLOYEE);

        addEmployeePage.createLoginDetail(TestDataShare.USERNAME, TestConfig.statusDisabled, TestConfig.password,
                TestConfig.confirmPassword);

        Assert.assertTrue(addEmployeePage.isCreateNewEmployeeSuccess(), "Add new employee not success");
    }

}
