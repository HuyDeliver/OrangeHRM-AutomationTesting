package com.hrm.TestCase.MyInfoTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.MyInfoPage.EmergencyContactPage;
import com.hrm.Pages.MyInfoPage.PersonalDetailPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.TestDataShare;
import com.hrm.Util.componentLocator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class AddEmergencyContactFunctionality extends TestBase {

        @Epic("MyInfo")
        @Feature("Emergency Contact")
        @Test(groups = "myinfo-contact", description = "OHR34: Add Emergency Contact")
        public void addEmergencyContact() {
                TestUtil.myInfoUtil();
                PersonalDetailPage personalDetailPage = new PersonalDetailPage(driver);
                Assert.assertTrue(personalDetailPage.isPersonalDetailVisible("Personal Details"), "Không vào đc trang");
                EmergencyContactPage emergencyContactPage = personalDetailPage
                                .goToEmergencyContactPage("Emergency Contacts");

                Assert.assertTrue(emergencyContactPage.isEmergencyContactVisible("Assigned Emergency Contacts"),
                                "Không vào được trang");
                emergencyContactPage.clickAdd("Assigned Emergency Contacts");
                emergencyContactPage.fillEmergencyContacts(TestDataShare.EMERGENCY_NAME, TestConfig.relationShip,
                                TestConfig.homePhone, TestConfig.mobilePhone, TestConfig.workPhone);
                emergencyContactPage.clicksave();
                Assert.assertTrue(emergencyContactPage.isCallToActionSuccess("Add emergency contact"),
                                "Add không thành công");
                Assert.assertTrue(emergencyContactPage.hasContact(TestDataShare.EMERGENCY_NAME), "Không tìm lại được record vừa thêm");
                componentLocator.takeScreenshotTable(driver, "Add emergency contact");
                Log.info("Add Emergency Contact success");
        }
}
