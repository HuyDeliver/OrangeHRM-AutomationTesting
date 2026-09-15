package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.AddLocationPage;
import com.hrm.Pages.AdminPage.LocationPage;
import com.hrm.Util.TestConfig;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("Module organization")
@Feature("Quản lý location")
public class AddLocationFunctionality extends TestBase {
    @Test
    public void addLocationSuccess() {
        TestUtil.organizeUtil();
        LocationPage locationPage = new LocationPage(driver);
        Assert.assertTrue(locationPage.isTitleLocationVisible(), "Không vào được trang Location");
        AddLocationPage addLocationPage = locationPage.goToAddLocationPage();
        Assert.assertTrue(addLocationPage.isTitleAddLocationVisible(), "Không vào được trang Add Location");
        addLocationPage.fillLocatinSpecific(TestConfig.locationName, TestConfig.cityName, TestConfig.provinceName,
                TestConfig.postalCode, TestConfig.countryName, TestConfig.phoneNumber, TestConfig.faxNumber,
                TestConfig.cityName, TestConfig.locationNote);
        addLocationPage.saveLocation();
        Assert.assertTrue(addLocationPage.isAddLocationSucess(), "Add Location không thành công");
    }
}
