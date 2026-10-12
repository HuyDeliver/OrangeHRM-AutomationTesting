# SQL Injection-like testing cho OrangeHRM local

Ba test dùng GET chỉ đọc, không tạo hoặc sửa dữ liệu. Chúng gửi chuỗi giống SQL vào bộ lọc để quan sát cách API xử lý input; không dùng payload phá hủy.

## Endpoint và dữ liệu đã xác minh

- Endpoint: `GET http://localhost:8080/web/index.php/api/v2/admin/users`.
- Parameter thật: `username`. Source `UserAPI` đọc parameter này; `UserDao` so sánh **đúng toàn bộ username** và truyền giá trị bằng `setParameter`, không phải tìm một phần tên. Assertion so sánh toàn bộ giá trị và bỏ qua khác biệt chữ hoa/thường.
- Response thành công có `data` là mảng users và `meta.total` là tổng số kết quả.
- Đối chứng đã gọi ngày 12/10/2026: không lọc trả `200`, 3 users; lọc username Admin từ config trả `200`, 1 user; lọc `SECURITY_INJ_NO_MATCH` trả `200`, `data=[]`, `meta.total=0`.
- Các test kế thừa `TestBaseAPI`, dùng token Admin được lấy bởi `OAuthTokenManager` hiện có. Rest Assured gửi bearer token qua header Authorization; không in giá trị token, password hoặc authorization code ra log.
- Trước ba testcase, `@BeforeClass verifyUsernameFilterWorks` lấy username Admin từ config rồi gọi bộ lọc: phải trả `200`, JSON có users và tất cả `userName` khớp username đó. Đây là tiền điều kiện để chứng minh token/endpoint/filter hoạt động, không phải testcase thứ tư. Setup thất bại thì các test bị SKIP, không được tính là PASS.
- Các URL dưới đây thể hiện query đã mã hóa để đọc được cấu trúc request. Rest Assured tự mã hóa giá trị truyền vào `queryParam`.
- Helper chỉ chấp nhận `400`/`422` khi JSON có `error.message` bằng `Invalid Parameter` (bỏ qua hoa/thường) và không có dữ liệu users ở `data`. Không cần `invalidParamKeys`; không coi một mã `4xx` bất kỳ là validation an toàn.

## Cách chạy

```powershell
mvn -DskipTests test-compile
mvn -o "-Dtestng.suite=testng_injection.xml" test
```

Class: `com.hrm.TestCase.TestAPI.InjectionAPITest`. Chỉ helper ngắn `assertNoDatabaseError` được dùng chung trong ba ca.

## INJ_01

**Test ID:** INJ_01  
**Tên test:** Single Quote Input — `singleQuoteShouldNotCauseDatabaseError`.

| Mục | Giải thích |
| --- | --- |
| 1. Input là gì? | Một dấu nháy đơn: `'`. Dấu này thường xuất hiện trong chuỗi SQL nên có thể làm lộ lỗi nếu backend ghép input sai cách. |
| 2. Query parameter nào? | `username` của API danh sách người dùng. |
| 3. HTTP request gần đúng? | `GET /web/index.php/api/v2/admin/users?username=%27`, kèm bearer token Admin hiện có. |
| 4. Server trả status gì? | Thực tế ngày 12/10/2026: `200`. Không mặc định SQL-like input phải trả `400`; `200` có thể hợp lệ khi API xem payload như text. |
| 5. Body có gì quan trọng? | Thực tế: `data=[]`, `meta.total=0`. Không tìm thấy các dấu hiệu SQL/DB error, stack trace hoặc query bị lộ mà helper kiểm tra. |
| 6. Assertion kiểm tra gì? | Không `500`; status nằm trong `200`/`400`/`422`; response là JSON; body không chứa các dấu hiệu lỗi SQL/DB rõ ràng. Nếu `400`/`422`, yêu cầu `error.message=Invalid Parameter` và không có `data` users. Chữ `error` đơn lẻ không làm test FAIL. |
| 7. Vì sao có ý nghĩa security? | Input bất thường không được làm backend lỗi hoặc tiết lộ chi tiết database cho người gọi. |
| 8. PASS nghĩa là gì? | Request được xử lý hoặc từ chối mà không xuất hiện lỗi nội bộ đã kiểm tra. Chỉ chứng minh tình huống này, chưa chứng minh mọi input đều an toàn. |
| 9. FAIL nghĩa là gì? | Có response khác hợp đồng hoặc dấu hiệu lộ lỗi. Cần phân biệt lỗi môi trường/token/endpoint với lỗi xử lý input của server. |
| 10. Chắc chắn SQL Injection chưa? | Chưa. `500` kèm SQL error là dấu hiệu đáng điều tra; vẫn cần xác minh nguyên nhân và khả năng input thay đổi câu query. |

