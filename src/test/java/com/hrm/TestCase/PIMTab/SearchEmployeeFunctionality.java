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

        TestUtil.employeeUtil();
        EmployeeListPage employeeListPage = new EmployeeListPage(driver);
        Assert.assertTrue(employeeListPage.isEmployeeListVisible(), "Không vào được employee list");

        employeeListPage.fillSearchEmployee(name, id, status, include, supervisor, jobTitle, subUnit);

        employeeListPage.searchEmployee();

        if (employeeListPage.checkSearch(name, id, status, include, supervisor, jobTitle, subUnit)) {
            Log.info("Không tìm thấy bản ghi");
        } else {
            Log.info("Tìm thấy bản ghi");
        }
    }

    @DataProvider(name = "searchingEmployee")
    public Object[][] getSearchingData() {
        String path = "src/test/resources/data/Datadriven-OrangeHrm.xlsx";
        String sheetName = "Employee";
        return ExelReader.getDataFromExel(path, sheetName);
    }

}
