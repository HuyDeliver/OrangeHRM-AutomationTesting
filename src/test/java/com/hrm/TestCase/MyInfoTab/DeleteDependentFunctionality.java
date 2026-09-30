package com.hrm.TestCase.MyInfoTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.MyInfoPage.DependentPage;
import com.hrm.Pages.MyInfoPage.PersonalDetailPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class DeleteDependentFunctionality extends TestBase {
        @Epic("MyInfo")
        @Feature("Dependent")
        @Test(description = "OHR37: Delete Dependent")
        public void deleteDependent() {
                TestUtil.myInfoUtil();
                PersonalDetailPage personalDetailPage = new PersonalDetailPage(driver);
                Assert.assertTrue(personalDetailPage.isPersonalDetailVisible("Personal Details"), "Không vào đc trang");
                DependentPage DependentPage = personalDetailPage
                                .goToDependentPage("Dependent");
                Assert.assertTrue(DependentPage.isDependentVisible("Assigned Dependent"),
                                "Không vào được trang");
                DependentPage.DeleteDependent(TestDataShare.EMERGENCY_NAME);
                Log.info("Xóa Dependent thành công");
        }
}
