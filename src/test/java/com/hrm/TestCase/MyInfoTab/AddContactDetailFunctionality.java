package com.hrm.TestCase.MyInfoTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.MyInfoPage.ContactDetailPage;
import com.hrm.Pages.MyInfoPage.PersonalDetailPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class AddContactDetailFunctionality extends TestBase {
        @Epic("MyInfo")
        @Feature("Contact Details")
        @Test(description = "OHR32: Add Contact Details")
        public void saveMyInfoDetails() {
                TestUtil.myInfoUtil();
                PersonalDetailPage personalDetailPage = new PersonalDetailPage(driver);
                Assert.assertTrue(personalDetailPage.isPersonalDetailVisible("Personal Details"),
                                "KHông vào được personal detail");
                ContactDetailPage contactDetailpage = personalDetailPage.goToContactDetailpage("Contact Details");
                Assert.assertTrue(contactDetailpage.isContactDetailVisible("Contact Details"),
                                "Không vào đc trang contact detail");
                contactDetailpage.fillContactDetails(TestConfig.street1, TestConfig.street2, TestConfig.cityName,
                                TestConfig.provinceName, TestConfig.postalCode, TestConfig.countryName,
                                TestConfig.homePhone,
                                TestConfig.mobilePhone, TestConfig.otherEmail);
                contactDetailpage.clicksave();
                Assert.assertTrue(contactDetailpage.isAddContactDetailSuccess("Add Contact detail"),
                                "Add Contact không thành công");
                Log.info("Add Contact detail success");
        }
}
