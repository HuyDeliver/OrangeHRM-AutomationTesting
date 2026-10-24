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
public class APIDeleteEmployeeTest extends TestBaseAPI {

    private Integer empNumber;
    private String employeeId;

    @Test(description = "OHR62: API_07 - Delete employee")
    public void deleteEmployeeShouldRemoveItFromList() {
        // Arrange: chỉ xóa employee do chính testcase này tạo.
        employeeId = "PD" + UUID.randomUUID().toString().substring(0, 8);
        String createBody = JsonOutput.toJson(Map.of(
                "firstName", TestConfig.firstName,
                "middleName", TestConfig.middleName,
                "lastName", TestConfig.lastName,
                "employeeId", employeeId));
        Log.info("API_07 - Bước 1: POST /api/v2/pim/employees. Body: " + createBody);
        Response created = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(createBody)
                .when().post("/api/v2/pim/employees");
        Log.info("Setup status: " + created.statusCode());
        Log.info(created.asPrettyString());
        if (employeeId.equals(created.jsonPath().getString("data.employeeId"))) {
            empNumber = created.jsonPath().get("data.empNumber");
        }
        Assert.assertEquals(created.statusCode(), 200, "Setup không tạo được employee để delete");
        Assert.assertNotNull(empNumber, "Setup thiếu empNumber");
        Assert.assertEquals(created.jsonPath().getString("data.employeeId"), employeeId,
                "Setup trả sai employee của test");

        // Act
        String deleteBody = JsonOutput.toJson(Map.of("ids", List.of(empNumber)));
        Log.info("API_07 - Bước 2: DELETE /api/v2/pim/employees. Body: " + deleteBody);
        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(deleteBody)
                .when().delete("/api/v2/pim/employees");

        // Assert
        Log.info("API_07 - Bước 3: Status: " + response.statusCode());
        Log.info(response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200, "Delete employee phải trả 200");

        // Tìm theo mã riêng, tránh GET detail ID đã xóa trả 422 ở bản local.
        Log.info("API_07 - Bước 4: GET list với employeeId=" + employeeId);
        Response remaining = given()
                .auth().oauth2(token)
                .queryParam("employeeId", employeeId).queryParam("model", "default")
                .when().get("/api/v2/pim/employees");
        Log.info("GET status: " + remaining.statusCode());
        Log.info(remaining.asPrettyString());
        Assert.assertEquals(remaining.statusCode(), 200, "Không kiểm tra được danh sách sau delete");
        List<?> employees = remaining.jsonPath().getList("data");
        Assert.assertNotNull(employees, "Response sau delete phải có data");
        Assert.assertTrue(employees.isEmpty(), "Employee vẫn tồn tại sau delete: " + employeeId);
        empNumber = null; // Đã xác nhận xóa, không cần cleanup lần nữa.
    }

    @AfterClass(alwaysRun = true)
    public void deleteEmployeeIfTestFailed() {
        if (empNumber == null) {
            return;
        }

        Log.info("API_07 - Cleanup dự phòng: kiểm tra employeeId=" + employeeId);
        Response remaining = given()
                .auth().oauth2(token)
                .queryParam("employeeId", employeeId).queryParam("model", "default")
                .when().get("/api/v2/pim/employees");
        Log.info("Cleanup - GET status: " + remaining.statusCode());
        Log.info(remaining.asPrettyString());
        Assert.assertEquals(remaining.statusCode(), 200, "Không kiểm tra được employee để cleanup");
        List<?> employees = remaining.jsonPath().getList("data");
        Assert.assertNotNull(employees, "Response cleanup thiếu data");
        if (employees.isEmpty()) {
            return;
        }

        Log.info("API_07 - Cleanup: DELETE employee của test, empNumber=" + empNumber);
        Response cleanup = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(JsonOutput.toJson(Map.of("ids", List.of(empNumber))))
                .when().delete("/api/v2/pim/employees");
        Log.info("Cleanup status: " + cleanup.statusCode());
        Log.info(cleanup.asPrettyString());
        Assert.assertEquals(cleanup.statusCode(), 200, "Cleanup không xóa được employee: " + empNumber);
    }
}
