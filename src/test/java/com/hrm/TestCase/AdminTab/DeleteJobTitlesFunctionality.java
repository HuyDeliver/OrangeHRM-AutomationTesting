package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.JobTitlePage;
import com.hrm.Util.Log;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

public class DeleteJobTitlesFunctionality extends TestBase {
    @Epic("Module AdminTab")
    @Feature("Quản lý Job")
    @Test(priority = 3, description = "OHR11: Delete Job", dependsOnGroups = { "job-test" })
    public void deleteJob() {
        TestUtil.jobUtil();
        JobTitlePage jobTitlePage = new JobTitlePage(driver);
        Assert.assertTrue(jobTitlePage.isJobtitleVisible(), "Không vào được job title");

        jobTitlePage.clickDelete(TestDataShare.JOB_TITLE);
        Assert.assertTrue(jobTitlePage.isDeleteSuccess(), "Delete không thành công");
        Assert.assertEquals(jobTitlePage.countJobTitles(TestDataShare.JOB_TITLE), 0, "Job Title vẫn còn sau khi xóa");

        Log.info("Delete Job title success");
    }
}
