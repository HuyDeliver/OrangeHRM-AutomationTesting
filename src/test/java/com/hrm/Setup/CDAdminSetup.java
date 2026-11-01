package com.hrm.Setup;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.JobTitlePage;
import com.hrm.Pages.AdminPage.LocationPage;
import com.hrm.Pages.AdminPage.SkillPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestDataShare;

// Dùng trong module Admin. Chỉ bật guard/cleanup CD khi có profile cd-admin.
public class CDAdminSetup extends CDAdminUserSetup {
    @Override
    @BeforeTest
    public void checkCDConfiguration() throws Exception {
        if (Boolean.getBoolean("cd.admin")) super.checkCDConfiguration();
    }

    @AfterTest(alwaysRun = true)
    public void cleanupJobTitle() {
        if (!ready) return;
        try {
            super.setUp();
            TestUtil.jobUtil();
            JobTitlePage page = new JobTitlePage(driver);
            int count = page.countJobTitles(TestDataShare.JOB_TITLE);
            Assert.assertTrue(count <= 1, "Cleanup Job Title: nhiều hơn một record khớp tên");
            if (count == 1) {
                page.clickDelete(TestDataShare.JOB_TITLE);
                Assert.assertTrue(page.isDeleteSuccess(), "Cleanup Job Title thất bại");
            }
            Assert.assertEquals(page.countJobTitles(TestDataShare.JOB_TITLE), 0, "Job Title vẫn còn sau cleanup");
            Log.info("[CD ADMIN] Cleanup Job Title: 0 record");
        } finally { super.tearDown(); }
    }

    @AfterTest(alwaysRun = true)
    public void cleanupLocation() {
        if (!ready) return;
        try {
            super.setUp();
            TestUtil.organizeUtil();
            LocationPage page = new LocationPage(driver);
            page.searchLocation(TestDataShare.LOCATION, "", "");
            int count = page.countLocations(TestDataShare.LOCATION);
            Assert.assertTrue(count <= 1, "Cleanup Location: nhiều hơn một record khớp tên");
            if (count == 1) {
                page.deleteLocation(TestDataShare.LOCATION);
                Assert.assertTrue(page.isDeleteLocationSuccess(), "Cleanup Location thất bại");
            }
            Assert.assertEquals(page.countLocations(TestDataShare.LOCATION), 0, "Location vẫn còn sau cleanup");
            Log.info("[CD ADMIN] Cleanup Location: 0 record");
        } finally { super.tearDown(); }
    }

    @AfterTest(alwaysRun = true)
    public void cleanupSkill() {
        if (!ready) return;
        try {
            super.setUp();
            TestUtil.qualificationUtil();
            SkillPage page = new SkillPage(driver);
            int count = page.countSkills(TestDataShare.SKILL);
            Assert.assertTrue(count <= 1, "Cleanup Skill: nhiều hơn một record khớp tên");
            if (count == 1) {
                page.deleteSkill(TestDataShare.SKILL);
                Assert.assertTrue(page.isDeleteSkillSuccess(), "Cleanup Skill thất bại");
            }
            Assert.assertEquals(page.countSkills(TestDataShare.SKILL), 0, "Skill vẫn còn sau cleanup");
            Log.info("[CD ADMIN] Cleanup Skill: 0 record");
        } finally { super.tearDown(); }
    }
}
