# Injection API dùng dữ liệu Excel

Ba input được lưu trong Excel và chạy qua cùng một method Java. Các request chỉ GET để kiểm tra bộ lọc username; không tạo, sửa hoặc xóa dữ liệu.

## File và cấu trúc

- Class: `com.hrm.TestCase.TestSecurity.InjectionAPITest`, kế thừa `TestBaseAPI`.
- Suite: `testng_injection.xml`, chỉ chạy Injection.
- Excel: `src/test/resources/data/Security-TestData.xlsx`, sheet `Injection`.
- `@DataProvider(name = "injectionData")` dùng `ExelReader.getDataFromExel(...)` hiện có.
- Một method: `sqlLikeInputShouldBeHandledSafely(String testId, String payload, String expectedStatuses)`.
- Mỗi dòng dữ liệu là một lần chạy riêng: ba dòng vẫn tạo ba kết quả TestNG, không gộp thành một kết quả.

| testId | payload | expectedStatuses |
| --- | --- | --- |
| INJ_01 | `'` | `200,400,422` |
| INJ_02 | `' OR '1'='1` | `200,400,422` |
| INJ_03 | `" OR "1"="1` | `200,400,422` |

`expectedStatuses` là chuỗi text gồm các mã có thể chấp nhận, không phải chỉ cần trúng status là PASS. Các kiểm tra body vẫn phải đạt.

## Request và authentication

Endpoint đã xác minh: `GET /api/v2/admin/users`, query parameter thật là `username`. Base URL và base path lấy từ setup hiện có; trên môi trường local, URL đầy đủ là `http://localhost:8080/web/index.php/api/v2/admin/users`.

Rest Assured dùng `given().auth().oauth2(token)` để gửi bearer token Admin do `OAuthTokenManager` lấy động. `queryParam("username", payload)` lấy payload từ dòng Excel và tự mã hóa khi gửi. Không đặt mật khẩu hoặc token trong Excel, không in chúng ra log.

Source `UserDao` đã được kiểm tra: lọc đúng toàn bộ username bằng tham số bind, không tìm một phần tên. Vì vậy, khi API trả `200`, mỗi `data.userName` phải khớp nguyên payload, bỏ qua hoa/thường.

## Code chạy theo thứ tự nào?

1. `@BeforeClass verifyUsernameFilterWorks` gọi API bằng username Admin lấy từ config. Kết quả phải là `200`, JSON có users và các username đều khớp input. Bước này chứng minh token, endpoint và bộ lọc hoạt động.
2. Setup gửi thêm `username=SECURITY_INJ_NO_MATCH`. Baseline phải là `200`, JSON, `data=[]`, `meta.total=0`. Tổng baseline được lưu ở `baselineTotal` và phải bằng `0`. Baseline chạy một lần trước cả ba input.
3. DataProvider đọc ba cột của mỗi dòng Excel và truyền vào method test.
4. Test log Test ID, endpoint, payload và từng bước; gửi GET trực tiếp bằng Rest Assured rồi log status thực tế.
5. Test kiểm tra status từ Excel và dùng helper ngắn `assertNoDatabaseError` kiểm tra lỗi nội bộ.
6. Với `200`, test kiểm tra dữ liệu, log count/total so với baseline và kiểm tra từng username khớp nguyên payload. Với `400`/`422`, phải đúng lỗi validation và không trả dữ liệu users.
7. Kết thúc class thì đóng trình duyệt dùng để lấy token.

Setup thất bại có thể khiến ba lần chạy bị SKIP. Đây là lỗi tiền điều kiện, không được báo thành ba testcase PASS.

## Mỗi input kiểm tra gì?

| Test ID | Mục đích | Cách hiểu đơn giản |
| --- | --- | --- |
| INJ_01 | Một dấu nháy đơn không gây lộ lỗi database hoặc làm sai bộ lọc. | Backend phải xử lý dấu nháy như dữ liệu hoặc từ chối input an toàn. |
| INJ_02 | Chuỗi giống điều kiện SQL không khiến API trả users ngoài bộ lọc. | So với baseline không tìm thấy user, payload không được biến thành điều kiện lấy mọi user. |
| INJ_03 | Thử biến thể dùng dấu nháy kép với cùng tiêu chí. | Đổi loại dấu nháy cũng không được làm lộ lỗi hoặc bypass bộ lọc. |

## Assertion và cách đọc PASS/FAIL

Các lần chạy đều kiểm tra JSON, không `500`, không lộ các dấu hiệu như SQL syntax, SQLSTATE, database exception, stack trace hoặc câu query SQL rõ ràng.

