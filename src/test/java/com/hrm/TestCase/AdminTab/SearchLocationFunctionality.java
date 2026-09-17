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
    @Epic("Module organization")
    @Feature("Quản lý location")
    @Test(dataProvider = "searchingLocation", description = "ỌHR14: Search location with City", priority = 10)
    public void searchLocationWithCity(String name, String city, String country) {
        TestUtil.organizeUtil();
        LocationPage locationPage = new LocationPage(driver);
        Assert.assertTrue(locationPage.isTitleLocationVisible(), "Không vào được trang Location");
        locationPage.searchLocationFolowingCountry(name, city, country);
        if (locationPage.isSearchLocationSuccess(country) == true) {
            Log.info("Tìm thấy bản ghi");
        } else {
            Log.info("Không tìm thấy bản ghi");
        }
    }

    @DataProvider(name = "searchingLocation")
    public Object[][] getSearchingData() {
        String path = "src/test/resources/data/Datadriven-OrangeHrm.xlsx";
        String sheetName = "Search Location";
        return ExelReader.getDataFromExel(path, sheetName);
    }
}
