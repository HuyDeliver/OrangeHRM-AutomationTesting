package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.EditSkillPage;
import com.hrm.Pages.AdminPage.SkillPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class EditSkillFunctionality extends TestBase {
    @Epic("Module AdminTab")
    @Feature("Quản lý qualification")
    @Test(description = "OHR17: Edit Skill", dependsOnGroups = { "add-skill" })
    public void editSkillSuccess() {
        TestUtil.qualificationUtil();
        SkillPage skillPage = new SkillPage(driver);
        Assert.assertTrue(skillPage.isSkillTitleVisible(), "Không vào được trang Skill");
        EditSkillPage editSkillPage = skillPage.goToEditSkillPage(TestDataShare.SKILL);
        Assert.assertTrue(editSkillPage.isEditPageVisible(), "Không vào đc trâng Edit Skill");
        editSkillPage.editSkill("Không bao giờ được nói dối");
        Assert.assertTrue(editSkillPage.isEditSkillSuccess(), "Edit không thành công");
        Log.info("Edit skill thành công");
    }
}
