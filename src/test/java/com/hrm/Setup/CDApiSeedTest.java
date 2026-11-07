package com.hrm.Setup;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.SideBar;
import com.hrm.Pages.AdminPage.AddUserPage;
import com.hrm.Pages.AdminPage.UserManagePage;
import com.hrm.Pages.PIMPage.AddEmployeePage;
import com.hrm.Pages.PIMPage.EmployeeListPage;
import com.hrm.Util.Config;
import com.hrm.Util.Log;

// Giữ seed cũ; chỉ thêm employee + ESS user cố định cho Authorization test.
public class CDApiSeedTest extends CDPerformanceSeedTest {
    @Test(groups = "data-preparation", dependsOnMethods = "preparePerformanceFixtures",
            description = "CD setup: ESS employee/user cho API Security, không tính functional PASS")
    public void prepareApiFixtures() throws Exception {
        String essUser = Config.get("ess.username");
        String essPass = Config.get("ess.password");
        String id = "AUTOESS001";
        Assert.assertTrue(essUser.startsWith("AUTO_"), "Chỉ seed ESS user riêng của CD");

        TestUtil.employeeUtil();
        EmployeeListPage employees = new EmployeeListPage(driver);
        int count = employees.searchByEmployeeIdAndCount(id);
        Assert.assertTrue(count <= 1, "Duplicate ESS employee");
        if (count == 0) {
            AddEmployeePage add = employees.goToAddEmployee();
            add.infoNewEmployee(null, "AUTO", "ApiEss", "", id);
            add.saveEmployee();
            Assert.assertTrue(add.isCreateNewEmployeeSuccess(), "Không tạo được ESS employee");
            employees = new SideBar(driver).clickEmployeeListPage("PIM");
        }
        Assert.assertEquals(employees.searchByEmployeeIdAndCount(id), 1, "Phải có đúng một ESS employee");
        String empNumber = employees.goToeEditEmployeePage(id).getEmployeeNumber();
        Assert.assertTrue(empNumber != null && empNumber.matches("\\d+"), "Không đọc được empNumber của ESS employee");

        UserManagePage users = new SideBar(driver).clickAdminPage();
        if (users.searchByUsernameAndCount(essUser) == 0) {
            AddUserPage addUser = users.addUserPage();
            addUser.addUserWithValidData(essUser, "ESS", "AUTO ApiEss", "Enabled", essPass, essPass);
            Assert.assertTrue(addUser.isAddUserSuccess(), "Không tạo được ESS user");
        }
        Assert.assertEquals(users.searchByUsernameAndCount(essUser), 1, "Phải có đúng một ESS user");

        Properties result = new Properties();
        result.setProperty("kind", "DATA_PREPARATION");
        result.setProperty("result", "SUCCESS");
        result.setProperty("ess.employeeId", id);
        result.setProperty("ess.empNumber", empNumber);
        try (Writer out = Files.newBufferedWriter(Path.of(System.getProperty("seed.output"), "api-seed-result.properties"))) {
            result.store(out, "Setup only");
        }
        Log.info("[API SEED] ESS employee " + id + " | empNumber=" + empNumber);
    }
}
