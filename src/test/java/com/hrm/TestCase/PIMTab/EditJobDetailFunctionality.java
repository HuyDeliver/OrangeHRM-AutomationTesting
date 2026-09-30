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

public class EditJobDetailFunctionality extends TestBase {
        @Epic("PIM tab")
        @Feature("Quản lý employee")
        @Test(description = "OHR20: Edit job detail of employee")
        public void editEmployeeInfo() {
                TestUtil.employeeUtil();
                EmployeeListPage employeeListPage = new EmployeeListPage(driver);
                Assert.assertTrue(employeeListPage.isEmployeeListVisible(), "Không vào được trang employeeList");
                EditEmployeePage editEmployeePage = employeeListPage.goToeEditEmployeePage(TestDataShare.IDEMPLOYEE);
                Assert.assertTrue(editEmployeePage.isPersonalDetailVisible("Personal Details"),
                                "Không vào được trang edit employee");
                editEmployeePage.goToJobDetail("Job");

                editEmployeePage.fillJobDetailInfo(TestConfig.joinedDate, TestConfig.jobTitleName,
                                TestConfig.jobCategorize,
                                TestConfig.subUnit, TestConfig.locationName, TestConfig.employmentStatus);
                editEmployeePage.saveInfo("Edit job detail");

                Assert.assertTrue(editEmployeePage.isEditSuccess("Fill Job Detail"),
                                "Nhập job detail không thành công");
                Log.info("Edit jobdetail thành công");
        }
}