## INJ_02

**Test ID:** INJ_02  
**Tên test:** Boolean SQL Injection-like Input — `booleanPayloadShouldNotBypassUsernameFilter`.

| Mục | Giải thích |
| --- | --- |
| 1. Input là gì? | Baseline là `SECURITY_INJ_NO_MATCH`; payload là `' OR '1'='1`. Baseline đã được kiểm tra là không trùng username hiện có. |
| 2. Query parameter nào? | Cả hai request đều dùng `username`; giữ nguyên endpoint và token Admin, chỉ thay giá trị parameter. |
| 3. HTTP request gần đúng? | Baseline: `GET /web/index.php/api/v2/admin/users?username=SECURITY_INJ_NO_MATCH`. Injection: cùng đường dẫn với `username=%27%20OR%20%271%27%3D%271`. |
| 4. Server trả status gì? | Thực tế ngày 12/10/2026: baseline `200`, injection `200`. |
| 5. Body có gì quan trọng? | Baseline và injection đều có `data=[]`, `meta.total=0`: data count `0` so với `0`, total `0` so với `0`. Không thấy trả danh sách users bất thường hay dấu hiệu lỗi nội bộ đã kiểm tra. |
| 6. Assertion kiểm tra gì? | Baseline phải thành công và không có kết quả; injection không gây lỗi SQL/DB. Khi `400`/`422`, phải đúng lỗi `Invalid Parameter` và không có `data` users. Khi `200`, các username trả về phải khớp đúng payload. Nếu số kết quả tăng, log hành vi đáng ngờ để điều tra, không dùng việc tăng count làm kết luận SQL Injection. |
| 7. Vì sao có ý nghĩa security? | Một giá trị input không được biến thành điều kiện SQL khiến API bỏ qua bộ lọc và trả các users không khớp. |
| 8. PASS nghĩa là gì? | Chưa thấy bypass bộ lọc hoặc lỗi SQL trong phép so sánh này. Ví dụ an toàn: cả hai trả `data=[]`, hoặc payload bị validation từ chối. |
| 9. FAIL nghĩa là gì? | Có thể là baseline/test data không còn đúng, lỗi response, lộ lỗi SQL hoặc bộ lọc trả users không khớp payload. Phải đọc assertion và response để biết loại lỗi. |
| 10. Chắc chắn SQL Injection chưa? | Chưa. Count tăng chỉ là dấu hiệu. Users không khớp payload là bằng chứng filter có hành vi sai; vẫn cần xác minh nguyên nhân có phải SQL Injection hay lỗi filter khác. |

## INJ_03

**Test ID:** INJ_03  
**Tên test:** Double Quote Variant — `doubleQuotePayloadShouldNotBypassUsernameFilter`.

