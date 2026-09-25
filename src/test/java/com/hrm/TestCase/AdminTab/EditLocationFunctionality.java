package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.EditLocationPage;
import com.hrm.Pages.AdminPage.LocationPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class EditLocationFunctionality extends TestBase {
    @Epic("Module AdminTab")
    @Feature("Quản lý location")
    @Test(description = "OHR14: Edit Location", dependsOnGroups = { "Location-test" })
    public void editCityinLocation() {
        TestUtil.organizeUtil();
        LocationPage locationPage = new LocationPage(driver);
        Assert.assertTrue(locationPage.isTitleLocationVisible(), "Không vào được trang Location");

        EditLocationPage editLocationPage = locationPage.clickEditLocation(TestDataShare.LOCATION);

        Assert.assertTrue(editLocationPage.isTitleEditVisible(), "Không vào được trang Edit Location");
        editLocationPage.editCity("Hồ Chí Minh");
        Assert.assertTrue(editLocationPage.isEditLocationSuccess(), "Edit không thành công");
        Log.info("Edit City thành công");
    }
}
