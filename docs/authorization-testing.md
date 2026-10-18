# Kiểm thử phân quyền OrangeHRM

Bộ kiểm thử gồm ba ca GET chỉ đọc trong môi trường OrangeHRM local. Ba dòng trong sheet `Authorization` dùng chung một method Java; vẫn kiểm tra đủ quyền Admin, quyền ESS và việc đổi ID nhân viên. Các kết quả cũ bên dưới được ghi rõ theo phiên bản, không thay cho kết quả chạy bản dùng Excel.

## Điều kiện và cách chạy

- Admin dùng `username`, `password` hiện có. ESS dùng `ess.username`, `ess.password`; nhân viên của ESS dùng `ess.empNumber`, đối tượng cần kiểm tra dùng `target.empNumber`. Không ghi mật khẩu, authorization code, access/refresh token vào tài liệu hay báo cáo. Không đưa thông tin xác thực thật vào Git.
- Dữ liệu đã đối chiếu: TriQuan là ESS, `empNumber=7`, không có cấp dưới; HuyBach là ESS, `empNumber=6`, là supervisor của nhân viên 7. TriQuan không được đọc ContactDetails của cấp trên 6. Chiều ngược lại, supervisor 6 có thể có quyền đọc cấp dưới 7 qua vai trò manager; không dùng chiều 6 → 7 làm ca âm tính.
- `ess.empNumber` và `target.empNumber` là ID nội bộ `emp_number`, không phải mã nhân viên hiển thị `employee_id` hay `TestDataShare.IDEMPLOYEE`.
- Lấy token bằng luồng Authorization Code + PKCE trong `OAuthTokenManager` hiện có. Khi username thay đổi, `currentUsername` làm ba giá trị `accessToken`, `refreshToken`, `expiresAt` được reset để tránh dùng token của người trước. Không tạo cơ chế cache mới. Đăng nhập ESS bằng trình duyệt riêng để không dùng phiên browser của Admin.
- Base hiện dùng URL local và API v2 của dự án. Lớp `com.hrm.TestCase.TestSecurity.AuthorizationAPITest` kế thừa `TestBaseAPI`; không thay đổi base hoặc test xác thực chỉ để chạy ba ca này.
- `verifyAccessControls()` chạy một lần trong `@BeforeClass`: lấy token ESS riêng và kiểm tra token khác Admin; ESS đọc hồ sơ mình phải trả `200` và có `data`; own ID phải khác target ID; Admin đọc target phải trả `200` và có `data`; Admin đọc users phải trả `200` và có danh sách không rỗng. Tất cả đối chứng chạy trước các dòng Excel. Nếu setup thất bại, các test bị SKIP do configuration failure; đó không phải PASS và chưa đủ bằng chứng đánh giá phân quyền.
- Chạy tuần tự bằng `testng_authorization.xml`, có `TestListener` và không có `RetryListener`. Các dòng Excel không phụ thuộc kết quả hoặc thứ tự chạy của nhau; chúng dùng các đối chứng đã hoàn tất trong setup.
- Method `userShouldHaveCorrectAccess(testId, role, endpoint, expectedStatuses)` gửi Rest Assured trực tiếp theo Arrange → Act → Assert. Log4j ghi từng bước trước request và trước assertion: Test ID, loại kiểm tra, role, endpoint, expected, actual status và tóm tắt response. Không in token hoặc toàn bộ hồ sơ liên hệ.

```powershell
mvn -DskipTests test-compile
mvn -o "-Dtestng.suite=testng_authorization.xml" test
```

## Dữ liệu Excel và cách code chạy

File `src/test/resources/data/Security-TestData.xlsx`, sheet `Authorization`:

| testId | role | endpoint | expectedStatuses |
| --- | --- | --- | --- |
| AUTHZ_01 | ADMIN | `/api/v2/admin/users` | `200` |
| AUTHZ_02 | ESS | `/api/v2/admin/users` | `403` |
| AUTHZ_03 | ESS | `/api/v2/pim/employee/{empNumber}/contact-details` | `403,422` |

`@DataProvider(name = "authorizationData")` dùng `ExelReader` hiện có để đọc mỗi dòng thành bốn chuỗi. TestNG gọi method test ba lần; mỗi lần nhận một dòng. Sheet `Injection` được giữ nguyên. Excel không lưu mật khẩu, token, URL máy chủ hay ID nhân viên.

Trong phần Arrange, code kiểm tra role và endpoint thuộc những giá trị đã xác minh, chọn token Admin hoặc ESS, rồi thay `{empNumber}` bằng `target.empNumber` từ config. Phần Act dùng `given().auth().oauth2(...)` để gửi bearer token và `when().get(...)` để gọi endpoint; tắt redirect để không nhầm trang đăng nhập với API thành công.

