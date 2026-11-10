package com.hrm.TestCase.TestAPI.AdminAPI;

import static io.restassured.RestAssured.given;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.hrm.Base.TestBaseAPI;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

import groovy.json.JsonOutput;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Epic("API Testing")
@Feature("Admin API - User CRUD")
public class APIUserCRUDTest extends TestBaseAPI {

    private static final String USERS_ENDPOINT = "/api/v2/admin/users";
    private static final String EMPLOYEES_ENDPOINT = "/api/v2/pim/employees";
    private Integer tempEmpNumber;
    private Integer createdUserId;
    private String testUsername;

    @BeforeClass
    public void prepareTempEmployee() {
        String empId = "USR" + UUID.randomUUID().toString().substring(0, 6);
        String body = JsonOutput.toJson(Map.of(
                "firstName", TestConfig.firstName,
                "middleName", TestConfig.middleName,
                "lastName", TestConfig.lastName,
                "employeeId", empId));

        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().post(EMPLOYEES_ENDPOINT);

        Assert.assertEquals(response.statusCode(), 200, "Setup: Không tạo được employee tạm cho User CRUD");
        tempEmpNumber = response.jsonPath().get("data.empNumber");
        Assert.assertNotNull(tempEmpNumber, "Setup: Thiếu empNumber");
    }

    @Test(priority = 1, description = "OHR89: Admin API - Tạo mới User (POST)")
    public void createUserAPI() {
        testUsername = "api_user_" + UUID.randomUUID().toString().substring(0, 6);
        String body = JsonOutput.toJson(Map.of(
                "username", testUsername,
                "password", TestConfig.password,
                "status", true,
                "userRoleId", 2,
                "empNumber", tempEmpNumber));

        Log.info("OHR89 - POST " + USERS_ENDPOINT + " | username=" + testUsername);
        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().post(USERS_ENDPOINT);

        Log.info("Status: " + response.statusCode());
        Log.info(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 200, "Tạo User phải trả về 200");
        createdUserId = response.jsonPath().get("data.id");
        Assert.assertNotNull(createdUserId, "Response phải có id của User");
        Assert.assertEquals(response.jsonPath().getString("data.userName"), testUsername, "Trả về sai userName");
    }

    @Test(priority = 2, dependsOnMethods = "createUserAPI", description = "OHR90: Admin API - Xem chi tiết User theo ID (GET)")
    public void getUserDetailAPI() {
        Log.info("OHR90 - GET " + USERS_ENDPOINT + "/" + createdUserId);
        Response response = given()
                .auth().oauth2(token)
                .pathParam("id", createdUserId)
                .when().get(USERS_ENDPOINT + "/{id}");

        Log.info("Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Lấy chi tiết User phải trả về 200");
        Assert.assertEquals(response.jsonPath().getInt("data.id"), createdUserId.intValue(), "Trả về sai ID User");
        Assert.assertEquals(response.jsonPath().getString("data.userName"), testUsername, "Trả về sai userName");
    }

    @Test(priority = 3, dependsOnMethods = "createUserAPI", description = "OHR91: Admin API - Cập nhật thông tin User (PUT)")
    public void updateUserAPI() {
        String updatedUsername = testUsername + "_ed";
        String body = JsonOutput.toJson(Map.of(
                "username", updatedUsername,
                "password", TestConfig.password,
                "status", false,
                "userRoleId", 2,
                "empNumber", tempEmpNumber,
                "changePassword", true));

        Log.info("OHR91 - PUT " + USERS_ENDPOINT + "/" + createdUserId);
        Response response = given()
                .auth().oauth2(token)
                .pathParam("id", createdUserId)
                .contentType(ContentType.JSON).body(body)
                .when().put(USERS_ENDPOINT + "/{id}");

        Log.info("Status: " + response.statusCode());
        Log.info(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 200, "Cập nhật User phải trả về 200");
        Assert.assertEquals(response.jsonPath().getString("data.userName"), updatedUsername, "userName chưa được cập nhật");
        Assert.assertFalse(response.jsonPath().getBoolean("data.status"), "Status chưa chuyển sang false (Disabled)");
        testUsername = updatedUsername;
    }

    @Test(priority = 4, description = "OHR92: Admin API - Từ chối tạo User khi thiếu password (422)")
    public void createUserMissingPasswordShouldFail() {
        String body = JsonOutput.toJson(Map.of(
                "username", "invalid_user_" + UUID.randomUUID().toString().substring(0, 5),
                "status", true,
                "userRoleId", 2,
                "empNumber", tempEmpNumber));

        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().post(USERS_ENDPOINT);

        Log.info("OHR92 - Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 422, "Thiếu password bắt buộc phải trả về 422");
    }

    @Test(priority = 5, dependsOnMethods = "createUserAPI", description = "OHR93: Admin API - Xóa User (DELETE)")
    public void deleteUserAPI() {
        String body = JsonOutput.toJson(Map.of("ids", List.of(createdUserId)));

        Log.info("OHR93 - DELETE " + USERS_ENDPOINT + " | ids=" + createdUserId);
        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().delete(USERS_ENDPOINT);

        Log.info("Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Xóa User phải trả về 200");
        createdUserId = null;
    }

    @AfterClass(alwaysRun = true)
    public void cleanupUserAndEmployee() {
        if (createdUserId != null) {
            given().auth().oauth2(token)
                    .contentType(ContentType.JSON)
                    .body(JsonOutput.toJson(Map.of("ids", List.of(createdUserId))))
                    .when().delete(USERS_ENDPOINT);
        }
        if (tempEmpNumber != null) {
            given().auth().oauth2(token)
                    .contentType(ContentType.JSON)
                    .body(JsonOutput.toJson(Map.of("ids", List.of(tempEmpNumber))))
                    .when().delete(EMPLOYEES_ENDPOINT);
        }
    }
}
