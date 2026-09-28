package com.hrm.TestCase.MyInfoTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.MyInfoPage.EmergencyContactPage;
import com.hrm.Pages.MyInfoPage.PersonalDetailPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("PIM")
@Feature("My Info")
public class AddEmergencyContactFunctionality extends TestBase {

        @Epic("MyInfo")
        @Feature("Emergency Contact")
        @Test(description = "OHR33: Add Emergency Contact")
        public void saveMyInfoDetails() {
                TestUtil.myInfoUtil();
                PersonalDetailPage personalDetailPage = new PersonalDetailPage(driver);
                Assert.assertTrue(personalDetailPage.isPersonalDetailVisible("Personal Details"), "Không vào đc trang");
                EmergencyContactPage emergencyContactPage = personalDetailPage
                                .goToEmergencyContactPage("Emergency Contacts");

                Assert.assertTrue(emergencyContactPage.isEmergencyContactVisible("Assigned Emergency Contacts"),
                                "Không vào được trang");
                emergencyContactPage.clickAdd("Assigned Emergency Contacts");
                emergencyContactPage.fillEmergencyContacts(TestConfig.userName, TestConfig.relationShip,
                                TestConfig.homePhone, TestConfig.mobilePhone, TestConfig.workPhone);
                emergencyContactPage.clicksave();
                Assert.assertTrue(emergencyContactPage.isAddEmergencyContactSuccess("Add emergency contact"),
                                "Add không thành công");
                Log.info("Add Emergency Contact success");
        }
}
