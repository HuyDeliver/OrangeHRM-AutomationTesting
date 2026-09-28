package com.hrm.TestCase.MyInfoTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.MyInfoPage.PersonalDetailPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("PIM")
@Feature("My Info")
public class AddPersonalDetailFunctionality extends TestBase {

    @Epic("MyInfo")
    @Feature("Personal Details")
    @Test(description = "OHR31: Add Personal Details")
    public void saveMyInfoDetails() {
        TestUtil.myInfoUtil();
        PersonalDetailPage personalDetailPage = new PersonalDetailPage(driver);
        Assert.assertTrue(personalDetailPage.isPersonalDetailVisible("Personal Details"),
                "KHông vào được personal detail");
        personalDetailPage.fillNewInfo(TestConfig.id, TestConfig.license, TestConfig.licenseExpired,
                "Afghan", TestConfig.maritualStatus, TestConfig.dateOfBirth, TestConfig.gender);
        personalDetailPage.clicksave();
        Assert.assertTrue(personalDetailPage.isAddPersonalDetailSuccess("Add personal detail"), "Add không thành công");
        Log.info("Add personal detail success");
    }
}
