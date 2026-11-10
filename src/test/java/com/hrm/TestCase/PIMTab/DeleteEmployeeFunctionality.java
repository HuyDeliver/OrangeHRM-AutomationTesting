package com.hrm.TestCase.PIMTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.PIMPage.EmployeeListPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestDataShare;
import com.hrm.Util.componentLocator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class DeleteEmployeeFunctionality extends TestBase {
    @Epic("PIM tab")
    @Feature("Quản lý employee")
    @Test(priority = 3, description = "OHR24: Delete employee", dependsOnGroups = "pim-employee")
    public void deleteEmployee() {
        TestUtil.employeeUtil();
        EmployeeListPage employeeListPage = new EmployeeListPage(driver);
        Assert.assertTrue(employeeListPage.isEmployeeListVisible(), "Không vào được trang employeeList");
        Assert.assertEquals(employeeListPage.searchByEmployeeIdAndCount(TestDataShare.IDEMPLOYEE), 1, "Không tìm được employee cần xóa");
        employeeListPage.deleteEmployee(TestDataShare.IDEMPLOYEE);
        Assert.assertTrue(employeeListPage.isDeleteEmployeeSuccess("Delete employee"), "Delete không thành công");
        Assert.assertEquals(employeeListPage.searchByEmployeeIdAndCount(TestDataShare.IDEMPLOYEE), 0, "Employee vẫn còn sau xóa");
        componentLocator.takeScreenshotTable(driver, "Delete employee");
        Log.info("Xóa employee thành công");
    }

}
