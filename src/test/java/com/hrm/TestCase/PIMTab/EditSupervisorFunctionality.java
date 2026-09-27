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

public class EditSupervisorFunctionality extends TestBase {
        @Epic("PIM tab")
        @Feature("Quản lý employee")
        @Test(description = "OHR22: Edit salary of employee")
        public void editEmployeeSupervisor() {
                TestUtil.employeeUtil();
                EmployeeListPage employeeListPage = new EmployeeListPage(driver);
                Assert.assertTrue(employeeListPage.isEmployeeListVisible(), "Không vào được trang employeeList");
                EditEmployeePage editEmployeePage = employeeListPage.goToeEditEmployeePage(TestDataShare.IDEMPLOYEE);
                Assert.assertTrue(editEmployeePage.isPersonalDetailVisible("Personal Details"),
                                "Không vào được trang edit employee");
                editEmployeePage.goToReportTo("Report-to");
                Assert.assertTrue(editEmployeePage.isReportToVisible("Report to"),
                                "không vào được trang report");
                editEmployeePage.addSupervisor("Assigned Supervisors");
                editEmployeePage.fillSupervisor("Huy", TestConfig.nameSupervisor, TestConfig.reportMethod);
                editEmployeePage.saveInfo();

                Assert.assertTrue(editEmployeePage.isEditSuccess("Fill Supervisor"),
                                "Nhập Supervisor không thành công");
                Log.info("Edit supervisor thành công");
        }
}
