# PIM API — Create, Update, Delete

Ba class nằm trong `com.hrm.TestCase.TestAPI.PIMAPI`, mỗi class có một testcase và chạy độc lập. Các testcase GET cũ vẫn nằm trong suite riêng `testng_pimapi.xml`.

## Cách chạy

```powershell
mvn -o -DskipTests test-compile
mvn -o "-Dtestng.suite=testng_pimcrud.xml" test
```

Suite mới dùng `TestListener` hiện có. Cả ba class kế thừa `TestBaseAPI`: Base thiết lập URL local, lấy Bearer token động qua `OAuthTokenManager` và đóng browser sau class. Test chỉ dùng `.auth().oauth2(token)`, không tự viết lại login.

## Dữ liệu và cách đọc code

- Tên employee lấy từ `TestConfig.firstName`, `middleName`, `lastName`.
- `employeeId` là mã hiển thị, gồm tiền tố `PC`/`PU`/`PD` và 8 ký tự ngẫu nhiên, tổng cộng 10 ký tự. Mỗi lần chạy tạo mã mới để giảm khả năng trùng.
- `empNumber` là ID nội bộ do POST trả về. GET detail, PUT và DELETE dùng ID này; không lấy employee có sẵn để sửa/xóa.
- Chỉ giữ `empNumber` để cleanup khi `data.employeeId` trong POST response khớp mã riêng vừa gửi. Nếu không khớp, test FAIL và không tự xóa ID chưa xác nhận; xem log để kiểm tra thủ công.
- `Map.of(...)` chuẩn bị các field; `JsonOutput.toJson(...)` chuyển thành JSON string, tận dụng `groovy-json` đã có trong dependency hiện tại.
- `given()` chuẩn bị request; `contentType(ContentType.JSON)` khai báo định dạng body; `body(...)` gửi dữ liệu; `pathParam(...)` điền ID vào đường dẫn; `queryParam(...)` thêm bộ lọc; `when().post/put/delete/get(...)` thực hiện request.
- `response.jsonPath()` đọc field trong JSON. `Assert` sai làm testcase FAIL. `Log.info` ghi các bước, endpoint, input, status và response qua Log4j/Allure hiện có.
- `@AfterClass(alwaysRun = true)` cleanup trước khi Base đóng browser. Nếu không nhận được ID của employee test, cleanup không đoán ID để xóa. Lỗi cleanup phải được xem trong configuration results của TestNG, kể cả khi method test đã được log PASS.

## API_05 — Create employee

File: `APICreateEmployeeTest.java`. Method: `createEmployeeShouldSucceed`.

1. Input gồm tên từ TestConfig và mã mới có tiền tố `PC`.
2. Gửi `POST /api/v2/pim/employees` bằng Bearer token của Base.
3. Expected: `200`, có `empNumber`, trả đúng `employeeId`.
4. GET `/api/v2/pim/employees/{empNumber}?model=default` để đọc lại. Assert `200`, đúng ID nội bộ, mã employee, firstName và lastName.
5. Cleanup DELETE employee vừa tạo rồi GET list theo mã riêng, assert `200` và `data` là danh sách rỗng.

PASS nghĩa là tạo được employee, GET lại đúng dữ liệu. Cleanup thành công nghĩa là dữ liệu thử đã được xóa và xác nhận không còn trong list theo mã đó. FAIL cần xem status/body để phân biệt lỗi tạo, lỗi dữ liệu trả về, lỗi đọc lại hoặc lỗi token/quyền.

## API_06 — Update employee

File: `APIUpdateEmployeeTest.java`. Method: `updateEmployeeShouldSaveNewName`.

1. Arrange POST tạo employee riêng với mã `PU...`. Nếu bước này FAIL, chưa kiểm tra được hành vi PUT.
2. Input mới: `firstName = TestConfig.firstName + " API"`; giữ middleName, lastName và employeeId của employee test.
3. Gửi `PUT /api/v2/pim/employees/{empNumber}/personal-details`. Endpoint PUT bắt buộc firstName/lastName. Source local còn cập nhật các field tùy chọn khi nhận request nên chỉ thử trên employee mới của test.
4. Expected: `200`, có `data`, response trả firstName mới.
5. GET detail lại, assert `200`, đúng empNumber, firstName mới; employeeId và lastName vẫn đúng input.
6. Cleanup DELETE và xác nhận list theo mã riêng rỗng.

