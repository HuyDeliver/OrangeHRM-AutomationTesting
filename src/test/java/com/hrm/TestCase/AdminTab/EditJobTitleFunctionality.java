package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.EditJobTitlePage;
import com.hrm.Pages.AdminPage.JobTitlePage;
import com.hrm.Util.Log;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("Module AdminTab")
@Feature("Quản lý Job Title")
public class EditJobTitleFunctionality extends TestBase {
    @Test(description = "OHR10: Edit Job title")
    public void editJobTileSuccess() {
        TestUtil.jobUtil();
        JobTitlePage jobTitlePage = new JobTitlePage(driver);
        Assert.assertTrue(jobTitlePage.isJobtitleVisible(), "Không vào được job title");

        EditJobTitlePage editJobTitlePage = jobTitlePage.goToEditPage();

        Assert.assertTrue(editJobTitlePage.isTitleEditJobVisible(), "không vào được edit job title");

        editJobTitlePage.editJobTitle("Sales IT");

        Assert.assertTrue(editJobTitlePage.isEditJobTitleSuccess(), "Edit không thành công");

        Log.info("Edit job title thành công");
    }
}
