package com.hrm.TestCase.TestAPI.PIMAPI;

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
@Feature("PIM API - Employee Sub-resource CRUD")
public class APIEmployeeDetailCRUDTest extends TestBaseAPI {

    private static final String EMPLOYEES_ENDPOINT = "/api/v2/pim/employees";
    private Integer empNumber;
    private String employeeId;

    @BeforeClass
    public void createTempEmployee() {
        employeeId = "DTL" + UUID.randomUUID().toString().substring(0, 6);
        String body = JsonOutput.toJson(Map.of(
                "firstName", TestConfig.firstName,
                "middleName", TestConfig.middleName,
                "lastName", TestConfig.lastName,
                "employeeId", employeeId));

        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().post(EMPLOYEES_ENDPOINT);

        Assert.assertEquals(response.statusCode(), 200, "Setup: Không tạo được employee tạm");
        empNumber = response.jsonPath().get("data.empNumber");
        Assert.assertNotNull(empNumber, "Setup: Thiếu empNumber");
    }

    @Test(priority = 1, description = "OHR104: PIM API - Lấy thông tin Personal Details của Employee (GET)")
    public void getPersonalDetailsAPI() {
        Response response = given()
                .auth().oauth2(token)
                .pathParam("empNumber", empNumber)
                .when().get("/api/v2/pim/employees/{empNumber}/personal-details");

        Log.info("OHR104 - Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Lấy Personal Details phải trả về 200");
        Assert.assertEquals(response.jsonPath().getInt("data.empNumber"), empNumber.intValue(), "Trả về sai empNumber");
        Assert.assertEquals(response.jsonPath().getString("data.employeeId"), employeeId, "Trả về sai employeeId");
    }

    @Test(priority = 2, description = "OHR105: PIM API - Lấy thông tin Contact Details của Employee (GET)")
    public void getContactDetailsAPI() {
        Response response = given()
                .auth().oauth2(token)
                .pathParam("empNumber", empNumber)
                .when().get("/api/v2/pim/employee/{empNumber}/contact-details");

        Log.info("OHR105 - Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Lấy Contact Details phải trả về 200");
        Assert.assertNotNull(response.jsonPath().get("data"), "Response Contact Details thiếu data");
    }

    @Test(priority = 3, description = "OHR106: PIM API - Cập nhật thông tin Contact Details của Employee (PUT)")
    public void updateContactDetailsAPI() {
        String body = JsonOutput.toJson(Map.of(
                "street1", TestConfig.street1,
                "street2", TestConfig.street2,
                "city", TestConfig.cityName,
                "province", TestConfig.provinceName,
                "zipCode", TestConfig.postalCode,
                "countryCode", "VN",
                "mobile", TestConfig.mobilePhone));

        Log.info("OHR106 - PUT /api/v2/pim/employee/" + empNumber + "/contact-details");
        Response response = given()
                .auth().oauth2(token)
                .pathParam("empNumber", empNumber)
                .contentType(ContentType.JSON).body(body)
                .when().put("/api/v2/pim/employee/{empNumber}/contact-details");

        Log.info("Status: " + response.statusCode());
        Log.info(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 200, "Cập nhật Contact Details phải trả về 200");
        Assert.assertEquals(response.jsonPath().getString("data.street1"), TestConfig.street1, "street1 chưa được lưu");
        Assert.assertEquals(response.jsonPath().getString("data.mobile"), TestConfig.mobilePhone, "mobile chưa được lưu");
    }

    @Test(priority = 4, description = "OHR107: PIM API - Lấy thông tin Job Details của Employee (GET)")
    public void getJobDetailsAPI() {
        Response response = given()
                .auth().oauth2(token)
                .pathParam("empNumber", empNumber)
                .when().get("/api/v2/pim/employees/{empNumber}/job-details");

        Log.info("OHR107 - Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Lấy Job Details phải trả về 200");
        Assert.assertNotNull(response.jsonPath().get("data"), "Response Job Details thiếu data");
    }

    @Test(priority = 5, description = "OHR108: PIM API - Từ chối tạo Employee thiếu lastName bắt buộc (422)")
    public void createEmployeeMissingLastNameShouldFail() {
        String body = JsonOutput.toJson(Map.of(
                "firstName", TestConfig.firstName,
                "employeeId", "ERR" + UUID.randomUUID().toString().substring(0, 5)));

        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().post(EMPLOYEES_ENDPOINT);

        Log.info("OHR108 - Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 422, "Thiếu lastName bắt buộc phải trả về 422");
    }

    @AfterClass(alwaysRun = true)
    public void cleanupTempEmployee() {
        if (empNumber != null) {
            given().auth().oauth2(token)
                    .contentType(ContentType.JSON)
                    .body(JsonOutput.toJson(Map.of("ids", List.of(empNumber))))
                    .when().delete(EMPLOYEES_ENDPOINT);
        }
    }
}
