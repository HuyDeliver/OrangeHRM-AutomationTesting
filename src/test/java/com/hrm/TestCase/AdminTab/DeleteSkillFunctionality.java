package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.SkillPage;
import com.hrm.Util.Log;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class DeleteSkillFunctionality extends TestBase {
    @Epic("Module AdminTab")
    @Feature("Quản lý qualification")
    @Test(description = "OHR13: Delete Skill", priority = 14)
    public void editSkillSuccess() {
        TestUtil.qualificationUtil();
        SkillPage skillPage = new SkillPage(driver);
        Assert.assertTrue(skillPage.isSkillTitleVisible(), "Không vào được trang Skill");
        skillPage.deleteSkill();
        Assert.assertTrue(skillPage.isDeleteSkillSuccess(), "Delete không thành công");
        Log.info("Edit skill thành công");
    }
}