PASS nghĩa là tên mới thực sự đọc lại được từ GET, không chỉ được phản hồi trong PUT. Nếu PUT trả `200` nhưng GET vẫn trả tên cũ, test FAIL vì dữ liệu lưu không đúng expected; cần kiểm tra response và trạng thái hệ thống để xác minh nguyên nhân.

## API_07 — Delete employee

File: `APIDeleteEmployeeTest.java`. Method: `deleteEmployeeShouldRemoveItFromList`.

1. Arrange POST tạo employee riêng với mã `PD...`, kiểm tra status, ID và mã employee.
2. Gửi `DELETE /api/v2/pim/employees` với JSON `{"ids":[empNumber]}`. Mảng `ids` lấy từ ID nội bộ của POST, không phải mã employeeId.
3. Expected: DELETE `200`.
4. GET `/api/v2/pim/employees?employeeId=<mã riêng>&model=default`. Assert `200`, có `data` và danh sách rỗng.
5. Sau khi xác nhận đã xóa, đặt `empNumber = null` để không DELETE lần nữa. Nếu test dừng sớm, cleanup dự phòng kiểm tra employee còn tồn tại và xóa bằng ID đã giữ.

PASS nghĩa là request xóa thành công và list không còn employee mang mã riêng đó. Nếu DELETE trả `200` nhưng list vẫn có employee, assertion FAIL; cần kiểm tra thêm để xác minh lỗi xóa hoặc lỗi filter. Không GET detail bằng ID đã xóa để mặc định chờ `404`: validator local có thể trả `422` cho ID không còn truy cập được.

## Kết quả chạy thật

Bản cuối chạy trên OrangeHRM OS 5.9 local ngày **19/10/2026**, kết thúc **23:35:12 (UTC+7)**. Maven **BUILD SUCCESS**; TestNG **3 tests, 0 failures, 0 errors, 0 skipped**, thời gian suite **13.04 giây**. Compile riêng trước đó cũng **BUILD SUCCESS**.

| Test | Request chính | Status | Relevant response và GET xác minh | Kết quả |
| --- | --- | --- | --- | --- |
| API_05 | POST `/api/v2/pim/employees` | 200 | `empNumber=33`, `employeeId=PCeae48a17`; GET 200 trả đúng `Nguyễn`, `Phát`; cleanup DELETE 200, GET list 200 `data=[]`, `meta.total=0` | PASS |
| API_06 | PUT `/api/v2/pim/employees/34/personal-details` | 200 | `empNumber=34`, `employeeId=PU68972acb`; GET 200 có `firstName="Nguyễn API"`; cleanup DELETE 200, GET list 200 `data=[]`, `meta.total=0` | PASS |
| API_07 | DELETE `/api/v2/pim/employees`, body `{"ids":[35]}` | 200 | DELETE `data=[35]`; GET list theo `PD7a0b9978` trả 200, `data=[]`, `meta.total=0` | PASS |

Các ID/mã trong bảng là bằng chứng của lần chạy, không hard-code trong source. Ba employee của lần chạy cuối đều đã được xác nhận không còn trong list. Test không thêm thời gian chờ; browser chỉ phục vụ cơ chế lấy token hiện tại của Base.

PASS chỉ xác nhận các trường hợp Create/Update/Delete này. Khi FAIL, không đổi expected chỉ để PASS; đọc request, status, response và bước bị lỗi. Nếu setup/token không thành công thì chưa đủ căn cứ kết luận chức năng đang kiểm tra có bug.

## Tài liệu đối chiếu

- [Create an Employee](https://api-starter-orangehrm.readme.io/reference/create-an-employee): POST, các field và expected 200.
- [Update an Employee's Personal Details](https://api-starter-orangehrm.readme.io/reference/update-an-employees-personal-details): PUT personal-details, expected 200.
- [Get an Employee](https://api-starter-orangehrm.readme.io/reference/get-an-employee): GET detail.
- [Delete Employees](https://api-starter-orangehrm.readme.io/reference/delete-employees): DELETE collection, body ids, expected 200.
- [List All Employees](https://api-starter-orangehrm.readme.io/reference/list-all-employees): GET list có filter employeeId. Trang tài liệu ghi type integer cho filter này; source local kiểm tra string/độ dài và DAO so sánh chính xác mã employeeId. Các mã chuỗi trong bảng đã được chạy thành công trên local 5.9.
