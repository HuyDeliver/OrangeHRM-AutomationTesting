package com.hrm.TestCase.PIMTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.PIMPage.MyInfoPage;
import com.hrm.Util.Log;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("PIM")
@Feature("My Info")
public class MyInfoFunctionality extends TestBase {

    @Test(description = "Cập nhật Personal Details, Contact Details và thêm Emergency Contact")
    public void saveMyInfoDetails() {
        TestUtil.myInfoUtil();
        MyInfoPage myInfoPage = new MyInfoPage(driver);

        Assert.assertTrue(myInfoPage.updatePersonalDetails(), "Lưu Personal Details không thành công");

        Assert.assertTrue(myInfoPage.updateContactDetails(), "Lưu Contact Details không thành công");

        Assert.assertTrue(myInfoPage.addEmergencyContact(), "Lưu Emergency Contact không thành công");

        Log.info("Đã lưu dữ liệu test cho ba mục My Info");
    }
}
