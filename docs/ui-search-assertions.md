# Login và Search: assertion OHR1, OHR4, OHR15, OHR23, OHR27

> Cách chạy hiện tại (26/10/2026): `mvn test "-Dmodule=Login,Admin,PIM,Recruitment"` qua `testng.xml`; lệnh này chạy toàn bộ module đã chọn, gồm cả issue. Các tên XML/lệnh riêng và số liệu bên dưới là lịch sử trước khi gộp. Xem [hướng dẫn module](test-execution.md).

Ngày cập nhật: 26/10/2026. Môi trường: OrangeHRM OS 5.9 local.

## Code đang kiểm tra gì?

| OHR | Assertion sau thao tác |
|---|---|
| OHR1 | Sau login phải hiển thị đúng tiêu đề Dashboard. |
| OHR4 | Có đúng một dòng chứa username riêng vừa tạo, role, employee và status đã nhập. |
| OHR15 | Có Location phù hợp name/city/country, hoặc hiển thị No Records Found theo expectedFound. |
| OHR23 | Có employee phù hợp tên/ID/employment status/supervisor/job title đã nhập, hoặc No Records Found. |
| OHR27 | Có Vacancy phù hợp job/vacancy/manager/status đã nhập, hoặc No Records Found. |

Test gọi POM, POM dùng lại `componentLocator.checkTable` và `checkSearch`.
Ca âm tính dùng hàm ngắn `checkNoRecordFound` trong cùng componentLocator để kiểm tra đúng thông báo.
Không lấy việc `checkTable` trả false làm bằng chứng bảng rỗng: false cũng có thể nghĩa là bảng có dữ liệu sai.

`checkTable` chuẩn hóa xuống dòng thành khoảng trắng để so tên đầy đủ nằm ở nhiều ô.
User search đã bỏ locator chứa tên cố định và kiểm tra đúng `TestDataShare.USERNAME` được dùng trong request UI.

`clickSearch` chỉ chờ loading kết thúc, tối đa 5 giây; đủ điều kiện thì đi tiếp.
Trong lúc kiểm tra loading, implicit wait tạm về 0 và được khôi phục bằng finally.
Không thêm Thread.sleep hay log trong vòng polling.

## expectedFound trong Excel

File: `src/test/resources/data/Datadriven-OrangeHrm.xlsx`.
Chỉ thêm một cột cuối tại ba sheet Search Location, Employee và Vacancy.
Giữ nguyên input cũ, các sheet khác, định dạng và kiểu text của employee ID `003`.

- `TRUE`: phải tìm thấy ít nhất một dòng khớp các giá trị mà POM đối chiếu.
- `FALSE`: phải nhìn thấy thông báo **No Records Found**.
- Giá trị khác true/false khiến testcase FAIL ngay để báo dữ liệu Excel không hợp lệ.

| Sheet / dòng Excel | Input nhận diện | expectedFound |
|---|---|---|
| Search Location / 2 | Country=Canada | FALSE |
| Search Location / 3 | Đống Đa, Hà Nội, Viet Nam | TRUE |
| Search Location / 4 | Name=Đống Đa | TRUE |
| Employee / 2 | Nguyễn Huy Bách, Current and Past Employees | TRUE |
| Employee / 3 | ID=003, Current Employees Only | FALSE |
| Employee / 4 | Full Time Contract, Current Employees Only | TRUE |
| Employee / 5 | Full Time Contract, Huy Deliver, Devops, OrangeHRM | TRUE |
| Vacancy / 2 | Job Title=Devops | TRUE |
| Vacancy / 3 | Vacancy=Tester | TRUE |
| Vacancy / 4 | Nguyễn Bách, Closed | FALSE |
| Vacancy / 5 | Sales IT, Sales IT, Nguyễn Quân, Active | TRUE |

**Căn cứ:** baseline dữ liệu UI được đọc bằng Selenium trước lần chạy ngày 26/10/2026.
Danh sách Locations hiện chỉ có Viet Nam; các vacancy hiện tại đều Active.
Employee hiện tại có mã `0003`, không phải `003`; phép lọc UI bằng `003` đã trả bảng rỗng.
Phép lọc kết hợp với Sub Unit cha OrangeHRM đã trả employee thuộc Administration.
Đây là dữ liệu đối chứng của môi trường local, chưa phải một đặc tả nghiệp vụ được chủ sản phẩm phê duyệt.
Nếu dataset đổi, cần xác minh lại tiền điều kiện và nguồn expected trước khi chỉnh Excel.

## Giới hạn cần hiểu khi bảo vệ

`checkTable` và `checkSearch` hiện kiểm tra tồn tại một dòng khớp, chưa kiểm tra toàn bộ các dòng và phân trang.
OHR4 bổ sung số dòng bằng 1 vì username được tạo riêng cho lần chạy.
OHR23 không so chữ Include với nội dung hàng vì Include không phải cột dữ liệu.
Không bắt Sub Unit hiển thị bằng tên đơn vị cha: chọn OrangeHRM có thể trả nhân viên ở Administration.
Assertion hiện chưa chứng minh riêng tính đúng đắn của phân cấp Sub Unit hoặc trạng thái terminated theo Include.

## Lệnh và kết quả lịch sử

```powershell
mvn -o -DskipTests test-compile
mvn -o "-Dtestng.suite=testng_search_assertions.xml" test
```

Suite riêng chỉ gồm Login thành công và bốn class Search, dùng TestListener hiện có, không dùng retry.
Hai nhóm User/Location chạy Add trước và Delete sau để đáp ứng dependency/cleanup hiện có.
PIM và Vacancy chỉ đọc dữ liệu đang có.

Lần chạy 26/10/2026 hoàn tất lúc **22:01:10 UTC+7**: **BUILD SUCCESS**.
TestNG: **17 PASS, 0 FAIL, 0 ERROR, 0 SKIP**; thời gian suite **89,30 giây**.

| Nhóm | Lượt chạy | Kết quả |
|---|---:|---|
| OHR1 Login | 1 | PASS |
| OHR4 User Search | 1 | PASS |
| OHR15 Location Search | 3 | PASS |
| OHR23 Employee Search | 4 | PASS |
| OHR27 Vacancy Search | 4 | PASS |
| Add/Delete User và Location | 4 | PASS |

Tổng 13 lượt Login/Search và 4 lượt setup/cleanup. Không phải 17 testcase mới.
Hai method loginFail/resetPassword không được chọn trong suite này; XML có thể ghi ignored=2, không phải hai lượt chạy bị SKIP.
Đọc lại bảng User và Location sau suite xác nhận không còn hai fixture mang hậu tố `8583ad1c` của lần chạy này.
Báo cáo thực tế: `target/surefire-reports/testng-results.xml`, `target/surefire-reports/index.html`.
Maven console có thể lỗi dấu tiếng Việt; đọc `logfile.log` bằng UTF-8 để xem log gốc.
