package com.hrm.Setup;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.openqa.selenium.Dimension;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.SideBar;
import com.hrm.Pages.AdminPage.AddUserPage;
import com.hrm.Pages.PIMPage.AddEmployeePage;
import com.hrm.Pages.PIMPage.EditEmployeePage;
import com.hrm.Pages.PIMPage.EmployeeListPage;
import com.hrm.Util.Config;
import com.hrm.Util.Log;

// Setup riêng: không gắn mã OHR, không đưa vào testng.xml/regression.
public class CDSeedTest extends TestBase {
    @Override
    @BeforeMethod
    public void setUp() {
        Assert.assertEquals(Config.get("app.url"), "http://localhost:8081/web/index.php/auth/login",
                "Seed chỉ được chạy trên CD 8081");
        Assert.assertTrue(Boolean.getBoolean("cd.seed.verified"),
                "Hãy chạy deploy/seed.ps1 để xác minh container và database trước");
        super.setUp();
        // Maximize trong TestBase có thể thu nhỏ cửa sổ headless theo màn hình runner.
        Log.info("[SEED] Window after maximize: " + driver.manage().window().getSize());
        driver.manage().window().setSize(new Dimension(1920, 1080));
    }

    @Test(groups = "data-preparation", description = "CD setup: chuẩn bị employee baseline, không phải functional test")
    public void prepareMinimalData() throws Exception {
        Properties fixture = new Properties();
        try (Reader reader = Files.newBufferedReader(Path.of("deploy/seed.properties"))) {
            fixture.load(reader);
        }
        String key = fixture.getProperty("employee.id");
        String first = fixture.getProperty("employee.firstName");
        String last = fixture.getProperty("employee.lastName");
        String negativeId = fixture.getProperty("employee.negativeId");
        Assert.assertTrue(key != null && key.matches("AUTO[A-Z0-9]{1,6}"), "Fixture ID phải có prefix AUTO, tối đa 10 ký tự");
        Assert.assertTrue(first != null && !first.isBlank() && first.length() <= 30, "Sai fixture firstName");
        Assert.assertTrue(last != null && !last.isBlank() && last.length() <= 30, "Sai fixture lastName");
        Assert.assertTrue(negativeId != null && negativeId.matches("AUTO[A-Z0-9]{1,6}"), "Sai negative ID");
        Assert.assertNotEquals(key, negativeId, "Positive và negative ID phải khác nhau");

        // 1. Đăng nhập bằng Admin CD, tìm theo Employee ID cố định.
        TestUtil.employeeUtil();
        EmployeeListPage employees = new EmployeeListPage(driver);
        Assert.assertTrue(employees.isEmployeeListVisible(), "Không mở được PIM");
        int matches = employees.searchByEmployeeIdAndCount(key);
        Assert.assertTrue(matches <= 1, "Duplicate fixture: " + key + " | matches=" + matches);
        String action = "REUSED";

        // 2. Chỉ tạo khi đã xác nhận không có bản ghi. Không tạo login/user.
        if (matches == 0) {
            AddEmployeePage add = employees.goToAddEmployee();
            Assert.assertTrue(add.isAddEmployeeVisible(), "Không mở được Add Employee");
            add.infoNewEmployee(null, first, last, "", key);
            add.saveEmployee();
            Assert.assertTrue(add.isCreateNewEmployeeSuccess(), "Không tạo được employee baseline");
            action = "CREATED";
        }

        // 3. Đọc lại từ UI; lấy internal ID thực tế thay vì ép empNumber.
        employees = new SideBar(driver).clickEmployeeListPage("PIM");
        matches = employees.searchByEmployeeIdAndCount(key);
        Assert.assertEquals(matches, 1, "Phải có đúng một employee baseline");
        EditEmployeePage detail = employees.goToeEditEmployeePage(key);
        Assert.assertTrue(detail.hasEmployeeIdentity(key, first, last), "Fixture có sẵn nhưng sai danh tính; dừng để kiểm tra");
        String empNumber = detail.getEmployeeNumber();
        Log.info("[SEED] Employee " + key + " -> " + action + " | empNumber=" + empNumber + " | matches=" + matches);

        // 4. Dữ liệu negative phải tiếp tục không tồn tại.
        employees = new SideBar(driver).clickEmployeeListPage("PIM");
        Assert.assertEquals(employees.searchByEmployeeIdAndCount(negativeId), 0, "Negative ID đã tồn tại; không tự xóa dữ liệu");

        // 5. Employee baseline phải chọn được khi Add User; không Save form.
        AddUserPage addUser = new SideBar(driver).clickAdminPage().addUserPage();
        Assert.assertTrue(addUser.canSelectEmployee(first + " " + last), "Employee không chọn được trong Add User");
        Log.info("[SEED] Admin User prerequisite ready | PIM ID/name ready | negative ID absent");

        Properties result = new Properties();
        result.setProperty("kind", "DATA_PREPARATION");
        result.setProperty("result", "SUCCESS");
        result.setProperty("fixture.key", key);
        result.setProperty("employee.empNumber", empNumber);
        result.setProperty("match.count", String.valueOf(matches));
        result.setProperty("duplicate", "NO");
        result.setProperty("action", action);
        result.setProperty("admin.prerequisite", "READY");
        result.setProperty("pim.prerequisite", "READY");
        Path output = Path.of(System.getProperty("seed.output"));
        Files.createDirectories(output);
        try (Writer writer = Files.newBufferedWriter(output.resolve("seed-result.properties"))) {
            result.store(writer, "Setup evidence only - excluded from functional/security/regression totals");
        }
    }

    @Override
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        super.tearDown();
    }
}
