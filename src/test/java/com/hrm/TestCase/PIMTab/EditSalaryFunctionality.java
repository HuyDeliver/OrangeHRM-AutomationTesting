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
        @Test(description = "OHR21: Edit salary of employee")
        public void editEmployeeSalary() {
                TestUtil.employeeUtil();
                EmployeeListPage employeeListPage = new EmployeeListPage(driver);
                Assert.assertTrue(employeeListPage.isEmployeeListVisible(), "Không vào được trang employeeList");
                EditEmployeePage editEmployeePage = employeeListPage.goToeEditEmployeePage(TestDataShare.IDEMPLOYEE);
                Assert.assertTrue(editEmployeePage.isPersonalDetailVisible("Personal Details"),
                                "Không vào được trang edit employee");
                editEmployeePage.goToReportTo("Salary");
                Assert.assertTrue(editEmployeePage.isSalaryVisible("Add Salary Component"),
                                "không vào được trang salary");
                editEmployeePage.addSalary("Assigned Salary Components");
                editEmployeePage.fillSalaryInfo(TestConfig.salary, TestConfig.payGrade,
                                TestConfig.payFrequency,
                                TestConfig.currency, TestConfig.amount, TestConfig.comment);
                editEmployeePage.saveInfo("edit salary");

                Assert.assertTrue(editEmployeePage.isEditSuccess("Fill Salary"),
                                "Nhập Salary không thành công");
                Log.info("Edit salary thành công");
        }
}