| Mục | Giải thích |
| --- | --- |
| 1. Input là gì? | Payload `" OR "1"="1`, dùng dấu nháy kép để thử một biến thể của input giống SQL. |
| 2. Query parameter nào? | `username`, cùng endpoint và token Admin như các ca trước. |
| 3. HTTP request gần đúng? | `GET /web/index.php/api/v2/admin/users?username=%22%20OR%20%221%22%3D%221`, kèm bearer token hiện có. |
| 4. Server trả status gì? | Thực tế ngày 12/10/2026: `200`. Assertion vẫn chấp nhận `400`/`422` nếu đó là validation đúng hợp đồng. |
| 5. Body có gì quan trọng? | Thực tế: `data=[]`, `meta.total=0`. Không tìm thấy các dấu hiệu SQL query, SQL/DB exception hoặc stack trace mà helper kiểm tra. |
| 6. Assertion kiểm tra gì? | Dùng cùng helper kiểm tra status/JSON/lỗi nội bộ. Khi `400`/`422`, phải đúng lỗi `Invalid Parameter` và không có `data` users. Khi `200`, từng `userName` phải khớp nguyên chuỗi payload, theo behavior exact match đã xác minh. |
| 7. Vì sao có ý nghĩa security? | Đổi cách dùng dấu nháy không được làm bypass filter hoặc khiến server trả lỗi nội bộ. |
| 8. PASS nghĩa là gì? | Không thấy bypass rõ ràng hoặc lỗi SQL/DB trong biến thể này. Không kết luận mọi biến thể SQL Injection đều đã được kiểm tra. |
| 9. FAIL nghĩa là gì? | Response sai hợp đồng, có dấu hiệu lỗi nội bộ hoặc users không khớp input. Phải kiểm tra log để phân biệt lỗi server với lỗi setup/test data. |
| 10. Chắc chắn SQL Injection chưa? | Chưa. Cần kiểm tra thủ công hoặc source để xác định payload thực sự làm thay đổi query, thay vì chỉ dựa vào mã HTTP hay một assertion FAIL. |

## Cách đọc kết quả

| Kết quả quan sát | Cách hiểu |
| --- | --- |
| `200`, `data=[]`, không lộ lỗi | Có thể là input được xem như text bình thường; không thấy hành vi SQL Injection trong ca đó. |
| `400`/`422`, `error.message=Invalid Parameter`, không có `data` users và không lộ chi tiết nội bộ | Input có thể đã bị từ chối an toàn. Không chấp nhận chỉ dựa vào mã status. |
| `500` kèm SQLSTATE/SQL syntax/database exception | Dấu hiệu lỗi xử lý input hoặc SQL cần điều tra thêm. |
| `200` nhưng users không khớp nguyên payload | Có dấu hiệu bypass filter; cần xác định nguyên nhân trước khi gọi là SQL Injection. |
| `401`/`403` | Kiểm tra token hoặc quyền Admin; không phải bằng chứng payload an toàn. |
| `404` hoặc HTML | Kiểm tra endpoint/môi trường/redirect; không tự gọi là SQL Injection. |

## Kết quả thực thi ngày 12/10/2026

Compile bằng `mvn -o -DskipTests test-compile`: **BUILD SUCCESS**. Chạy riêng suite bằng `mvn -o "-Dtestng.suite=testng_injection.xml" test`, hoàn tất lúc **22:15:37 UTC+07:00**: **BUILD SUCCESS; 3 PASS, 0 FAIL, 0 ERROR, 0 SKIP**.

Surefire ghi đúng ba method Injection trong [TEST-TestSuite.xml](/E:/Studying/GraduateProject/orangehrmtesting/target/surefire-reports/TEST-TestSuite.xml). [Allure raw results](/E:/Studying/GraduateProject/orangehrmtesting/target/allure-results) có ba testcase Injection với status `passed`; đây là dữ liệu kết quả, chưa khẳng định đã tạo báo cáo HTML.

| Test / đối chứng | HTTP thực tế | Body quan trọng / kết quả |
| --- | --- | --- |
| Setup: lọc username Admin từ config | `200` | Các assertion JSON, có users và username đúng đều PASS. |
| INJ_01: dấu nháy đơn | `200` | `data=[]`, `meta.total=0`; PASS. |
| INJ_02: baseline bình thường | `200` | `data=[]`, `meta.total=0`. |
| INJ_02: boolean payload | `200` | `data=[]`, `meta.total=0`; so sánh count `0` với `0`, total `0` với `0`; PASS. |
| INJ_03: biến thể nháy kép | `200` | `data=[]`, `meta.total=0`; PASS. |

Lần chạy này chưa thấy bypass bộ lọc hoặc lộ lỗi SQL/DB với ba input đã thử. Kết quả không chứng minh OrangeHRM hoàn toàn không có SQL Injection hoặc mọi parameter khác đều an toàn.
