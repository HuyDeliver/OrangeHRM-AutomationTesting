package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.AddSkillPage;
import com.hrm.Pages.AdminPage.SkillPage;
import com.hrm.Util.Log;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class AddSkillFunctionality extends TestBase {
    @Epic("Module AdminTab")
    @Feature("Quản lý qualification")
    @Test(description = "OHR12: Add Skill", priority = 12)
    public void addSkillSuccess() {
        TestUtil.qualificationUtil();
        SkillPage skillPage = new SkillPage(driver);
        Assert.assertTrue(skillPage.isSkillTitleVisible(), "Không vào được trang Skill");
        AddSkillPage addSkillPage = skillPage.goToAddSkillPage();
        Assert.assertTrue(addSkillPage.isAddSkillPageVisible(), "Không vào được trang Skill");
        addSkillPage.addSkillFunction("Coding", "JavaCore, OOP, Selenium, TestNg");
        Assert.assertTrue(addSkillPage.isAddSkillSuccess(), "Add skill không thành công");
        Log.info("Add skill thành công");
    }
}
