# Recruitment API: Validation & Boundary

> Cách chạy hiện tại (26/10/2026): `mvn test "-Dmodule=RecruitmentAPI"` qua `testng.xml`; lệnh này chạy toàn bộ module đã chọn, gồm cả issue. Các tên XML/lệnh riêng và số liệu bên dưới là lịch sử trước khi gộp. Xem [hướng dẫn module](test-execution.md).

8 testcase kiểm tra dữ liệu khi tạo Candidate trên OrangeHRM local.
Mỗi testcase chỉ thay đổi một điều kiện để dễ hiểu nguyên nhân PASS/FAIL.

## File và cách chạy

Package: `com.hrm.TestCase.TestAPI.RecruitmentAPI`.

| File | Case | Nội dung |
|---|---|---|
| `APICandidateRequiredFieldTest.java` | REC_VAL_01–03 | Thiếu trường bắt buộc |
| `APICandidateValidationTest.java` | REC_VAL_04–05 | Sai định dạng, sai kiểu dữ liệu |
| `APICandidateBoundaryTest.java` | REC_VAL_06–08 | Tên dài 29, 30, 31 ký tự |

```powershell
mvn -o -DskipTests test-compile
mvn -o "-Dtestng.suite=testng_recruitment_validation.xml" test
```

`-o` dùng dependency Maven đã có trên máy. Nếu máy mới chưa tải dependency, bỏ `-o`.
Suite riêng chỉ chạy 8 case này, dùng `TestListener` hiện có và chạy tuần tự.

## Hiểu code theo thứ tự

1. `extends TestBaseAPI`: dùng lại cấu hình API và token của project. Base mở trình duyệt để lấy OAuth token động bằng tài khoản trong config. Các request Candidate dùng Rest Assured.
2. `@DataProvider`: một bảng nhỏ ngay trong class. Mỗi dòng là một lần chạy test, có Test ID riêng. Ba dòng không có nghĩa là phải viết ba đoạn request giống nhau.
3. `// Arrange`: chuẩn bị body. `Map` chứa các cặp tên field và giá trị; `JsonOutput.toJson(body)` chuyển thành JSON để gửi.
4. `// Act`: gửi request thật.
5. `// Assert`: in expected, actual status và body rồi so sánh kết quả.
6. `@AfterMethod(alwaysRun = true)`: chạy cleanup sau từng case, kể cả khi assertion FAIL.

Các lệnh Rest Assured trong test:

| Cú pháp | Ý nghĩa |
|---|---|
| `given()` | Bắt đầu chuẩn bị request |
| `.auth().oauth2(token)` | Gửi Bearer token đã lấy từ base |
| `.contentType(ContentType.JSON)` | Báo cho server body là JSON |
| `.body(requestBody)` | Gắn dữ liệu cần gửi |
| `.when().post("/api/v2/recruitment/candidates")` | Gửi POST tạo ứng viên |
| `.pathParam("id", candidateId)` | Điền ID thật vào `{id}` của URL GET |
| `response.statusCode()` | Đọc HTTP status |
| `response.jsonPath().getList("error.data.invalidParamKeys")` | Lấy danh sách field bị báo lỗi |

Không hard-code token, tài khoản hoặc candidate ID trong test. Tên cơ bản và email domain lấy từ `TestConfig`; tên họ dạng `REC_...` đánh dấu dữ liệu riêng của từng case.

## Từng testcase và kết quả thực tế

Request chung: **POST `/api/v2/recruitment/candidates`**, body JSON.
Base hiện tại ghép thành `http://localhost:8080/web/index.php/api/v2/recruitment/candidates`.

| Test ID | Input | Expected và assertion | Actual ngày 21/10/2026 | Kết quả |
|---|---|---|---|---|
| REC_VAL_01 | Bỏ key `firstName` | 422; lỗi chứa `firstName`; không trả `data` candidate | 422; `invalidParamKeys: ["firstName"]` | PASS |
| REC_VAL_02 | Bỏ key `lastName` | 422; lỗi chứa `lastName`; không trả `data` candidate | 422; `invalidParamKeys: ["lastName"]` | PASS |
| REC_VAL_03 | Bỏ key `email` | 422; lỗi chứa `email`; không trả `data` candidate | 422; `invalidParamKeys: ["email"]` | PASS |
| REC_VAL_04 | `email: "candidate-test"` | 422; lỗi chứa `email`; không trả `data` candidate | 422; `invalidParamKeys: ["email"]` | PASS |
| REC_VAL_05 | `firstName: 123` (số) | 422; lỗi chứa `firstName`; không trả `data` candidate | 422; `invalidParamKeys: ["firstName"]` | PASS |
| REC_VAL_06 | `firstName` gồm 29 chữ A | POST 200; có ID; GET 200, đúng ID và tên nguyên vẹn | POST/GET 200; ID 21; tên đủ 29 ký tự | PASS |
| REC_VAL_07 | `firstName` gồm 30 chữ A | POST 200; có ID; GET 200, đúng ID và tên nguyên vẹn | POST/GET 200; ID 22; tên đủ 30 ký tự | PASS |
| REC_VAL_08 | `firstName` gồm 31 chữ A | 422; lỗi chứa `firstName`; không trả `data` candidate | 422; `invalidParamKeys: ["firstName"]` | PASS |

