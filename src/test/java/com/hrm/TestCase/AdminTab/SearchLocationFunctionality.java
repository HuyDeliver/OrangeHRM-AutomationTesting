package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.LocationPage;
import com.hrm.Util.ExelReader;
import com.hrm.Util.Log;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class SearchLocationFunctionality extends TestBase {
    @Epic("Module AdminTab")
    @Feature("Quản lý location")
    @Test(dataProvider = "searchingLocation", description = "OHR15: Search location with City", dependsOnGroups = {
            "Location-test" })
    public void searchLocationWithCity(String name, String city, String country, String expectedFound) {
        Assert.assertTrue("true".equalsIgnoreCase(expectedFound) || "false".equalsIgnoreCase(expectedFound),
                "Excel: expectedFound phải là true hoặc false");
        TestUtil.organizeUtil();
        LocationPage locationPage = new LocationPage(driver);
        Assert.assertTrue(locationPage.isTitleLocationVisible(), "Không vào được trang Location");
        locationPage.searchLocation(name, city, country);
        Assert.assertTrue(locationPage.isSearchLocationSuccess(name, city, country,
                Boolean.parseBoolean(expectedFound)), "Kết quả Location không đúng expectedFound=" + expectedFound
                        + " | name=" + name + ", city=" + city + ", country=" + country);
        Log.info("OHR15: Kết quả tìm Location đúng expectedFound=" + expectedFound);
    }

    @DataProvider(name = "searchingLocation")
    public Object[][] getSearchingData() {
        String path = "src/test/resources/data/Datadriven-OrangeHrm.xlsx";
        String sheetName = "Search Location";
        return ExelReader.getDataFromExel(path, sheetName);
    }
}
