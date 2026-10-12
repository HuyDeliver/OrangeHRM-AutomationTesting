package com.hrm.TestCase.TestAPI;

import static io.restassured.RestAssured.given;

import java.util.List;
import java.util.Locale;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.hrm.Base.TestBaseAPI;
import com.hrm.Util.Config;
import com.hrm.Util.Log;

import io.restassured.response.Response;

public class InjectionAPITest extends TestBaseAPI {
    private static final String USERS_ENDPOINT = "/api/v2/admin/users";

    @BeforeClass
    public void verifyUsernameFilterWorks() {
        String knownUsername = Config.get("username");
        Response response = given().auth().oauth2(token).queryParam("username", knownUsername)
                .redirects().follow(false).when().get(USERS_ENDPOINT);
        Log.info("Precondition: username filter, status=" + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Setup lỗi: kiểm tra token, quyền Admin và endpoint");
        Assert.assertTrue(response.contentType().contains("json"), "Setup phải trả JSON");
        List<String> userNames = response.jsonPath().getList("data.userName", String.class);
        Assert.assertFalse(userNames.isEmpty(), "Setup lỗi: không tìm thấy username từ config");
        for (String userName : userNames) {
            Assert.assertTrue(knownUsername.equalsIgnoreCase(userName), "Setup lỗi: bộ lọc username không hoạt động đúng");
        }
    }

    @Test(priority = 1, description = "INJ_01 - Single quote input")
    public void singleQuoteShouldNotCauseDatabaseError() {
        // Arrange
        String payload = "'";

        // Act
        Response response = given().auth().oauth2(token).queryParam("username", payload)
                .redirects().follow(false).when().get(USERS_ENDPOINT);

        // Assert
        Log.info("===== INJ_01 =====\nCategory: SQL Injection\nMethod: GET\nEndpoint: " + USERS_ENDPOINT
                + "\nParameter: username\nPayload: " + payload + "\nStatus: " + response.statusCode());
        assertNoDatabaseError(response);
        if (response.statusCode() == 200) {
            Log.info("Response: data count=" + response.jsonPath().getList("data").size()
                    + ", meta.total=" + response.jsonPath().getInt("meta.total"));
        }
    }

    @Test(priority = 2, description = "INJ_02 - Boolean SQL Injection-like input")
    public void booleanPayloadShouldNotBypassUsernameFilter() {
        // Arrange
        String normalInput = "SECURITY_INJ_NO_MATCH";
        String payload = "' OR '1'='1";

        // Act: cùng endpoint/token, chỉ thay giá trị username.
        Response normalResponse = given().auth().oauth2(token).queryParam("username", normalInput)
                .redirects().follow(false).when().get(USERS_ENDPOINT);
        Response injectionResponse = given().auth().oauth2(token).queryParam("username", payload)
                .redirects().follow(false).when().get(USERS_ENDPOINT);

        // Assert
        Log.info("===== INJ_02 =====\nCategory: SQL Injection\nMethod: GET\nEndpoint: " + USERS_ENDPOINT
                + "\nParameter: username\nPayload: " + payload + "\nStatus: " + injectionResponse.statusCode());
        Assert.assertEquals(normalResponse.statusCode(), 200, "Baseline lỗi: kiểm tra token/endpoint, chưa kết luận SQL Injection");
        Assert.assertTrue(normalResponse.contentType().contains("json"), "Baseline phải trả JSON");
        List<?> normalData = normalResponse.jsonPath().getList("data");
        int normalTotal = normalResponse.jsonPath().getInt("meta.total");
        Log.info("Baseline: input=" + normalInput + ", data count=" + normalData.size() + ", meta.total=" + normalTotal);
        Assert.assertTrue(normalData.isEmpty() && normalTotal == 0, "Baseline keyword đang match user: kiểm tra test data");
        assertNoDatabaseError(injectionResponse);
        if (injectionResponse.statusCode() == 200) {
            List<?> injectionData = injectionResponse.jsonPath().getList("data");
            int injectionTotal = injectionResponse.jsonPath().getInt("meta.total");
            Log.info("Comparison: baseline count=" + normalData.size() + ", injection count=" + injectionData.size()
                    + ", baseline total=" + normalTotal + ", injection total=" + injectionTotal);
            if (injectionTotal > normalTotal) {
                Log.warn("Result count increased; investigate literal username matches before concluding SQL Injection");
            }
            for (String userName : injectionResponse.jsonPath().getList("data.userName", String.class)) {
                Assert.assertTrue(payload.equalsIgnoreCase(userName),
                        "Potential search-filter bypass: returned user does not match payload; SQL Injection chưa được xác nhận");
            }
        }
    }

    @Test(priority = 3, description = "INJ_03 - Double quote variant")
    public void doubleQuotePayloadShouldNotBypassUsernameFilter() {
        // Arrange
        String payload = "\" OR \"1\"=\"1";

        // Act
        Response response = given().auth().oauth2(token).queryParam("username", payload)
                .redirects().follow(false).when().get(USERS_ENDPOINT);

        // Assert
        Log.info("===== INJ_03 =====\nCategory: SQL Injection\nMethod: GET\nEndpoint: " + USERS_ENDPOINT
                + "\nParameter: username\nPayload: " + payload + "\nStatus: " + response.statusCode());
        assertNoDatabaseError(response);
        if (response.statusCode() == 200) {
            Log.info("Response: data count=" + response.jsonPath().getList("data").size()
                    + ", meta.total=" + response.jsonPath().getInt("meta.total"));
            for (String userName : response.jsonPath().getList("data.userName", String.class)) {
                Assert.assertTrue(payload.equalsIgnoreCase(userName),
                        "Potential search-filter bypass: returned user does not match payload; cần kiểm tra thêm");
            }
        }
    }

    private void assertNoDatabaseError(Response response) {
        String body = response.asString().toLowerCase(Locale.ROOT);
        for (String marker : List.of("sql syntax", "syntax error", "sqlstate", "pdoexception",
                "mysqli_sql_exception", "database exception", "stack trace")) {
            Assert.assertFalse(body.contains(marker), "Potential DB/internal error exposure: " + marker + "; cần kiểm tra thêm");
        }
        Assert.assertFalse(body.contains("select ") && body.contains("from "), "Potential SQL query exposure; cần kiểm tra thêm");
        Assert.assertNotEquals(response.statusCode(), 500, "Potential backend input handling issue: HTTP 500");
        Assert.assertTrue(response.statusCode() == 200 || response.statusCode() == 400 || response.statusCode() == 422,
                "Unexpected status: " + response.statusCode() + "; kiểm tra token/route/environment trước");
        Assert.assertTrue(response.contentType().contains("json"), "API phải trả JSON, không phải trang login/error HTML");
        if (response.statusCode() != 200) {
            String message = response.jsonPath().getString("error.message");
            Assert.assertTrue("Invalid Parameter".equalsIgnoreCase(message),
                    "400/422 phải là lỗi validation của API, không phải lỗi không liên quan");
            Assert.assertNull(response.jsonPath().get("data"), "Validation response không được trả dữ liệu users");
            Log.info("Response: validation message=" + message);
        }
    }

    @AfterClass(alwaysRun = true)
    public void closeBrowser() {
        if (driver != null) driver.quit();
    }
}
