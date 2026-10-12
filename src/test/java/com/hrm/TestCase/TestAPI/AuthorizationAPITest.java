package com.hrm.TestCase.TestAPI;

import static io.restassured.RestAssured.given;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.hrm.Base.TestBaseAPI;
import com.hrm.Util.Config;
import com.hrm.Util.Log;
import com.hrm.Util.OAuthTokenManager;
import com.hrm.Util.WebDriverFactory;

import io.restassured.response.Response;

public class AuthorizationAPITest extends TestBaseAPI {
    private static final String ADMIN_ENDPOINT = "/api/v2/admin/users";
    private static final String CONTACT_ENDPOINT = "/api/v2/pim/employee/{empNumber}/contact-details";
    private WebDriver essDriver;
    private String essToken;

    @BeforeClass
    public void loginEssUser() {
        // Browser riêng để không dùng session đăng nhập Admin.
        essDriver = WebDriverFactory.createDriver();
        essToken = OAuthTokenManager.getAccessToken(
                essDriver, Config.get("ess.username"), Config.get("ess.password"));
        Assert.assertFalse(essToken.equals(token), "Token ESS không được trùng token Admin");
    }

    @Test(priority = 1, description = "AUTHZ_01 - Admin baseline")
    public void adminShouldAccessAdminApi() {
        // Arrange
        String adminToken = token;

        // Act
        Response response = given().auth().oauth2(adminToken)
                .redirects().follow(false).get(ADMIN_ENDPOINT);

        // Assert
        logResponse("AUTHZ_01", "Admin", ADMIN_ENDPOINT, "200 và có danh sách users", response);
        Assert.assertEquals(response.statusCode(), 200, "Baseline lỗi: Admin không truy cập được API users");
        Assert.assertTrue(response.contentType().contains("json"), "Baseline phải trả JSON");
        Assert.assertTrue(response.jsonPath().get("data") != null, "Baseline thiếu data users");
        Assert.assertFalse(response.jsonPath().getList("data").isEmpty(), "Baseline cần ít nhất một user");
    }

    @Test(priority = 2, dependsOnMethods = "adminShouldAccessAdminApi", description = "AUTHZ_02 - ESS gọi Admin API")
    public void essUserShouldNotAccessAdminApi() {
        // Arrange: chứng minh token ESS truy cập được dữ liệu của chính mình.
        String employeeId = Config.get("ess.empNumber");
        Response ownResponse = given().auth().oauth2(essToken).pathParam("empNumber", employeeId)
                .redirects().follow(false).get(CONTACT_ENDPOINT);
        Assert.assertEquals(ownResponse.statusCode(), 200, "Đối chứng ESS lỗi: kiểm tra token và ess.empNumber");
        Assert.assertTrue(ownResponse.jsonPath().get("data") != null, "Đối chứng ESS thiếu dữ liệu của mình");

        // Act: cùng API với AUTHZ_01, chỉ đổi role/token.
        Response response = given().auth().oauth2(essToken)
                .redirects().follow(false).get(ADMIN_ENDPOINT);

        // Assert
        logResponse("AUTHZ_02", "ESS", ADMIN_ENDPOINT, "403 và không trả data users", response);
        Assert.assertEquals(response.statusCode(), 403, "ESS phải bị từ chối API Admin; cần kiểm tra nếu khác 403");
        Assert.assertTrue(response.contentType().contains("json"), "API từ chối phải trả JSON");
        Assert.assertTrue(response.jsonPath().get("data") == null, "Potential security issue: ESS nhận data users");
    }

    @Test(priority = 3, description = "AUTHZ_03 - IDOR READ Contact Details")
    public void employeeShouldNotAccessAnotherEmployeesPrivateData() {
        // Arrange: A xem được mình, Admin xem được B để chứng minh B tồn tại.
        String employeeId = Config.get("ess.empNumber");
        String targetEmployeeId = Config.get("target.empNumber");
        Assert.assertNotEquals(employeeId, targetEmployeeId, "Test data phải là hai nhân viên khác nhau");
        Response ownResponse = given().auth().oauth2(essToken).pathParam("empNumber", employeeId)
                .redirects().follow(false).get(CONTACT_ENDPOINT);
        Assert.assertEquals(ownResponse.statusCode(), 200, "Đối chứng A lỗi: kiểm tra token và ess.empNumber");
        Assert.assertTrue(ownResponse.jsonPath().get("data") != null, "Đối chứng A thiếu Contact Details");
        Response adminResponse = given().auth().oauth2(token).pathParam("empNumber", targetEmployeeId)
                .redirects().follow(false).get(CONTACT_ENDPOINT);
        Assert.assertEquals(adminResponse.statusCode(), 200, "Đối chứng B lỗi: kiểm tra target.empNumber");
        Assert.assertTrue(adminResponse.jsonPath().get("data") != null, "Đối chứng B thiếu Contact Details");

        // Act: giữ token A, chỉ thay empNumber thành B.
        Response response = given().auth().oauth2(essToken).pathParam("empNumber", targetEmployeeId)
                .redirects().follow(false).get(CONTACT_ENDPOINT);

        // Assert
        Log.info("AUTHZ_03: user=" + Config.get("ess.username") + ", own=" + employeeId + ", target=" + targetEmployeeId);
        logResponse("AUTHZ_03", "ESS", CONTACT_ENDPOINT.replace("{empNumber}", targetEmployeeId),
                "403 hoặc 422 do empNumber ngoài quyền; không trả Contact Details", response);
        Assert.assertTrue(response.statusCode() == 403 || response.statusCode() == 422,
                "IDOR READ phải bị từ chối; actual status=" + response.statusCode());
        Assert.assertTrue(response.contentType().contains("json"), "API từ chối phải trả JSON");
        Assert.assertTrue(response.jsonPath().get("data") == null, "Potential security issue: A nhận dữ liệu riêng của B");
        if (response.statusCode() == 422) {
            List<String> invalidParams = response.jsonPath().getList("error.data.invalidParamKeys");
            Assert.assertTrue(invalidParams != null && invalidParams.contains("empNumber"),
                    "422 phải do empNumber ngoài quyền, không phải lỗi input khác");
        }
    }

    //TODO AUTHZ_04: PUT Contact Details cần lưu đủ 11 trường và kiểm chứng restore trước khi thêm test UPDATE.
    private void logResponse(String testId, String role, String endpoint, String expected, Response response) {
        Log.info("===== " + testId + " =====\nCategory: Authorization / Broken Access Control / IDOR"
                + "\nRole: " + role + "\nMethod: GET\nEndpoint: " + endpoint
                + "\nExpected: " + expected + "\nActual status: " + response.statusCode());
        if (response.contentType().contains("json")) {
            Log.info("Relevant response: hasData=" + (response.jsonPath().get("data") != null)
                    + ", error=" + response.jsonPath().getString("error.message"));
        } else {
            Log.info("Relevant response: Content-Type=" + response.contentType());
        }
    }

    @AfterClass(alwaysRun = true)
    public void closeBrowsers() {
        try {
            if (essDriver != null) essDriver.quit();
        } finally {
            if (driver != null) driver.quit();
        }
    }
}
