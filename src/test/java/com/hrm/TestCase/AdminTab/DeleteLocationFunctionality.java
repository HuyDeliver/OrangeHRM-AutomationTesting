package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.LocationPage;
import com.hrm.Util.Log;

public class DeleteLocationFunctionality extends TestBase {
    @Test
    public void deleteLocationSuccess() {
        TestUtil.organizeUtil();
        LocationPage locationPage = new LocationPage(driver);
        Assert.assertTrue(locationPage.isTitleLocationVisible(), "Không vào được trang Location");
        locationPage.deleteLocation();
        Assert.assertTrue(locationPage.isDeleteLocationSuccess(), "Delete không thành công");

        Log.info("Xóa thành công");
    }
}
