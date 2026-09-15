package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.EditLocationPage;
import com.hrm.Pages.AdminPage.LocationPage;
import com.hrm.Util.Log;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("Module organization")
@Feature("Quản lý location")
public class EditLocationFunctionality extends TestBase {
    @Test
    public void editCityinLocation() {
        TestUtil.organizeUtil();
        LocationPage locationPage = new LocationPage(driver);
        Assert.assertTrue(locationPage.isTitleLocationVisible(), "Không vào được trang Location");

        EditLocationPage editLocationPage = locationPage.clickEditLocation();

        Assert.assertTrue(editLocationPage.isTitleEditVisible(), "Không vào được trang Edit Location");
        editLocationPage.editCity("Hồ Chí Minh");
        Assert.assertTrue(editLocationPage.isEditLocationSuccess(), "Edit không thành công");
        Log.info("Edit City thành công");
    }
}
