package com.hrm.TestCase.MyInfoTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.MyInfoPage.PersonalDetailPage;
import com.hrm.Pages.MyInfoPage.QualificationPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("MyInfo")
@Feature("Issue #1928 - Education")
public class Issue1928RegressionTest extends TestBase {
    // Reported on OrangeHRM 5.8; reproduced on local 5.9.
    @Test(description = "OHR42: Issue #1928 - Education đã tồn tại phải xóa được")
    public void existingEducationShouldBeDeletedSuccessfully() {
        // Arrange
        Log.info("Bước 1: Đăng nhập và mở My Info > Qualifications");
        TestUtil.myInfoUtil();
        PersonalDetailPage personalDetailPage = new PersonalDetailPage(driver);
        Assert.assertTrue(personalDetailPage.isPersonalDetailVisible("Personal Details"), "Không vào được My Info");
        QualificationPage qualificationPage = personalDetailPage.goToQualificationPage("Qualifications");
        Assert.assertTrue(qualificationPage.isQualificationVisible(), "Không vào được Qualifications");

        String marker = "ISS1928_" + System.currentTimeMillis();
        Log.info("Bước 2: Thêm Education | Level=" + TestConfig.educationLevel + " | GPA/Score=" + marker);
        qualificationPage.clickAdd("Education");
        qualificationPage.fillEducation(TestConfig.educationLevel, marker, marker);
        qualificationPage.clicksave();
        Assert.assertTrue(qualificationPage.isCallToActionSuccess("Add Education"), "Không lưu được Education");
        qualificationPage.waitForEducation(marker);

        // Act
        Log.info("Bước 3: Xóa đúng Education vừa thêm và xác nhận");
        qualificationPage.deleteEducation(marker);
        Log.info("Bước 4: Refresh Qualifications và đợi bảng tải xong");
        qualificationPage.reloadQualification();

        // Assert
        boolean stillExists = qualificationPage.isEducationVisible(marker);
        Log.info("Bước 5: Expected: không còn bản ghi | Actual: stillExists=" + stillExists);
        Assert.assertFalse(stillExists, "Bug #1928: Education vẫn còn sau khi xóa. GPA/Score=" + marker);
        Log.info("PASS: Education đã được xóa");
    }
}
