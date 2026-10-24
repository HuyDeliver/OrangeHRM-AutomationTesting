package com.hrm.TestCase.TestSecurity;

import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.hrm.Base.TestBaseAPI;
import com.hrm.Util.Log;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;

@Epic("Security")
@Feature("AuthenticationAPI")
public class AuthenticationAPITest extends TestBaseAPI {

        private static final int EMPLOYEE_ID = 7;

        private static final String EMPLOYEE_ENDPOINT = "/api/v2/pim/employees/{empNumber}";

        @Test(description = "OHR51: Authentication - Token hợp lệ phải truy cập được API")
        public void validTokenShouldBeAccepted() {

                Response response = given()
                                .auth()
                                .oauth2(token)
                                .pathParam("empNumber", EMPLOYEE_ID)
                                .when()
                                .get(EMPLOYEE_ENDPOINT);

                Log.info("\n===== VALID TOKEN =====");
                Log.info("Status: " + response.statusCode());
                Log.info(response.asPrettyString());

                Log.info("Status: " + response.statusCode());

                Log.info(response.asPrettyString());

                Assert.assertEquals(response.statusCode(), 200, "Valid token không truy cập được API");
        }

        // =========================================================
        // SEC_AUTH_02
        // Không gửi Bearer Token
        // =========================================================
        @Test(description = "OHR52: Authentication - Request không có token phải bị từ chối")
        public void requestWithoutTokenShouldBeRejected() {

                Response response = given()
                                .redirects()
                                .follow(false)
                                .pathParam("empNumber", EMPLOYEE_ID)
                                .when()
                                .get(EMPLOYEE_ENDPOINT);

                Log.info("\n===== NO TOKEN =====");
                Log.info("Status: " + response.statusCode());
                Log.info("Location: " + response.getHeader("Location"));
                Log.info(response.asPrettyString());

                Assert.assertNotEquals(
                                response.statusCode(),
                                200,
                                "Security issue: API cho phép truy cập mà không cần token");

                Assert.assertTrue(
                                response.statusCode() == 401
                                                || response.statusCode() == 403
                                                || response.statusCode() == 302,
                                "Unexpected status khi request không có token: "
                                                + response.statusCode());
        }

        // =========================================================
        // AUTH_03 - Bearer Token không hợp lệ
        // =========================================================
        @Test(description = "OHR53: Authentication - Token không hợp lệ phải bị từ chối")
        public void invalidTokenShouldBeRejected() {

                Response response = given()
                                .redirects()
                                .follow(false)
                                .auth()
                                .oauth2("fake-token")
                                .pathParam("empNumber", EMPLOYEE_ID)
                                .when()
                                .get(EMPLOYEE_ENDPOINT);

                Log.info("\n===== INVALID TOKEN =====");
                Log.info("Status: " + response.statusCode());
                Log.info("Location: " + response.getHeader("Location"));
                Log.info(response.asPrettyString());

                Assert.assertNotEquals(
                                response.statusCode(),
                                200,
                                "Security issue: invalid token vẫn truy cập được API");

                Assert.assertTrue(
                                response.statusCode() == 401
                                                || response.statusCode() == 403
                                                || response.statusCode() == 302,
                                "Unexpected status khi dùng invalid token: "
                                                + response.statusCode());

                if (response.statusCode() == 302) {

                        String location = response.getHeader("Location");

                        Assert.assertNotNull(
                                        location,
                                        "302 nhưng không có Location header");

                        Assert.assertTrue(
                                        location.contains("/auth/login"),
                                        "Invalid token redirect tới URL bất thường: "
                                                        + location);
                }
        }
        // @Test
        // public void checkList(){
        // Response response =
        // given()
        // .auth()
        // .oauth2(token)
        // .when()
        // .get("/api/v2/pim/employees");

        // Log.info(response.asPrettyString());
        // }

        @AfterMethod
        public void tearDown() {
                if (driver != null) {
                        driver.quit();
                }
        }

}
