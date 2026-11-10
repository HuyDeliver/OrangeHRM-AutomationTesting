package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.EditLocationPage;
import com.hrm.Pages.AdminPage.LocationPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestDataShare;
import com.hrm.Util.componentLocator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class EditLocationFunctionality extends TestBase {
    @Epic("Module AdminTab")
    @Feature("Quản lý location")
    @Test(priority = 2, description = "OHR14: Edit Location", dependsOnGroups = { "Location-test" })
    public void editCityinLocation() {
        TestUtil.organizeUtil();
        LocationPage locationPage = new LocationPage(driver);
        Assert.assertTrue(locationPage.isTitleLocationVisible(), "Không vào được trang Location");

        EditLocationPage editLocationPage = locationPage.clickEditLocation(TestDataShare.LOCATION);

        Assert.assertTrue(editLocationPage.isTitleEditVisible(), "Không vào được trang Edit Location");
        editLocationPage.editCity("Hồ Chí Minh");
        Assert.assertTrue(editLocationPage.isEditLocationSuccess(), "Edit không thành công");
        locationPage.searchLocation(TestDataShare.LOCATION, "Hồ Chí Minh", "");
        Assert.assertTrue(locationPage.isSearchLocationSuccess(TestDataShare.LOCATION, "Hồ Chí Minh", "", true),
                "City chưa được lưu đúng sau khi sửa");
        componentLocator.takeScreenshotTable(driver, "Edit location success");
        Log.info("Edit City thành công");
    }
}
