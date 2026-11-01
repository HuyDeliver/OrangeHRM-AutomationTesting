package com.hrm.TestCase.PIMTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.PIMPage.EditEmployeePage;
import com.hrm.Pages.PIMPage.EmployeeListPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class EditSalaryFunctionality extends TestBase {
        @Epic("PIM tab")
        @Feature("Quản lý employee")
        @Test(priority = 1, description = "OHR21: Edit salary of employee", dependsOnGroups = "pim-employee")
        public void editEmployeeSalary() {
                TestUtil.employeeUtil();
                EmployeeListPage employeeListPage = new EmployeeListPage(driver);
                Assert.assertTrue(employeeListPage.isEmployeeListVisible(), "Không vào được trang employeeList");
                Assert.assertEquals(employeeListPage.searchByEmployeeIdAndCount(TestDataShare.IDEMPLOYEE), 1, "Không tìm được employee của lượt chạy");
                EditEmployeePage editEmployeePage = employeeListPage.goToeEditEmployeePage(TestDataShare.IDEMPLOYEE);
                Assert.assertTrue(editEmployeePage.isPersonalDetailVisible("Personal Details"),
                                "Không vào được trang edit employee");
                editEmployeePage.goToReportTo("Salary");
                Assert.assertTrue(editEmployeePage.isSalaryVisible("Assigned Salary Components"),
                                "không vào được trang salary");
                editEmployeePage.addSalary("Assigned Salary Components");
                editEmployeePage.fillSalaryInfo(TestConfig.salary, TestConfig.payGrade,
                                TestConfig.payFrequency,
                                TestConfig.currency, TestConfig.amount, TestConfig.comment);
                editEmployeePage.saveInfo("edit salary");

                Assert.assertTrue(editEmployeePage.isEditSuccess("Fill Salary"),
                                "Nhập Salary không thành công");
                driver.navigate().refresh();
                Assert.assertTrue(editEmployeePage.hasSalary(TestConfig.salary, TestConfig.amount,
                                TestConfig.currency, TestConfig.payFrequency), "Salary đã lưu không đúng input");
                Log.info("Edit salary thành công");
        }
}