Phần Assert kiểm tra status nằm trong cột `expectedStatuses`, response là JSON và dữ liệu có đúng quyền hay không. Java vẫn giữ yêu cầu riêng: Admin users phải `200`; ESS users phải `403`; ESS contact-details chỉ `403` hoặc `422` đúng lỗi `empNumber`. Vì vậy sửa Excel thành một status bất kỳ không thể làm một hành vi vượt quyền trở thành PASS. Excel giúp thay dữ liệu dễ đọc, còn quy tắc bảo mật vẫn nằm trong Java.

## AUTHZ_01

| Mục | Nội dung |
| --- | --- |
| 1. Test ID | AUTHZ_01 |
| 2. Tên | Admin truy cập được API danh sách người dùng; dòng AUTHZ_01 chạy bằng `userShouldHaveCorrectAccess`. |
| 3. Category | Đối chứng phân quyền theo vai trò. |
| 4. Mục đích | Chứng minh endpoint hoạt động và token Admin hợp lệ trước khi đánh giá ca truy cập trái phép. |
| 5. Role | Admin đã xác thực bằng OAuth. |
| 6. Request | `GET http://localhost:8080/web/index.php/api/v2/admin/users`, gửi bearer token Admin và nhận JSON. |
| 7. Tại sao đổi ID/role? | Không đổi; giữ Admin làm đối chứng. AUTHZ_02 giữ endpoint và đổi người gọi sang ESS. |
| 8. Expected | HTTP `200`, JSON có `data` là danh sách người dùng không rỗng. |
| 9. Assertion | Kiểm tra status `200`, Content-Type JSON và `data` là list không rỗng; không kiểm tra schema từng bản ghi. |
| 10. PASS | Admin nhận danh sách hợp lệ, không phải HTML trang đăng nhập hoặc JSON lỗi. |
| 11. FAIL | Không đạt đối chứng: đăng nhập/token/endpoint/môi trường hoặc response không đúng hợp đồng. Chưa được dùng kết quả đó để kết luận các ca âm tính an toàn. |
| 12. Có chắc vulnerability? | Không. Đây là hành vi được phép; FAIL của đối chứng cũng không chứng minh lỗ hổng phân quyền. |

## AUTHZ_02

| Mục | Nội dung |
| --- | --- |
| 1. Test ID | AUTHZ_02 |
| 2. Tên | ESS không truy cập được API quản trị người dùng; dòng AUTHZ_02 chạy bằng `userShouldHaveCorrectAccess`. |
| 3. Category | Vượt quyền theo vai trò: từ ESS lên chức năng Admin. |
| 4. Mục đích | Kiểm tra server kiểm soát quyền của endpoint, kể cả khi người dùng gọi thẳng API ngoài giao diện. |
| 5. Role | ESS hợp lệ, TriQuan/nhân viên 7; token và phiên đăng nhập độc lập với Admin. |
| 6. Request | Giữ `GET http://localhost:8080/web/index.php/api/v2/admin/users` như AUTHZ_01; thay bearer token Admin bằng token ESS. |
| 7. Tại sao đổi ID/role? | Chỉ đổi vai trò người gọi. Nếu vẫn đọc được dữ liệu quản trị, quyền của endpoint chưa ngăn đúng tài khoản ESS. |
| 8. Expected | HTTP `403`, JSON lỗi, không có `data` danh sách người dùng. `error.message` được log để đọc kết quả, không dùng làm assertion. |
| 9. Assertion | Sau đối chứng Admin users và kiểm tra phiên ESS hợp lệ trong setup, yêu cầu chính xác status `403`, JSON và không có `data` được bảo vệ. Không chấp nhận một mã `4xx` bất kỳ hoặc token hết hạn làm bằng chứng đạt. |
| 10. PASS | ESS đang xác thực hợp lệ nhận đúng phản hồi từ chối `403` và không đọc được dữ liệu quản trị. |
| 11. FAIL | ESS nhận dữ liệu quản trị hoặc response khác hợp đồng mong đợi. Lỗi setup, `404`, `5xx`, HTML hoặc token không hợp lệ phải được phân loại riêng, không tự gọi là vượt quyền. |
| 12. Có chắc vulnerability? | Chỉ khi xác nhận đúng tài khoản ESS, token hợp lệ, đúng endpoint và response thực tế cấp dữ liệu/quyền quản trị. Một assertion FAIL đơn lẻ chưa đủ. |

## AUTHZ_03

