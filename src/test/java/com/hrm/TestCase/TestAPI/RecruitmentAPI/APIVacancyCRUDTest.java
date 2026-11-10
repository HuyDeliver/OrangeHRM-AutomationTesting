package com.hrm.TestCase.TestAPI.RecruitmentAPI;

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

import groovy.json.JsonOutput;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Epic("API Testing")
@Feature("Recruitment API - Vacancy CRUD")
public class APIVacancyCRUDTest extends TestBaseAPI {

    private static final String VACANCIES_ENDPOINT = "/api/v2/recruitment/vacancies";
    private Integer jobTitleId;
    private Integer hiringManagerEmpNumber;
    private Integer vacancyId;
    private String vacancyName;

    @BeforeClass
    public void preparePrerequisites() {
        Response jobResp = given().auth().oauth2(token)
                .queryParam("limit", 1)
                .when().get("/api/v2/admin/job-titles");
        Assert.assertEquals(jobResp.statusCode(), 200, "Setup: Không lấy được danh sách Job Title");
        jobTitleId = jobResp.jsonPath().get("data[0].id");
        Assert.assertNotNull(jobTitleId, "Setup: Cần ít nhất 1 Job Title trong hệ thống");

        Response empResp = given().auth().oauth2(token)
                .queryParam("limit", 1)
                .when().get("/api/v2/pim/employees");
        Assert.assertEquals(empResp.statusCode(), 200, "Setup: Không lấy được danh sách Employee");
        hiringManagerEmpNumber = empResp.jsonPath().get("data[0].empNumber");
        Assert.assertNotNull(hiringManagerEmpNumber, "Setup: Cần ít nhất 1 Employee làm Hiring Manager");
    }

    @Test(priority = 1, description = "OHR99: Recruitment API - Tạo vị trí tuyển dụng Vacancy mới (POST)")
    public void createVacancyAPI() {
        vacancyName = "API_Vacancy_" + UUID.randomUUID().toString().substring(0, 6);
        String body = JsonOutput.toJson(Map.of(
                "name", vacancyName,
                "jobTitleId", jobTitleId,
                "employeeId", hiringManagerEmpNumber,
                "numOfPositions", 2,
                "description", "Vi tri tuyen dung tao tu REST API",
                "status", true,
                "isPublished", true));

        Log.info("OHR99 - POST " + VACANCIES_ENDPOINT + " | Body: " + body);
        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().post(VACANCIES_ENDPOINT);

        Log.info("Status: " + response.statusCode());
        Log.info(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 200, "Tạo Vacancy phải trả về 200");
        vacancyId = response.jsonPath().get("data.id");
        Assert.assertNotNull(vacancyId, "Response phải có id của Vacancy");
        Assert.assertEquals(response.jsonPath().getString("data.name"), vacancyName, "Trả về sai tên Vacancy");
    }

    @Test(priority = 2, dependsOnMethods = "createVacancyAPI", description = "OHR100: Recruitment API - Xem chi tiết Vacancy theo ID (GET)")
    public void getVacancyDetailAPI() {
        Log.info("OHR100 - GET " + VACANCIES_ENDPOINT + "/" + vacancyId);
        Response response = given()
                .auth().oauth2(token)
                .pathParam("id", vacancyId)
                .when().get(VACANCIES_ENDPOINT + "/{id}");

        Log.info("Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Lấy chi tiết Vacancy phải trả về 200");
        Assert.assertEquals(response.jsonPath().getInt("data.id"), vacancyId.intValue(), "Trả về sai ID Vacancy");
        Assert.assertEquals(response.jsonPath().getString("data.name"), vacancyName, "Trả về sai tên Vacancy");
    }

    @Test(priority = 3, dependsOnMethods = "createVacancyAPI", description = "OHR101: Recruitment API - Cập nhật thông tin Vacancy (PUT)")
    public void updateVacancyAPI() {
        String updatedName = vacancyName + "_Senior";
        String body = JsonOutput.toJson(Map.of(
                "name", updatedName,
                "jobTitleId", jobTitleId,
                "employeeId", hiringManagerEmpNumber,
                "numOfPositions", 5,
                "description", "Cap nhat so luong tuyen dung qua API",
                "status", false,
                "isPublished", false));

        Log.info("OHR101 - PUT " + VACANCIES_ENDPOINT + "/" + vacancyId);
        Response response = given()
                .auth().oauth2(token)
                .pathParam("id", vacancyId)
                .contentType(ContentType.JSON).body(body)
                .when().put(VACANCIES_ENDPOINT + "/{id}");

        Log.info("Status: " + response.statusCode());
        Log.info(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 200, "Cập nhật Vacancy phải trả về 200");
        Assert.assertEquals(response.jsonPath().getString("data.name"), updatedName, "Tên Vacancy chưa được cập nhật");
        Assert.assertEquals(response.jsonPath().getInt("data.numOfPositions"), 5, "Số lượng vị trí chưa được cập nhật");
        vacancyName = updatedName;
    }

    @Test(priority = 4, description = "OHR102: Recruitment API - Từ chối tạo Vacancy thiếu jobTitleId (422)")
    public void createVacancyMissingJobTitleShouldFail() {
        String body = JsonOutput.toJson(Map.of(
                "name", "Invalid_Vacancy_" + UUID.randomUUID().toString().substring(0, 5),
                "employeeId", hiringManagerEmpNumber,
                "status", true,
                "isPublished", true));

        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().post(VACANCIES_ENDPOINT);

        Log.info("OHR102 - Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 422, "Thiếu jobTitleId bắt buộc phải trả về 422");
    }

    @Test(priority = 5, dependsOnMethods = "createVacancyAPI", description = "OHR103: Recruitment API - Xóa Vacancy (DELETE)")
    public void deleteVacancyAPI() {
        String body = JsonOutput.toJson(Map.of("ids", List.of(vacancyId)));

        Log.info("OHR103 - DELETE " + VACANCIES_ENDPOINT + " | ids=" + vacancyId);
        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().delete(VACANCIES_ENDPOINT);

        Log.info("Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Xóa Vacancy phải trả về 200");
        vacancyId = null;
    }

    @AfterClass(alwaysRun = true)
    public void cleanupVacancy() {
        if (vacancyId != null) {
            given().auth().oauth2(token)
                    .contentType(ContentType.JSON)
                    .body(JsonOutput.toJson(Map.of("ids", List.of(vacancyId))))
                    .when().delete(VACANCIES_ENDPOINT);
        }
    }
}
