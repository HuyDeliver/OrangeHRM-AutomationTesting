package com.hrm.TestCase.PIMTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.PIMPage.EmployeeListPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class DeleteEmployeeFunctionality extends TestBase {
    @Epic("PIM tab")
    @Feature("Quản lý employee")
    @Test(description = "OHR23: Delete employee")
    public void deleteEmployee() {
        TestUtil.employeeUtil();
        EmployeeListPage employeeListPage = new EmployeeListPage(driver);
        Assert.assertTrue(employeeListPage.isEmployeeListVisible(), "Không vào được trang employeeList");
        employeeListPage.deleteEmployee(TestDataShare.IDEMPLOYEE);
        Assert.assertTrue(employeeListPage.isDeleteEmployeeSuccess("Delete employee"), "Delete không thành công");

        Log.info("Xóa employee thành công");
    }

}