| Mục | Nội dung |
| --- | --- |
| 1. Test ID | AUTHZ_03 |
| 2. Tên | Nhân viên không đọc được thông tin riêng của nhân viên khác; dòng AUTHZ_03 chạy bằng `userShouldHaveCorrectAccess`. |
| 3. Category | Phân quyền đối tượng / IDOR: đổi ID tài nguyên trong request. |
| 4. Mục đích | Kiểm tra server đối chiếu `empNumber` được yêu cầu với quyền của người đang gọi API. |
| 5. Role | ESS TriQuan, nhân viên 7, không có cấp dưới. Đối tượng 6 là HuyBach, tài khoản ESS và supervisor của nhân viên 7. |
| 6. Request | Trong setup, ESS gọi `GET /web/index.php/api/v2/pim/employee/7/contact-details` và Admin gọi `GET /web/index.php/api/v2/pim/employee/6/contact-details` để xác nhận cả hai đối chứng. Khi chạy dòng AUTHZ_03, ESS giữ token của mình và gọi ID 6. Đường dẫn dùng `employee` số ít; ID lấy từ config. |
| 7. Tại sao đổi ID/role? | Chỉ đổi ID từ chính mình (7) sang người khác (6), không đổi token hay role. Đối tượng khác đã tồn tại và không thuộc phạm vi ESS được phép đọc. |
| 8. Expected | ESS đọc ID 7 và Admin đọc ID 6 đều trả `200` với JSON ContactDetails. ESS đọc ID 6 bị từ chối: `403`, hoặc `422` InvalidParameter với `error.data.invalidParamKeys` chứa `empNumber`; không có `data` ContactDetails riêng tư. |
| 9. Assertion | Các đối chứng trong setup phải thành công; own ID khác target ID. Response âm tính phải là JSON không có `data` được bảo vệ. Nếu status là `422`, message phải là `Invalid Parameter` và `invalidParamKeys` có `empNumber`; trường hợp còn lại bắt buộc `403`. Không chấp nhận `422` do lỗi tham số khác. |
| 10. PASS | ESS đọc được ContactDetails của mình nhưng không đọc được ContactDetails của nhân viên 6. |
| 11. FAIL | Request đổi ID trả dữ liệu ContactDetails của người khác hoặc response không đúng hợp đồng. Nếu đối chứng ESS own-profile hoặc Admin target thất bại, target sai/không tồn tại hay quyền thực tế khác tiền điều kiện thì cần sửa setup trước khi đánh giá IDOR. |
| 12. Có chắc vulnerability? | Chỉ kết luận IDOR khi chứng minh cùng token ESS hợp lệ đọc được tài nguyên riêng của nhân viên 6 dù không có quyền. `200` HTML, lỗi server hoặc đối chứng hỏng không phải chứng cứ đó. |

## Bằng chứng và giới hạn

Source local có hai lớp từ chối: `ApiAuthorizationSubscriber` trả `403` khi thiếu quyền API; `InAccessibleEmpNumbers` kiểm tra phạm vi nhân viên và có thể tạo lỗi `InvalidParamException`, được chuyển thành `422` với `invalidParamKeys=['empNumber']`. Vì vậy AUTHZ_03 kiểm tra cả mã HTTP và nội dung lỗi, thay vì mặc định mọi IDOR phải trả `403` hoặc nới assertion để PASS.

Ghi Test ID, method/path, role, own/target ID, status HTTP, loại nội dung, kết quả assertion và đối chứng liên quan. Chỉ lưu bản tóm tắt đã loại dữ liệu nhạy cảm; không log token, cookie, authorization code hoặc body chứa thông tin liên hệ thật. Mỗi PASS chỉ xác nhận tình huống đã kiểm tra trên bản triển khai đó.

## Kết quả lịch sử ngày 10/10/2026 — phiên bản ba method

Đây là bằng chứng của lần chạy bản code trước, không thay cho việc kiểm tra bản refactor ngày 17/10/2026.

Compilation: **SUCCESS**. Suite thực tế: **3 PASS, 0 FAIL, 0 SKIP**, chạy bằng:

```powershell
mvn -o "-Dtestng.suite=testng_authorization.xml" test
```

Allure đã tạo ba kết quả `passed` trong `target/allure-results` theo cấu hình hiện có.

| Ca / đối chứng | Kết quả thực tế |
| --- | --- |
| AUTHZ_01: Admin → users | `200`, PASS. |
| AUTHZ_02: ESS → users Admin | `403 Unauthorized`, không có `data`, PASS. |
| AUTHZ_03: ESS 7 → ContactDetails 6 | `422 InvalidParameter`, `invalidParamKeys` chứa `empNumber`, không có `data`, PASS. |
| ESS 7 → ContactDetails 7 | `200`, đối chứng thành công. |
| Admin → ContactDetails 6 | `200`, đối chứng target thành công. |

