package com.hrm.TestCase.TestAPI.RecruitmentAPI;

import static io.restassured.RestAssured.given;

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
public class APICandidateBoundaryTest extends TestBaseAPI {

    private Integer candidateId;
    private String currentTestId;

    @DataProvider(name = "nameLengths")
    public Object[][] nameLengths() {
        return new Object[][] {
                { "REC_VAL_06", 29, 200 },
                { "REC_VAL_07", 30, 200 },
                { "REC_VAL_08", 31, 422 }
        };
    }

    @Test(dataProvider = "nameLengths", description = "OHR66: REC_VAL_06-08 - Kiểm tra giới hạn firstName: 29, 30 và 31 ký tự")
    public void firstNameLengthShouldFollowLimit(String testId, int length, int expectedStatus) {
        // Arrange
        currentTestId = testId;
        candidateId = null;
        String firstName = "A".repeat(length); // Tạo đúng số ký tự cần kiểm tra.
        String marker = "REC_" + UUID.randomUUID().toString().substring(0, 8);
        String body = JsonOutput.toJson(Map.of(
                "firstName", firstName,
                "lastName", marker,
                "email", marker + TestConfig.shortlistEmailDomain));
        Log.info(testId + " - Bước 1: Chuẩn bị firstName dài " + length + " ký tự; giới hạn là 30");
        Log.info("Request body: " + body);

        // Act
        Log.info(testId + " - Bước 2: POST /api/v2/recruitment/candidates bằng token từ TestBaseAPI");
        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().post("/api/v2/recruitment/candidates");

        // Assert
        Log.info(testId + " - Bước 3: Expected " + expectedStatus + "; Actual " + response.statusCode());
        Log.info(response.asPrettyString());
        if (marker.equals(response.jsonPath().getString("data.lastName"))) {
            candidateId = response.jsonPath().get("data.id");
        }
        Assert.assertEquals(response.statusCode(), expectedStatus, "API xử lý sai tên dài " + length + " ký tự");

        if (expectedStatus == 422) {
            Log.info(testId + " - Bước 4: Kiểm tra API từ chối firstName quá dài");
            List<String> invalidFields = response.jsonPath().getList("error.data.invalidParamKeys");
            Assert.assertNotNull(invalidFields, "Response phải có danh sách field không hợp lệ");
            Assert.assertTrue(invalidFields.contains("firstName"), "Response phải báo lỗi firstName");
            Assert.assertNull(response.jsonPath().get("data"), "Tên quá dài không được tạo candidate mới");
            Log.info(testId + " - PASS: API đã từ chối tên 31 ký tự");
            return;
        }

        Assert.assertNotNull(candidateId, "Response tạo candidate phải có ID và đúng dấu riêng của test");
        Log.info(testId + " - Bước 4: GET /api/v2/recruitment/candidates/" + candidateId);
        Response savedCandidate = given()
                .auth().oauth2(token)
                .pathParam("id", candidateId)
                .when().get("/api/v2/recruitment/candidates/{id}");
        Log.info("GET - Expected 200; Actual " + savedCandidate.statusCode());
        Log.info(savedCandidate.asPrettyString());
        Log.info(testId + " - Bước 5: Kiểm tra đúng ID và tên đã lưu nguyên vẹn");
        Assert.assertEquals(savedCandidate.statusCode(), 200, "Không đọc lại được candidate vừa tạo");
        Assert.assertEquals(savedCandidate.jsonPath().getInt("data.id"), candidateId.intValue(), "GET trả sai ID");
        Assert.assertEquals(savedCandidate.jsonPath().getString("data.firstName"), firstName, "Tên lưu sai hoặc bị cắt");
        Log.info(testId + " - PASS: Đã lưu đúng tên " + length + " ký tự");
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
