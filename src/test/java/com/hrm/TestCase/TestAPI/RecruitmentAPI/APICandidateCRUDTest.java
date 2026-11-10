package com.hrm.TestCase.TestAPI.RecruitmentAPI;

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
@Feature("Recruitment API - Candidate CRUD")
public class APICandidateCRUDTest extends TestBaseAPI {

    private static final String ENDPOINT = "/api/v2/recruitment/candidates";
    private Integer candidateId;
    private String candidateLastName;
    private String candidateEmail;

    @Test(priority = 1, description = "OHR94: Recruitment API - Tạo hồ sơ Candidate mới (POST)")
    public void createCandidateAPI() {
        candidateLastName = "Cand_" + UUID.randomUUID().toString().substring(0, 6);
        candidateEmail = candidateLastName.toLowerCase() + TestConfig.shortlistEmailDomain;
        String body = JsonOutput.toJson(Map.of(
                "firstName", TestConfig.firstName,
                "middleName", TestConfig.middleName,
                "lastName", candidateLastName,
                "email", candidateEmail,
                "contactNumber", "0987654321",
                "comment", "Ho so ung vien tao qua REST API"));

        Log.info("OHR94 - POST " + ENDPOINT + " | Body: " + body);
        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().post(ENDPOINT);

        Log.info("Status: " + response.statusCode());
        Log.info(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 200, "Tạo Candidate phải trả về 200");
        candidateId = response.jsonPath().get("data.id");
        Assert.assertNotNull(candidateId, "Response phải có id của Candidate");
        Assert.assertEquals(response.jsonPath().getString("data.lastName"), candidateLastName, "Trả về sai lastName");
        Assert.assertEquals(response.jsonPath().getString("data.email"), candidateEmail, "Trả về sai email");
    }

    @Test(priority = 2, dependsOnMethods = "createCandidateAPI", description = "OHR95: Recruitment API - Xem chi tiết Candidate theo ID (GET)")
    public void getCandidateDetailAPI() {
        Log.info("OHR95 - GET " + ENDPOINT + "/" + candidateId);
        Response response = given()
                .auth().oauth2(token)
                .pathParam("id", candidateId)
                .when().get(ENDPOINT + "/{id}");

        Log.info("Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Lấy chi tiết Candidate phải trả về 200");
        Assert.assertEquals(response.jsonPath().getInt("data.id"), candidateId.intValue(), "Trả về sai ID Candidate");
        Assert.assertEquals(response.jsonPath().getString("data.email"), candidateEmail, "Trả về sai email Candidate");
    }

    @Test(priority = 3, dependsOnMethods = "createCandidateAPI", description = "OHR96: Recruitment API - Cập nhật thông tin Candidate (PUT)")
    public void updateCandidateAPI() {
        String updatedEmail = "updated_" + candidateEmail;
        String body = JsonOutput.toJson(Map.of(
                "firstName", TestConfig.firstName,
                "middleName", TestConfig.middleName,
                "lastName", candidateLastName,
                "email", updatedEmail,
                "contactNumber", "0912345678",
                "comment", "Da cap nhat thong tin lien he qua PUT API"));

        Log.info("OHR96 - PUT " + ENDPOINT + "/" + candidateId);
        Response response = given()
                .auth().oauth2(token)
                .pathParam("id", candidateId)
                .contentType(ContentType.JSON).body(body)
                .when().put(ENDPOINT + "/{id}");

        Log.info("Status: " + response.statusCode());
        Log.info(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 200, "Cập nhật Candidate phải trả về 200");
        Assert.assertEquals(response.jsonPath().getString("data.email"), updatedEmail, "Email Candidate chưa được cập nhật");
        candidateEmail = updatedEmail;
    }

    @Test(priority = 4, description = "OHR97: Recruitment API - Lấy Candidate không tồn tại trả về 404")
    public void getNonExistentCandidateShouldReturn404() {
        Response response = given()
                .auth().oauth2(token)
                .pathParam("id", 999999)
                .when().get(ENDPOINT + "/{id}");

        Log.info("OHR97 - Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 404, "ID Candidate không tồn tại phải trả về 404");
    }

    @Test(priority = 5, dependsOnMethods = "createCandidateAPI", description = "OHR98: Recruitment API - Xóa hồ sơ Candidate (DELETE)")
    public void deleteCandidateAPI() {
        String body = JsonOutput.toJson(Map.of("ids", List.of(candidateId)));

        Log.info("OHR98 - DELETE " + ENDPOINT + " | ids=" + candidateId);
        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().delete(ENDPOINT);

        Log.info("Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Xóa Candidate phải trả về 200");
        candidateId = null;
    }

    @AfterClass(alwaysRun = true)
    public void cleanupCandidate() {
        if (candidateId != null) {
            given().auth().oauth2(token)
                    .contentType(ContentType.JSON)
                    .body(JsonOutput.toJson(Map.of("ids", List.of(candidateId))))
                    .when().delete(ENDPOINT);
        }
    }
}
