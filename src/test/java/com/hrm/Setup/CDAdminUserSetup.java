package com.hrm.Setup;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.UserManagePage;
import com.hrm.Util.Config;
import com.hrm.Util.Log;
import com.hrm.Util.TestDataShare;

// Chỉ được khai báo trong nhóm CDAdminUser; không có @Test, không cộng số testcase.
public class CDAdminUserSetup extends TestBase {
    private boolean ready;

    @BeforeTest
    public void checkCDConfiguration() throws Exception {
        Assert.assertEquals(Config.get("app.url"), "http://localhost:8081/web/index.php/auth/login",
                "CDAdminUser chỉ được chạy trên CD 8081");
        Properties fixture = new Properties();
        try (Reader reader = Files.newBufferedReader(Path.of("deploy/seed.properties"))) {
            fixture.load(reader);
        }
        String employee = fixture.getProperty("employee.firstName") + " " + fixture.getProperty("employee.lastName");
        Assert.assertEquals(Config.get("admin.employee.name"), employee,
                "admin.employee.name phải khớp employee baseline trong seed.properties");
        Assert.assertNotEquals(TestDataShare.USERNAME, Config.get("username"), "Không dùng tài khoản Admin đăng nhập làm test data");
        ready = true;
        Log.info("[CD ADMIN] Dùng employee baseline; user tạm của lượt chạy: " + TestDataShare.USERNAME);
    }

    @AfterTest(alwaysRun = true)
    public void cleanupRunUser() {
        if (!ready) return;
        // Chạy kể cả khi Add/Edit/Delete lỗi. Chỉ tìm/xóa username của chính lượt này.
        try {
            super.setUp();
            TestUtil.userUtil();
            UserManagePage users = new UserManagePage(driver);
            int count = users.searchByUsernameAndCount(TestDataShare.USERNAME);
            Assert.assertTrue(count <= 1, "Cleanup tìm thấy nhiều user; dừng để kiểm tra");
            if (count == 1) {
                Log.info("[CD ADMIN] Cleanup user còn lại: " + TestDataShare.USERNAME);
                users.clickDeleteUser(TestDataShare.USERNAME);
                Assert.assertTrue(users.isDeleteSuccess(), "Cleanup không xóa được user");
                Assert.assertEquals(users.searchByUsernameAndCount(TestDataShare.USERNAME), 0,
                        "User vẫn tồn tại sau cleanup");
            }
            Log.info("[CD ADMIN] Cleanup verified: run user count=0; employee baseline được giữ lại");
        } finally {
            super.tearDown();
        }
    }
}
