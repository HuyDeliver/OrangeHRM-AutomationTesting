package com.hrm.TestCase.PIMTab;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.PIMPage.EmployeeListPage;
import com.hrm.Util.ExelReader;
import com.hrm.Util.Log;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class SearchEmployeeFunctionality extends TestBase {
    @Epic("PIM tab")
    @Feature("Quản lý employee")
    @Test(description = "OHR23: search employee using data-driven", dataProvider = "searchingEmployee")
    public void searchEmployee(String name, String id, String status,
            String include, String supervisor,
            String jobTitle, String subUnit) {

        try {
            TestUtil.employeeUtil();
            EmployeeListPage employeeListPage = new EmployeeListPage(driver);
            Assert.assertTrue(employeeListPage.isEmployeeListVisible(), "Không vào được employee list");

            employeeListPage.fillSearchEmployee(name, id, status, include, supervisor, jobTitle, subUnit);

            employeeListPage.searchEmployee();

            // Thêm log để xác nhận đã chạy xong 1 dòng Excel
            Log.info("---> BÀI TEST THÀNH CÔNG CHO DÒNG NÀY <---");

        } catch (Exception e) {
            Log.error("LỖI TẠI DÒNG NÀY: " + e.getMessage());
            Assert.fail("Test case thất bại do lỗi: " + e.getMessage());
        }
    }

    @DataProvider(name = "searchingEmployee")
    public Object[][] getSearchingData() {
        String path = "src/test/resources/data/Datadriven-OrangeHrm.xlsx";
        String sheetName = "Employee";
        return ExelReader.getDataFromExel(path, sheetName);
    }

}