| Response quan sát | Kết quả cần kiểm tra |
| --- | --- |
| `200`, `data=[]`, `meta.total=0`, không lộ lỗi | Có thể PASS: input được xử lý như text và không tìm thấy user. |
| `200`, có users | Chỉ PASS khi từng `userName` khớp nguyên payload và các kiểm tra khác đều đạt. User không khớp là dấu hiệu filter sai cần điều tra. |
| `400`/`422` | Chỉ chấp nhận khi JSON có `error.message=Invalid Parameter` và không có `data` users; vẫn phải qua kiểm tra lộ lỗi. |
| `500` hoặc dấu hiệu SQL/stack trace | FAIL; có thể là vấn đề xử lý input hoặc lộ thông tin, cần kiểm tra nguyên nhân. |
| `401`/`403`, `404` hoặc HTML | FAIL; kiểm tra token, quyền, endpoint hoặc môi trường, không coi đây là bằng chứng xử lý injection an toàn. |

Nếu số kết quả tăng so với baseline, log cảnh báo để xem lại. Count tăng chưa đủ kết luận SQL Injection vì có thể tồn tại username trùng nguyên payload. FAIL cũng chưa tự chứng minh có lỗ hổng SQL Injection; cần xem assertion, response và kiểm tra thủ công. PASS chỉ cho biết chưa thấy các dấu hiệu đã kiểm tra trong input đó.

## Thay đổi dữ liệu Excel

Để thêm input cùng mục đích, thêm một dòng liên tiếp dưới dữ liệu hiện có với đủ ba cột. Không cần copy thêm method test. Payload dùng đúng dấu nháy ASCII như bảng trên, không đổi thành dấu nháy cong.

Giữ header ở hàng đầu tiên và tên sheet `Injection`. Không thêm dòng tiêu đề, dòng ghi chú, dòng trống xen giữa hoặc dòng thừa phía dưới; reader đọc đến dòng cuối của sheet. Lưu cả ba cột dưới dạng text, không dùng công thức. Đặc biệt, dấu `'` phải thực sự là giá trị payload, không chỉ là ký hiệu Excel dùng để nhập text.

Chỉ thay `expectedStatuses` khi có lý do từ hợp đồng API hoặc behavior đã xác minh. Không thêm `500`, `401` hay status đang FAIL chỉ để làm test PASS.

## Cách chạy

```powershell
mvn -o -DskipTests test-compile
mvn -o "-Dtestng.suite=testng_injection.xml" test
```

Đọc log theo Test ID và bước thực hiện. Surefire ghi ba lần chạy cùng method với ba bộ parameter khác nhau; các giá trị Test ID giúp phân biệt INJ_01, INJ_02 và INJ_03.

## Kết quả bản Excel ngày 17/10/2026

Compile bằng `mvn -o -DskipTests test-compile`: **BUILD SUCCESS**. Suite Injection hoàn tất lúc **11:15:22 UTC+07:00**: **3 PASS, 0 FAIL, 0 ERROR, 0 SKIP**.

| Test ID | HTTP thực tế | Response quan trọng | Kết quả |
| --- | --- | --- | --- |
| INJ_01 | `200` | `data=[]`, `meta.total=0` | PASS |
| INJ_02 | `200` | `data=[]`, `meta.total=0` | PASS |
| INJ_03 | `200` | `data=[]`, `meta.total=0` | PASS |

Positive control trả `200`, username khớp; baseline trả `200`, không có kết quả. Không xuất hiện các dấu hiệu SQL/DB error mà code kiểm tra. Surefire xác nhận ba bộ parameter đúng nguyên văn từ Excel, kể cả các dấu nháy. Allure raw results có ba kết quả `passed`, mỗi kết quả có parameter và tám log/step; chưa tạo báo cáo HTML trong lần này.

## Kết quả lịch sử ngày 12/10/2026

Đây là kết quả của bản cũ có ba method riêng, trước khi chuyển sang Excel; không phải bằng chứng bản Excel đã chạy thành công.

Compile: **BUILD SUCCESS**. Suite hoàn tất lúc **22:15:37 UTC+07:00**: **BUILD SUCCESS; 3 PASS, 0 FAIL, 0 ERROR, 0 SKIP**. Positive control trả `200` và username đúng; baseline trả `200`, `data=[]`, `meta.total=0`. Cả ba payload đều trả `200`, `data=[]`, `meta.total=0`, không xuất hiện các dấu hiệu SQL/DB error được kiểm tra. Kết quả không chứng minh OrangeHRM hoàn toàn không có SQL Injection.
