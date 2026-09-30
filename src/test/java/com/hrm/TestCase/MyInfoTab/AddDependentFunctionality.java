package com.hrm.TestCase.MyInfoTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.MyInfoPage.DependentPage;
import com.hrm.Pages.MyInfoPage.PersonalDetailPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class AddDependentFunctionality extends TestBase {

        @Epic("MyInfo")
        @Feature("Dependent")
        @Test(description = "OHR36: Add Dependent")
        public void addDependent() {
                TestUtil.myInfoUtil();
                PersonalDetailPage personalDetailPage = new PersonalDetailPage(driver);
                Assert.assertTrue(personalDetailPage.isPersonalDetailVisible("Personal Details"), "Không vào đc trang");
                DependentPage DependentPage = personalDetailPage
                                .goToDependentPage("Dependents");

                Assert.assertTrue(DependentPage.isDependentVisible("Assigned Dependents"),
                                "Không vào được trang");
                DependentPage.clickAdd("Assigned Dependents");
                DependentPage.fillDependents(TestDataShare.EMERGENCY_NAME, "Child",
                                TestConfig.dateOfBirth);
                DependentPage.clicksave();
                Assert.assertTrue(DependentPage.isCallToActionSuccess("Add Dependent"),
                                "Add không thành công");
                Log.info("Add Dependent success");
        }
}
