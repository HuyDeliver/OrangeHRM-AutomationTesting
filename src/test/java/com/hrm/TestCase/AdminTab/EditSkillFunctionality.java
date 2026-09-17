package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.EditSkillPage;
import com.hrm.Pages.AdminPage.SkillPage;
import com.hrm.Util.Log;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class EditSkillFunctionality extends TestBase {
    @Epic("Module AdminTab")
    @Feature("Quản lý qualification")
    @Test(description = "OHR12: Edit Skill", priority = 13)
    public void editSkillSuccess() {
        TestUtil.qualificationUtil();
        SkillPage skillPage = new SkillPage(driver);
        Assert.assertTrue(skillPage.isSkillTitleVisible(), "Không vào được trang Skill");
        EditSkillPage editSkillPage = skillPage.goToEditSkillPage();
        Assert.assertTrue(editSkillPage.isEditPageVisible(), "Không vào đc trâng Edit Skill");
        editSkillPage.editSkill("Nói dối", "Không bao giờ được nói dối");
        Assert.assertTrue(editSkillPage.isEditSkillSuccess(), "Edit không thành công");
        Log.info("Edit skill thành công");
    }
}
