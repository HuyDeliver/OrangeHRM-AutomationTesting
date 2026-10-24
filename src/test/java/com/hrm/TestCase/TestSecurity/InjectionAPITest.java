package com.hrm.TestCase.TestSecurity;

import static io.restassured.RestAssured.given;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.hrm.Base.TestBaseAPI;
import com.hrm.Util.Config;
import com.hrm.Util.ExelReader;
import com.hrm.Util.Log;

import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;

@Epic("Security")
@Feature("SQL Injection Test")
public class InjectionAPITest extends TestBaseAPI {
        private static final String USERS_ENDPOINT = "/api/v2/admin/users";
        private static final String EXCEL_PATH = "src/test/resources/data/Security-TestData.xlsx";
        private static final List<String> DB_ERROR_MARKERS = List.of(
                        "sql syntax", "syntax error", "sqlstate", "pdoexception",
                        "mysqli_sql_exception", "database exception", "stack trace");

        // Gọi API tìm user theo username, dùng chung cho setup và mọi test
        private Response searchUsername(String value) {
                Response r = given().auth().oauth2(token).queryParam("username", value)
                                .redirects().follow(false).when().get(USERS_ENDPOINT);
                Log.info("GET " + USERS_ENDPOINT + " | username=[" + value + "] | Status: " + r.statusCode());
                return r;
        }

        private void assertJson(Response r, String msg) {
                String type = r.contentType();
                Assert.assertTrue(type != null && type.contains("json"), msg);
        }

        // Đối chứng: tìm username thật phải ra đúng user đó, chứng minh endpoint +
        // token + bộ lọc hoạt động.
        // Nếu setup fail thì các test bên dưới không có ý nghĩa.
        @BeforeClass
        public void verifyUsernameFilterWorks() {
                String known = Config.get("username");
                Response r = searchUsername(known);
                Assert.assertEquals(r.statusCode(), 200, "Setup lỗi: kiểm tra token, quyền Admin và endpoint");
                assertJson(r, "Setup phải trả JSON");
                List<String> names = r.jsonPath().getList("data.userName", String.class);
                Assert.assertNotNull(names, "Setup lỗi: response không có data.userName");
                Assert.assertFalse(names.isEmpty(), "Setup lỗi: không tìm thấy username từ config");
                Assert.assertTrue(names.stream().anyMatch(known::equalsIgnoreCase),
                                "Setup lỗi: bộ lọc username không trả về user " + known);
        }

        @DataProvider(name = "injectionData")
        public Object[][] getInjectionData() {
                Object[][] data = ExelReader.getDataFromExel(EXCEL_PATH, "Injection");
                Assert.assertNotNull(data, "Không đọc được file Excel Security-TestData.xlsx");
                Assert.assertTrue(data.length > 0, "Sheet Injection phải có test data");
                return data;
        }

        @Test(dataProvider = "injectionData", description = "OHR55: SQL-like input từ Excel không được bypass bộ lọc")
        public void sqlLikeInputShouldBeHandledSafely(String testId, String payload, String expectedStatuses) {
                // Hiện INJ_01, INJ_02... làm tên test trong báo cáo Allure
                Allure.getLifecycle().updateTestCase(tc -> tc.setName(testId));

                // Arrange
                // Excel hay nuốt dấu ' ở đầu ô: nếu payload rỗng thì dừng ngay, tránh kết luận
                // sai
                Assert.assertTrue(payload != null && !payload.isEmpty(),
                                testId + ": payload rỗng, kiểm tra Excel (cột payload phải để định dạng Text)");
                List<String> allowed = Arrays.stream(expectedStatuses.split(","))
                                .map(String::trim).toList();
                Log.info(testId + " | SQL Injection | Admin | payload=[" + payload + "] | expected=" + allowed);

                // Act
                Response response = searchUsername(payload);

                // Assert 1: status nằm trong danh sách Excel cho phép
                Assert.assertTrue(allowed.contains(String.valueOf(response.statusCode())),
                                testId + ": status " + response.statusCode() + " ngoài expected " + allowed);

                // Assert 2: không lộ lỗi DB, không 500, đúng định dạng JSON
                assertNoDatabaseError(testId, response);

                // Assert 3: nếu 200 thì mọi user trả về phải chứa nguyên payload (không bypass
                // bộ lọc)
                if (response.statusCode() == 200) {
                        List<String> names = response.jsonPath().getList("data.userName", String.class);
                        Assert.assertNotNull(names, testId + ": response 200 phải có mảng data");
                        Log.info(testId + " | data count=" + names.size());
                        for (String name : names) {
                                Assert.assertTrue(
                                                name.toLowerCase(Locale.ROOT)
                                                                .contains(payload.toLowerCase(Locale.ROOT)),
                                                testId + ": filter bị bypass, trả về user [" + name
                                                                + "] không khớp payload");
                        }
                }
        }

        private void assertNoDatabaseError(String testId, Response response) {
                Assert.assertNotEquals(response.statusCode(), 500, testId + ": HTTP 500, backend xử lý input lỗi");
                assertJson(response, testId + ": API phải trả JSON, không phải trang login/error HTML");

                String body = response.asString().toLowerCase(Locale.ROOT);
                for (String marker : DB_ERROR_MARKERS) {
                        Assert.assertFalse(body.contains(marker), testId + ": lộ lỗi DB/nội bộ: " + marker);
                }
                Assert.assertFalse(body.contains("select ") && body.contains("from "),
                                testId + ": response có thể lộ câu truy vấn SQL");

                // Với 400/422: phải là lỗi validation của API và không được kèm dữ liệu users
                if (response.statusCode() != 200) {
                        String message = response.jsonPath().getString("error.message");
                        Log.info(testId + " | validation message=" + message);
                        Assert.assertTrue("Invalid Parameter".equalsIgnoreCase(message),
                                        testId + ": 400/422 phải là lỗi validation của API, nhận: " + message);
                        Assert.assertNull(response.jsonPath().get("data"),
                                        testId + ": response lỗi không được trả dữ liệu users");
                }
        }

        @AfterClass(alwaysRun = true)
        public void closeBrowser() {
                if (driver != null)
                        driver.quit();
        }
}
