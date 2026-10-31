package com.hrm.TestCase.PIMTab;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.openqa.selenium.Dimension;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.PIMPage.EditEmployeePage;
import com.hrm.Pages.PIMPage.EmployeeListPage;
import com.hrm.Util.Config;
import com.hrm.Util.Log;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("PIM tab")
@Feature("PIM trên CD với employee baseline")
public class CDPIMTest extends TestBase {
    private final Properties fixture = new Properties();
    private EmployeeListPage employees;

    @BeforeClass
    public void readFixture() throws Exception {
        Assert.assertEquals(Config.get("app.url"), "http://localhost:8081/web/index.php/auth/login",
                "Nhóm này chỉ dùng dữ liệu seed của CD 8081");
        try (Reader reader = Files.newBufferedReader(Path.of("deploy/seed.properties"))) {
            fixture.load(reader);
        }
        for (String key : new String[] {"employee.id", "employee.negativeId", "employee.firstName", "employee.lastName"}) {
            Assert.assertFalse(fixture.getProperty(key, "").isBlank(), "Thiếu fixture: " + key);
        }
        Assert.assertNotEquals(fixture.getProperty("employee.id"), fixture.getProperty("employee.negativeId"),
                "ID positive và negative phải khác nhau");
    }

    @Override
    @BeforeMethod
    public void setUp() {
        super.setUp();
        // Giữ sidebar hiển thị trên runner headless, giống CDSeedTest.
        driver.manage().window().setSize(new Dimension(1920, 1080));
        Log.info("Bước 1: Đăng nhập CD 8081 và mở PIM");
        TestUtil.employeeUtil();
        employees = new EmployeeListPage(driver);
        Assert.assertTrue(employees.isEmployeeListVisible(), "Không mở được Employee List");
    }

    @Test(description = "OHR67: CD - Tìm đúng employee baseline theo Employee ID")
    public void searchExistingEmployeeShouldReturnMatchingId() {
        // Arrange
        String id = fixture.getProperty("employee.id");
        // Act
        Log.info("OHR67 - Bước 2: Tìm Employee ID=" + id);
        int count = employees.searchByEmployeeIdAndCount(id);
        // Assert
        Log.info("OHR67 - Bước 3: Expected=1 record đúng ID | Actual count=" + count);
        Assert.assertEquals(count, 1, "Phải tìm thấy đúng một employee baseline");
        Assert.assertTrue(employees.checkSearch("", id, "", "", "", true), "Kết quả trả về sai Employee ID");
    }

    @Test(description = "OHR68: CD - Employee ID không tồn tại trả về No Records Found")
    public void searchMissingEmployeeShouldReturnNoRecords() {
        // Arrange
        String id = fixture.getProperty("employee.negativeId");
        // Act
        Log.info("OHR68 - Bước 2: Tìm Employee ID không tồn tại=" + id);
        int count = employees.searchByEmployeeIdAndCount(id);
        // Assert
        Log.info("OHR68 - Bước 3: Expected=0 record | Actual count=" + count);
        Assert.assertEquals(count, 0, "ID negative phải không có kết quả");
        Assert.assertTrue(employees.checkSearch("", id, "", "", "", false), "Phải hiển thị No Records Found");
    }

    @Test(description = "OHR69: CD - Mở employee baseline hiển thị đúng ID và họ tên")
    public void employeeDetailsShouldMatchBaseline() {
        // Arrange
        String id = fixture.getProperty("employee.id");
        Log.info("OHR69 - Bước 2: Tìm employee để mở chi tiết: " + id);
        Assert.assertEquals(employees.searchByEmployeeIdAndCount(id), 1, "Không xác định được employee baseline");
        // Act
        EditEmployeePage detail = employees.goToeEditEmployeePage(id);
        // Assert
        Log.info("OHR69 - Bước 3: Kiểm tra Employee ID, First Name và Last Name theo seed.properties");
        Assert.assertTrue(detail.hasEmployeeIdentity(id, fixture.getProperty("employee.firstName"),
                fixture.getProperty("employee.lastName")), "Thông tin chi tiết không khớp employee baseline");
    }
}
