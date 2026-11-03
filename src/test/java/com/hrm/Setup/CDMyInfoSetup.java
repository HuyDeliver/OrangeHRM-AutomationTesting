package com.hrm.Setup;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.SideBar;
import com.hrm.Pages.PIMPage.AddEmployeePage;
import com.hrm.Pages.PIMPage.EmployeeListPage;
import com.hrm.Pages.AdminPage.AddUserPage;
import com.hrm.Pages.AdminPage.UserManagePage;
import com.hrm.Util.TestDataShare;
import com.hrm.Util.Log;

// Dùng employee/login tạm: không thay đổi hồ sơ Admin hoặc employee baseline.
public class CDMyInfoSetup extends CDAdminUserSetup {
    @Override
    @BeforeTest
    public void checkCDConfiguration() throws Exception {
        if (!Boolean.getBoolean("cd.myinfo")) return;
        super.checkCDConfiguration();
        try {
            super.setUp();
            TestUtil.employeeUtil();
            EmployeeListPage employees = new EmployeeListPage(driver);
            Assert.assertEquals(employees.searchByEmployeeIdAndCount(TestDataShare.IDEMPLOYEE), 0,
                    "Không ghi đè employee đã có");
            AddEmployeePage add = employees.goToAddEmployee();
            add.infoNewEmployee(null, "AUTO", TestDataShare.LASTNAME, "", TestDataShare.IDEMPLOYEE);
            add.saveEmployee();
            Assert.assertTrue(add.isCreateNewEmployeeSuccess(), "Không tạo được tài khoản My Info tạm");
            employees = new SideBar(driver).clickEmployeeListPage("PIM");
            Assert.assertEquals(employees.searchByEmployeeIdAndCount(TestDataShare.IDEMPLOYEE), 1,
                    "Không tìm lại được employee My Info");
            // Bộ test cũ dùng Admin; ESS không được sửa License Number/Date of Birth.
            UserManagePage users = new SideBar(driver).clickAdminPage();
            AddUserPage addUser = users.addUserPage();
            addUser.addUserWithValidData(TestDataShare.USERNAME, "Admin", "AUTO " + TestDataShare.LASTNAME,
                    "Enabled", TestDataShare.USER_PASSWORD, TestDataShare.USER_PASSWORD);
            Assert.assertTrue(addUser.isAddUserSuccess(), "Không tạo được Admin tạm cho My Info");
            Assert.assertEquals(users.searchByUsernameAndCount(TestDataShare.USERNAME), 1, "Không tìm được user tạm");
            Assert.assertTrue(users.isRecordFoundMatchKeySearch(TestDataShare.USERNAME, "Admin",
                    "AUTO " + TestDataShare.LASTNAME, "Enabled"), "Sai role/danh tính user My Info");
            Log.info("[CD MYINFO] Setup employee/login riêng: " + TestDataShare.IDEMPLOYEE);
        } finally { super.tearDown(); }
    }

    @AfterTest(alwaysRun = true)
    public void cleanupEmployee() {
        if (!ready) return;
        try {
            super.setUp();
            TestUtil.employeeUtil();
            EmployeeListPage employees = new EmployeeListPage(driver);
            int count = employees.searchByEmployeeIdAndCount(TestDataShare.IDEMPLOYEE);
            Assert.assertTrue(count <= 1, "Cleanup: ID khớp nhiều employee");
            if (count == 1) {
                employees.deleteEmployee(TestDataShare.IDEMPLOYEE);
                Assert.assertTrue(employees.isDeleteEmployeeSuccess("Cleanup My Info"), "Không xóa được employee tạm");
            }
            Assert.assertEquals(employees.searchByEmployeeIdAndCount(TestDataShare.IDEMPLOYEE), 0,
                    "Employee My Info còn trong danh sách");
            Log.info("[CD MYINFO] Employee tạm: 0 record; user được kiểm tra bởi cleanupRunUser");
        } finally { super.tearDown(); }
    }
}
