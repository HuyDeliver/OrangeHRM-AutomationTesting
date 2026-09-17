package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.JobTitlePage;
import com.hrm.Util.Log;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class DeleteJobTitlesFunctionality extends TestBase {
    @Epic("Module AdminTab")
    @Feature("Quản lý Job")
    @Test(description = "OHR11: Delete Job", dependsOnGroups = { "job-test" }, priority = 8)
    public void deleteJob() {
        TestUtil.jobUtil();
        JobTitlePage jobTitlePage = new JobTitlePage(driver);
        Assert.assertTrue(jobTitlePage.isJobtitleVisible(), "Không vào được job title");

        jobTitlePage.clickDelete();
        Assert.assertTrue(jobTitlePage.isDeleteSuccess(), "Delete không thành công");

        Log.info("Delete Job title success");
    }
}
