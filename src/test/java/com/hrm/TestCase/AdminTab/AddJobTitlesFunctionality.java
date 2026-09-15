package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.AddJobTitlePage;
import com.hrm.Pages.AdminPage.JobTitlePage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("Module AdminTab")
@Feature("Quản lý Job Title")
public class AddJobTitlesFunctionality extends TestBase {
    @Test(description = "OHR9: Add new job title", groups = { "job-test" }, priority = 6)
    public void addJobTitleSuccess() {
        TestUtil.jobUtil();

        JobTitlePage jobTitlePage = new JobTitlePage(driver);
        Assert.assertTrue(jobTitlePage.isJobtitleVisible(), "Không vào được job title");

        AddJobTitlePage addJobTitlePage = jobTitlePage.addUserPage();
        Assert.assertTrue(addJobTitlePage.isTitleAddJobTitleVisible(), "Không vào được add job");
        addJobTitlePage.addTitleJob(TestConfig.jobTitleName, TestConfig.jobDescription, TestConfig.jobSpecification,
                TestConfig.jobNote);

        Assert.assertTrue(addJobTitlePage.isAddTitleJobSuccess(), "Add không thành công");

        Log.info("Add job title thành công");
    }
}