Các ID 21 và 22 là kết quả của lần chạy này, không được dùng làm test data cố định.
Cleanup trả **200**, lần lượt `data: [21]` và `data: [22]`. Tất cả setup/cleanup/closeBrowser trong TestNG report đều PASS.
Compile thành công. Suite kết thúc lúc **22:34:59 ngày 21/10/2026 (UTC+7)**: **8 PASS, 0 FAIL, 0 SKIP**.

## Vì sao kiểm tra như vậy?

- Case 01–03: server phải kiểm tra dữ liệu bắt buộc ngay cả khi người dùng gọi API trực tiếp. `body.remove(...)` bỏ hẳn field, khác với gửi chuỗi rỗng.
- Case 04: kiểm tra định dạng email. Đây không phải phép kiểm tra hộp thư có tồn tại hoặc nhận được email.
- Case 05: số `123` và chuỗi `"123"` là hai kiểu JSON khác nhau. `Object invalidValue` cho phép giữ nguyên số trong bảng dữ liệu.
- Case 06–08: 30 là giới hạn đã xác minh. Thử 29, 30 và 31 để kiểm tra ngay dưới, đúng và vượt giới hạn. GET lại ở case hợp lệ giúp xác nhận server lưu tên nguyên vẹn.

**422 là PASS cho các case negative** nếu server báo đúng field không hợp lệ. Không đổi assertion sang 200 khi ứng dụng chấp nhận input sai.
FAIL có thể do ứng dụng không kiểm tra đúng rule, nhưng cũng có thể do token hết hạn, môi trường hoặc response thay đổi. Cần đọc status/body trong log trước khi kết luận bug.
PASS chỉ xác nhận 8 input này hoạt động đúng; không chứng minh toàn bộ Recruitment không có lỗi.

## Cleanup và log

Candidate được nhận diện bằng tên họ hoặc email riêng. Test giữ `data.id` trước assertion để vẫn xóa được nếu request sai lại tạo thành công.
Cleanup chỉ DELETE những ID đã nhận từ POST của chính testcase: `/api/v2/recruitment/candidates`, body `{"ids":[id]}`.
Cleanup kiểm tra status 200 và ID trả về đúng ID cần xóa. Không chọn một candidate có sẵn để xóa.

Log dùng `Log.info()` hiện có, vừa ghi Log4j vừa thêm bước vào Allure. Log gồm Test ID, bước, endpoint, request body, expected/actual, response và cleanup; không in Bearer token.
File `logfile.log` và Allure giữ đúng dấu tiếng Việt. Console Maven trên Windows trong lần chạy này hiển thị một số dấu sai; có thể đọc file log bằng UTF-8.
Report nằm tại `target/surefire-reports/index.html`; dữ liệu Allure nằm tại `target/allure-results`.

## Căn cứ expected

- [Create Candidate](https://api-starter-orangehrm.readme.io/reference/create-a-candidate): field, endpoint, response thành công.
- [CandidateAPI v5.9](https://github.com/orangehrm/orangehrm/blob/v5.9/src/plugins/orangehrmRecruitmentPlugin/Api/CandidateAPI.php): kiểu chuỗi, giới hạn 30, email validation; GET và DELETE.
- [AbstractRestController v5.9](https://github.com/orangehrm/orangehrm/blob/v5.9/src/plugins/orangehrmCorePlugin/Controller/Rest/V2/AbstractRestController.php): validation error trả 422.
- [InvalidParamException v5.9](https://github.com/orangehrm/orangehrm/blob/v5.9/src/plugins/orangehrmCorePlugin/Api/V2/Exception/InvalidParamException.php): lỗi chứa `invalidParamKeys`.

Không thêm case bắt buộc từ chối email trùng vì chưa có rule yêu cầu email Candidate phải duy nhất.
