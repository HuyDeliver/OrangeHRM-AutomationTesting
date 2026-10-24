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
public class APIUpdateEmployeeTest extends TestBaseAPI {

    private Integer empNumber;
    private String employeeId;

    @Test(description = "OHR61: API_06 - Update employee personal details")
    public void updateEmployeeShouldSaveNewName() {
        // Arrange: tạo employee riêng để không sửa employee có sẵn.
        employeeId = "PU" + UUID.randomUUID().toString().substring(0, 8);
        String createBody = JsonOutput.toJson(Map.of(
                "firstName", TestConfig.firstName,
                "middleName", TestConfig.middleName,
                "lastName", TestConfig.lastName,
                "employeeId", employeeId));
        Log.info("API_06 - Bước 1: POST /api/v2/pim/employees. Body: " + createBody);
        Response created = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(createBody)
                .when().post("/api/v2/pim/employees");
        Log.info("Setup status: " + created.statusCode());
        Log.info(created.asPrettyString());
        if (employeeId.equals(created.jsonPath().getString("data.employeeId"))) {
            empNumber = created.jsonPath().get("data.empNumber");
        }
        Assert.assertEquals(created.statusCode(), 200, "Setup không tạo được employee để update");
        Assert.assertNotNull(empNumber, "Setup thiếu empNumber");
        Assert.assertEquals(created.jsonPath().getString("data.employeeId"), employeeId,
                "Setup trả sai employee của test");

        String updatedFirstName = TestConfig.firstName + " API";
        String updateBody = JsonOutput.toJson(Map.of(
                "firstName", updatedFirstName,
                "middleName", TestConfig.middleName,
                "lastName", TestConfig.lastName,
                "employeeId", employeeId));

        // Act
        Log.info("API_06 - Bước 2: PUT /api/v2/pim/employees/" + empNumber + "/personal-details");
        Log.info("Update body: " + updateBody);
        Response response = given()
                .auth().oauth2(token)
                .pathParam("empNumber", empNumber)
                .contentType(ContentType.JSON).body(updateBody)
                .when().put("/api/v2/pim/employees/{empNumber}/personal-details");

        // Assert
        Log.info("API_06 - Bước 3: Status: " + response.statusCode());
        Log.info(response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200, "Update employee phải trả 200");
        Assert.assertNotNull(response.jsonPath().get("data"), "Update response phải có data");
        Assert.assertEquals(response.jsonPath().getString("data.firstName"), updatedFirstName,
                "PUT trả sai firstName mới");

        Log.info("API_06 - Bước 4: GET lại để kiểm tra tên mới đã được lưu");
        Response savedEmployee = given()
                .auth().oauth2(token)
                .pathParam("empNumber", empNumber).queryParam("model", "default")
                .when().get("/api/v2/pim/employees/{empNumber}");
        Log.info("GET status: " + savedEmployee.statusCode());
        Log.info(savedEmployee.asPrettyString());
        Assert.assertEquals(savedEmployee.statusCode(), 200, "Không đọc lại được employee sau update");
        Assert.assertEquals(savedEmployee.jsonPath().getInt("data.empNumber"), empNumber.intValue(),
                "GET trả sai employee vừa update");
        Assert.assertEquals(savedEmployee.jsonPath().getString("data.firstName"), updatedFirstName,
                "Tên mới chưa được lưu");
        Assert.assertEquals(savedEmployee.jsonPath().getString("data.employeeId"), employeeId,
                "Update làm thay đổi employeeId ngoài dự kiến");
        Assert.assertEquals(savedEmployee.jsonPath().getString("data.lastName"), TestConfig.lastName,
                "Update làm thay đổi lastName ngoài dự kiến");
    }

    @AfterClass(alwaysRun = true)
    public void deleteCreatedEmployee() {
        if (empNumber == null) {
            return;
        }

        Log.info("API_06 - Cleanup: DELETE employee của test, empNumber=" + empNumber);
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
