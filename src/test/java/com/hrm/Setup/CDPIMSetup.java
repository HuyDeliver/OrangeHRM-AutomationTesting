package com.hrm.Setup;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.SideBar;
import com.hrm.Pages.PIMPage.EmployeeListPage;
import com.hrm.Util.Config;
import com.hrm.Util.Log;
import com.hrm.Util.TestDataShare;

// Kế thừa cleanup user tạm. Employee baseline và danh mục seed không bị xóa.
public class CDPIMSetup extends CDAdminUserSetup {
    @Override
    @BeforeTest
    public void checkCDConfiguration() throws Exception {
        if (!Boolean.getBoolean("cd.pim")) return;
        super.checkCDConfiguration();
        Assert.assertTrue(Config.get("pim.job.title", "").startsWith("AUTO CD "), "Chưa cấu hình PIM CD");
    }

    @AfterTest(alwaysRun = true)
    public void cleanupEmployees() {
        if (!ready) return;
        try {
            super.setUp();
            TestUtil.employeeUtil();
            // Employee của chuỗi CRUD và employee riêng của case Terminate.
            for (String id : new String[]{TestDataShare.IDEMPLOYEE, "T" + TestDataShare.IDEMPLOYEE}) {
                EmployeeListPage page = new SideBar(driver).clickEmployeeListPage("PIM");
                int count = page.searchByEmployeeIdAndCount(id);
                Assert.assertTrue(count <= 1, "Cleanup: nhiều employee khớp ID " + id);
                if (count == 1) {
                    page.deleteEmployee(id);
                    Assert.assertTrue(page.isDeleteEmployeeSuccess("Cleanup PIM"), "Không xóa được employee " + id);
                }
                Assert.assertEquals(page.searchByEmployeeIdAndCount(id), 0, "Employee còn sau cleanup: " + id);
                Log.info("[CD PIM] Cleanup " + id + ": 0 record");
            }
        } finally { super.tearDown(); }
    }
}
