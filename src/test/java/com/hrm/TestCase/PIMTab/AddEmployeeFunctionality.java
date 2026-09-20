package com.hrm.TestCase.PIMTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.PIMPage.AddEmployeePage;
import com.hrm.Pages.PIMPage.EmployeeListPage;

public class AddEmployeeFunctionality extends TestBase {
    @Test
    public void addNewEmployee() {
        TestUtil.employeeUtil();
        EmployeeListPage employeeListPage = new EmployeeListPage(driver);
        Assert.assertTrue(employeeListPage.isEmployeeListVisible(), "Không vào được employee list");
        AddEmployeePage addEmployeePage = employeeListPage.goToAddEmployee();
        Assert.assertTrue(addEmployeePage.isAddEmployeeVisible(), "Không vào được trang add employee");
        addEmployeePage.infoNewEmployee("Nguyễn", "Huy", "Bách", "2003");
    }

}
