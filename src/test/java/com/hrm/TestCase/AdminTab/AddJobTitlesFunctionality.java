package com.hrm.TestCase.AdminTab;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.AddJobTitlePage;
import com.hrm.Pages.AdminPage.JobTitlePage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

public class AddJobTitlesFunctionality extends TestBase {
    @Test
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