Lần chạy này chưa phát hiện vượt quyền trong ba tình huống trên; không khẳng định toàn bộ ứng dụng không có lỗ hổng.

## Kết quả lịch sử ngày 17/10/2026 — phiên bản ba method

Ba method mới là `adminCanViewUsers`, `essCannotViewUsers` và `essCannotViewOtherEmployee`. Đối chứng ESS chỉ chạy một lần trong setup; đối chứng Admin cho target vẫn nằm trong AUTHZ_03. Các assertion về status, JSON, không trả dữ liệu và lỗi `empNumber` được giữ. `@AfterClass(alwaysRun = true)` đóng cả hai browser.

Đã compile và chạy lại thật ngày **17/10/2026, 10:28:25 (UTC+07)**:

```powershell
mvn -o -DskipTests test-compile
mvn -o "-Dtestng.suite=testng_authorization.xml" test
```

Compile: **BUILD SUCCESS**. Chạy test: **BUILD SUCCESS, 3 PASS, 0 FAIL, 0 ERROR, 0 SKIP**.

| Ca / đối chứng | Kết quả thực tế của bản refactor |
| --- | --- |
| Setup: ESS 7 → hồ sơ mình | `200`, có `data`; token ESS hoạt động. |
| AUTHZ_01: Admin → users | `200`, danh sách có 3 users, PASS. |
| AUTHZ_02: ESS → users Admin | `403`, `error.message=Unauthorized`, không có `data`, PASS. |
| AUTHZ_03: Admin → ContactDetails 6 | `200`, có `data`; đối chứng target thành công. |
| AUTHZ_03: ESS 7 → ContactDetails 6 | `422`, `error.message=Invalid Parameter`, `invalidParamKeys=[empNumber]`, không có `data`, PASS. |

Các assertion được giữ đúng như bản xem trước đã duyệt; không đổi expected để làm test PASS. Kết quả chỉ xác nhận ba tình huống GET đã kiểm tra, không chứng minh toàn bộ ứng dụng không có lỗ hổng.

**AUTHZ_04 chưa triển khai.** Thao tác PUT ContactDetails ghi toàn bộ 11 trường; cần xác minh việc lưu và khôi phục dữ liệu trước khi thêm ca ghi trái phép. Ba ca trên không sửa ContactDetails hay bản ghi người dùng/nhân viên.

## Phiên bản dùng Excel

Phiên bản này dùng `authorizationData`, `verifyAccessControls()` và một method `userShouldHaveCorrectAccess(...)` thay cho ba method test cũ. `@AfterClass(alwaysRun = true)` vẫn đóng cả hai browser bằng `try/finally`.

Kết quả của phiên bản ba method bên trên không thay cho kết quả bản Excel bên dưới. Nếu FAIL thì giữ expected để tìm nguyên nhân, không sửa assertion chỉ để PASS.

Đã compile thành công và chạy thật bản Excel ngày **17/10/2026**, lần xác minh cuối hoàn tất lúc **11:31:27 UTC+07:00**, bằng `mvn -o "-Dtestng.suite=testng_authorization.xml" test`: **BUILD SUCCESS; 3 PASS, 0 FAIL, 0 ERROR, 0 SKIP**.

| Test ID | Request thực tế | HTTP | Relevant response | Kết quả |
| --- | --- | --- | --- | --- |
| AUTHZ_01 | Admin GET `/api/v2/admin/users` | `200` | Danh sách có 3 users | PASS |
| AUTHZ_02 | ESS GET `/api/v2/admin/users` | `403` | `error.message=Unauthorized`, không có `data` | PASS |
| AUTHZ_03 | ESS GET `/api/v2/pim/employee/6/contact-details` | `422` | `Invalid Parameter`, `invalidParamKeys=[empNumber]`, không có `data` | PASS |

Các đối chứng trước ba dòng đều đạt: ESS đọc employee của mình (7) trả `200/data`; Admin đọc target (6) trả `200/data`; Admin đọc users trả `200` và 3 users. ID trong request được lấy từ config khi chạy, không ghi cứng trong test hoặc Excel.

Surefire ghi đúng ba bộ parameter của sheet `Authorization`. Allure raw results có ba kết quả `passed` và các log/step; chưa tạo báo cáo HTML trong lần này. Sheet `Injection` giữ nguyên giá trị, công thức và hiển thị sau khi lưu workbook.

`422` của AUTHZ_03 được chấp nhận theo behavior local đã xác minh và đủ các assertion về lỗi `empNumber`/không có dữ liệu. AUTHZ_02 vẫn bắt buộc `403`. PASS chỉ xác nhận ba tình huống đã thử, chưa chứng minh toàn bộ ứng dụng không có Broken Access Control hoặc IDOR.
