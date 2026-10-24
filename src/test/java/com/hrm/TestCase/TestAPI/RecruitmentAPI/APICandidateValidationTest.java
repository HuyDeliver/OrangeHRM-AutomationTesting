package com.hrm.TestCase.TestAPI.RecruitmentAPI;

import static io.restassured.RestAssured.given;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
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
@Feature("Recruitment API")
public class APICandidateValidationTest extends TestBaseAPI {

    private Integer candidateId;
    private String currentTestId;

    @DataProvider(name = "invalidValues")
    public Object[][] invalidValues() {
        return new Object[][] {
                { "REC_VAL_04", "email", "candidate-test" },
                { "REC_VAL_05", "firstName", 123 } // Số JSON, không phải chuỗi "123".
        };
    }

    @Test(dataProvider = "invalidValues", description = "OHR65: REC_VAL_04-05 - Candidate sai định dạng hoặc kiểu dữ liệu phải bị từ chối")
    public void invalidFieldValueShouldBeRejected(String testId, String field, Object invalidValue) {
        // Arrange
        currentTestId = testId;
        candidateId = null;
        String marker = "REC_" + UUID.randomUUID().toString().substring(0, 8);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("firstName", TestConfig.firstName);
        body.put("lastName", marker);
        body.put("email", marker + TestConfig.shortlistEmailDomain);
        body.put(field, invalidValue); // Chỉ thay một field bằng giá trị sai.
        String requestBody = JsonOutput.toJson(body);
        Log.info(testId + " - Bước 1: Gán " + field + " = " + invalidValue);
        Log.info("Request body: " + requestBody);

        // Act
        Log.info(testId + " - Bước 2: POST /api/v2/recruitment/candidates bằng token từ TestBaseAPI");
        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(requestBody)
                .when().post("/api/v2/recruitment/candidates");

        // Assert
        Log.info(testId + " - Bước 3: Expected 422; Actual " + response.statusCode());
        Log.info(response.asPrettyString());
        // Chỉ giữ ID của candidate mang dấu riêng của testcase này.
        if (marker.equals(response.jsonPath().getString("data.lastName"))) {
            candidateId = response.jsonPath().get("data.id");
        }
        Assert.assertEquals(response.statusCode(), 422, "Giá trị sai của " + field + " phải trả 422");
        Log.info(testId + " - Bước 4: Kiểm tra response báo lỗi đúng field " + field);
        List<String> invalidFields = response.jsonPath().getList("error.data.invalidParamKeys");
        Assert.assertNotNull(invalidFields, "Response phải có danh sách field không hợp lệ");
        Assert.assertTrue(invalidFields.contains(field), "Response chưa báo lỗi field " + field);
        Assert.assertNull(response.jsonPath().get("data"), "Request sai dữ liệu không được trả candidate mới");
        Log.info(testId + " - PASS: API đã từ chối giá trị sai và báo đúng field");
    }

    @AfterMethod(alwaysRun = true)
    public void deleteCreatedCandidate() {
        if (candidateId == null) {
            Log.info(currentTestId + " - Cleanup: Không có ID candidate được tạo để xóa");
            return;
        }
        Log.info(currentTestId + " - Cleanup: DELETE /api/v2/recruitment/candidates, ids=[" + candidateId + "]");
        Response cleanup = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(JsonOutput.toJson(Map.of("ids", List.of(candidateId))))
                .when().delete("/api/v2/recruitment/candidates");
        Log.info("Cleanup - Expected 200; Actual " + cleanup.statusCode());
        Log.info(cleanup.asPrettyString());
        Assert.assertEquals(cleanup.statusCode(), 200, "Cleanup không xóa được candidate " + candidateId);
        Assert.assertEquals(cleanup.jsonPath().getList("data"), List.of(candidateId), "Cleanup trả sai ID đã xóa");
        candidateId = null;
        Log.info(currentTestId + " - Cleanup hoàn tất");
    }
}
