package com.hrm.TestCase.MyInfoTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.MyInfoPage.EmergencyContactPage;
import com.hrm.Pages.MyInfoPage.PersonalDetailPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class DeleteEmergencyContactFunctionality extends TestBase {
    @Epic("MyInfo")
    @Feature("Emergency Contact")
    @Test(priority = 1, dependsOnGroups = "myinfo-contact", description = "OHR35: Delete Emergency")
    public void deleteEmergencyContact() {
        TestUtil.myInfoUtil();
        PersonalDetailPage personalDetailPage = new PersonalDetailPage(driver);
        Assert.assertTrue(personalDetailPage.isPersonalDetailVisible("Personal Details"), "Không vào đc trang");
        EmergencyContactPage emergencyContactPage = personalDetailPage
                .goToEmergencyContactPage("Emergency Contacts");
        Assert.assertTrue(emergencyContactPage.isEmergencyContactVisible("Assigned Emergency Contacts"),
                "Không vào được trang");
        emergencyContactPage.DeleteEmergencyContact(TestDataShare.EMERGENCY_NAME);
                Assert.assertTrue(emergencyContactPage.isToastDeleteVisible(), "Delete không thành công");
                Assert.assertFalse(emergencyContactPage.hasContact(TestDataShare.EMERGENCY_NAME), "Record vẫn còn sau Delete và refresh");
        Log.info("Xóa emergency contact thành công");
    }
}
