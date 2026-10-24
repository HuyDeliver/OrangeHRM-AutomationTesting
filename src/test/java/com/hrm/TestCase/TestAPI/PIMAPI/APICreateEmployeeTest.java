package com.hrm.TestCase.TestAPI.PIMAPI;

import static io.restassured.RestAssured.given;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
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
@Feature("PIM API")
public class APICreateEmployeeTest extends TestBaseAPI {

    private Integer empNumber;
    private String employeeId;

    @Test(description = "OHR60: API_05 - Create employee")
    public void createEmployeeShouldSucceed() {
        // Arrange
        employeeId = "PC" + UUID.randomUUID().toString().substring(0, 8);
        String body = JsonOutput.toJson(Map.of(
                "firstName", TestConfig.firstName,
                "middleName", TestConfig.middleName,
                "lastName", TestConfig.lastName,
                "employeeId", employeeId));
        Log.info("API_05 - Bước 1: Chuẩn bị employee mới. Body: " + body);

        // Act
        Log.info("API_05 - Bước 2: POST /api/v2/pim/employees");
        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().post("/api/v2/pim/employees");

        // Assert
        Log.info("API_05 - Bước 3: Status: " + response.statusCode());
        Log.info(response.asPrettyString());
        // Chỉ giữ ID để cleanup khi response trả đúng mã riêng của test.
        if (employeeId.equals(response.jsonPath().getString("data.employeeId"))) {
            empNumber = response.jsonPath().get("data.empNumber");
        }
        Assert.assertEquals(response.statusCode(), 200, "Tạo employee phải trả 200");
        Assert.assertNotNull(empNumber, "Response tạo employee phải có empNumber");
        Assert.assertEquals(response.jsonPath().getString("data.employeeId"), employeeId,
                "POST trả sai employeeId");

        Log.info("API_05 - Bước 4: GET lại employee vừa tạo, empNumber=" + empNumber);
        Response savedEmployee = given()
                .auth().oauth2(token)
                .pathParam("empNumber", empNumber).queryParam("model", "default")
                .when().get("/api/v2/pim/employees/{empNumber}");
        Log.info("GET status: " + savedEmployee.statusCode());
        Log.info(savedEmployee.asPrettyString());
        Assert.assertEquals(savedEmployee.statusCode(), 200, "Không đọc lại được employee vừa tạo");
        Assert.assertEquals(savedEmployee.jsonPath().getInt("data.empNumber"), empNumber.intValue(),
                "GET trả sai empNumber");
        Assert.assertEquals(savedEmployee.jsonPath().getString("data.employeeId"), employeeId,
                "Employee đã lưu sai mã");
        Assert.assertEquals(savedEmployee.jsonPath().getString("data.firstName"), TestConfig.firstName,
                "Employee đã lưu sai firstName");
        Assert.assertEquals(savedEmployee.jsonPath().getString("data.lastName"), TestConfig.lastName,
                "Employee đã lưu sai lastName");
    }

    @AfterClass(alwaysRun = true)
    public void deleteCreatedEmployee() {
        if (empNumber == null) {
            return;
        }

        Log.info("API_05 - Cleanup: DELETE employee của test, empNumber=" + empNumber);
        Response cleanup = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(JsonOutput.toJson(Map.of("ids", List.of(empNumber))))
                .when().delete("/api/v2/pim/employees");
        Log.info("Cleanup status: " + cleanup.statusCode());
        Log.info(cleanup.asPrettyString());
        Assert.assertEquals(cleanup.statusCode(), 200, "Cleanup không xóa được employee: " + empNumber);

        Response remaining = given()
                .auth().oauth2(token)
                .queryParam("employeeId", employeeId).queryParam("model", "default")
                .when().get("/api/v2/pim/employees");
        Log.info("Cleanup - GET employeeId=" + employeeId + ", status: " + remaining.statusCode());
        Log.info(remaining.asPrettyString());
        Assert.assertEquals(remaining.statusCode(), 200, "Không xác minh được cleanup");
        List<?> employees = remaining.jsonPath().getList("data");
        Assert.assertNotNull(employees, "Response xác minh cleanup thiếu data");
        Assert.assertTrue(employees.isEmpty(), "Cleanup còn employee của test: " + employeeId);
    }
}
