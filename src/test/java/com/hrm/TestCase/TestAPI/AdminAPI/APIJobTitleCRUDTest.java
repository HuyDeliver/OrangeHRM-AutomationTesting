package com.hrm.TestCase.TestAPI.AdminAPI;

import static io.restassured.RestAssured.given;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.hrm.Base.TestBaseAPI;
import com.hrm.Util.Log;

import groovy.json.JsonOutput;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Epic("API Testing")
@Feature("Admin API - Job Title CRUD")
public class APIJobTitleCRUDTest extends TestBaseAPI {

    private static final String ENDPOINT = "/api/v2/admin/job-titles";
    private Integer jobTitleId;
    private String jobTitleName;

    @Test(priority = 1, description = "OHR77: Admin API - Tạo mới Job Title (POST)")
    public void createJobTitleAPI() {
        jobTitleName = "API_Job_" + UUID.randomUUID().toString().substring(0, 6);
        String body = JsonOutput.toJson(Map.of(
                "title", jobTitleName,
                "description", "Mo ta cong viec tao tu API Test",
                "note", "Ghi chu API Test"));

        Log.info("OHR77 - POST " + ENDPOINT + " | Body: " + body);
        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().post(ENDPOINT);

        Log.info("Status: " + response.statusCode());
        Log.info(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 200, "Tạo Job Title phải trả về 200");
        jobTitleId = response.jsonPath().get("data.id");
        Assert.assertNotNull(jobTitleId, "Response phải có id của Job Title");
        Assert.assertEquals(response.jsonPath().getString("data.title"), jobTitleName, "Trả về sai tên Job Title");
    }

    @Test(priority = 2, dependsOnMethods = "createJobTitleAPI", description = "OHR78: Admin API - Xem chi tiết Job Title (GET)")
    public void getJobTitleDetailAPI() {
        Log.info("OHR78 - GET " + ENDPOINT + "/" + jobTitleId);
        Response response = given()
                .auth().oauth2(token)
                .pathParam("id", jobTitleId)
                .when().get(ENDPOINT + "/{id}");

        Log.info("Status: " + response.statusCode());
        Log.info(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 200, "Lấy chi tiết Job Title phải trả về 200");
        Assert.assertEquals(response.jsonPath().getInt("data.id"), jobTitleId.intValue(), "Trả về sai ID");
        Assert.assertEquals(response.jsonPath().getString("data.title"), jobTitleName, "Trả về sai tên Job Title");
    }

    @Test(priority = 3, dependsOnMethods = "createJobTitleAPI", description = "OHR79: Admin API - Cập nhật Job Title (PUT)")
    public void updateJobTitleAPI() {
        String updatedTitle = jobTitleName + "_Updated";
        String body = JsonOutput.toJson(Map.of(
                "title", updatedTitle,
                "description", "Mo ta da cap nhat qua PUT API",
                "note", "Ghi chu cap nhat"));

        Log.info("OHR79 - PUT " + ENDPOINT + "/" + jobTitleId);
        Response response = given()
                .auth().oauth2(token)
                .pathParam("id", jobTitleId)
                .contentType(ContentType.JSON).body(body)
                .when().put(ENDPOINT + "/{id}");

        Log.info("Status: " + response.statusCode());
        Log.info(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 200, "Cập nhật Job Title phải trả về 200");
        Assert.assertEquals(response.jsonPath().getString("data.title"), updatedTitle, "Tên mới chưa được cập nhật");
        jobTitleName = updatedTitle;
    }

    @Test(priority = 4, description = "OHR80: Admin API - Từ chối tạo Job Title thiếu trường bắt buộc (422)")
    public void createJobTitleMissingTitleShouldFail() {
        String body = JsonOutput.toJson(Map.of("description", "Thieu truong title bat buoc"));

        Log.info("OHR80 - POST thiếu title");
        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().post(ENDPOINT);

        Log.info("Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 422, "Thiếu title bắt buộc phải trả về 422");
    }

    @Test(priority = 5, description = "OHR81: Admin API - Lấy Job Title không tồn tại trả về 404")
    public void getNonExistentJobTitleShouldReturn404() {
        Response response = given()
                .auth().oauth2(token)
                .pathParam("id", 999999)
                .when().get(ENDPOINT + "/{id}");

        Log.info("OHR81 - Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 404, "ID không tồn tại phải trả về 404");
    }

    @Test(priority = 6, dependsOnMethods = "createJobTitleAPI", description = "OHR82: Admin API - Xóa Job Title (DELETE)")
    public void deleteJobTitleAPI() {
        String body = JsonOutput.toJson(Map.of("ids", List.of(jobTitleId)));

        Log.info("OHR82 - DELETE " + ENDPOINT + " | ids=" + jobTitleId);
        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().delete(ENDPOINT);

        Log.info("Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Xóa Job Title phải trả về 200");
        jobTitleId = null;
    }

    @AfterClass(alwaysRun = true)
    public void cleanupJobTitle() {
        if (jobTitleId != null) {
            given().auth().oauth2(token)
                    .contentType(ContentType.JSON)
                    .body(JsonOutput.toJson(Map.of("ids", List.of(jobTitleId))))
                    .when().delete(ENDPOINT);
        }
    }
}
