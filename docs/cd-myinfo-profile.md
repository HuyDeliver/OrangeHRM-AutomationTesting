# My Info và Profile trên CD 8081

## Phạm vi

| Module | Testcase có sẵn | Lượt chạy |
|---|---|---:|
| My Info | OHR32 Personal Details, OHR33 Contact Details | 2 |
| My Info | OHR34–37 Add/Delete Emergency Contact và Dependent | 4 |
| My Info | OHR42 / Issue #1928: xóa Education | 1 |
| Profile | OHR44–45 / Issue #1923: click avatar và click ngoài menu | 2 |

Không thêm mã testcase mới. Seed và setup/cleanup không tính vào regression PASS.
Các class dùng cùng `testng.xml`; mỗi module chạy trong một Maven invocation.

## Fixture và cleanup

- `CDMyInfoSeedTest` tái sử dụng seed PIM/Recruitment, thêm đúng một Education level
  `AUTO CD Degree` nếu chưa có. ID lấy từ UI, không hard-code.
- `CDMyInfoSetup` tạo employee và login Admin riêng cho mỗi lượt chạy, bằng
  `AddEmployeePage`, `AddUserPage` hiện có và `TestDataShare`. Giữ role của bộ test
  cũ vì ESS không được sửa License Number/Date of Birth. Password sinh động, không ghi log.
- Khi bật `cd.myinfo`, `TestUtil.myInfoUtil()` đăng nhập bằng user tạm đó. Các module
  khác và cách chạy DEV vẫn dùng tài khoản trong config.
- Personal/Contact Details chỉ sửa hồ sơ tạm, đọc lại ID/ngày sinh hoặc địa chỉ/email
  sau refresh. Các test Add/Delete liên hệ/dependent kiểm tra record sau refresh.
- `@AfterTest(alwaysRun=true)` xóa employee tạm và xác nhận không còn trong PIM;
  cleanup kế thừa kiểm tra/xóa username tạm. Không sửa hồ sơ Admin.
- Đây là xóa qua UI OrangeHRM, không phải cam kết xóa vật lý mọi dòng trong database.
  Nếu Issue #1928 giữ lại Education, test vẫn FAIL; cleanup employee không đổi kết quả đó.
- Profile chỉ thao tác menu; không tạo dữ liệu nên không cần seed riêng.

## Chạy local

Thêm các dòng non-secret từ `deploy/myinfo.properties` vào config CD hiện có.
Workflow sẽ tự ghép file này, không cần thêm Secret.

```powershell
.\deploy\seed.ps1 -IncludeMyInfo -RunName myinfo-run1
.\deploy\seed.ps1 -IncludeMyInfo -RunName myinfo-run2
mvn -B -ntp test -Pcd-myinfo "-Dconfig.file=deploy/config.cd.properties" "-Dheadless=true"
.\deploy\myinfo-report.ps1
mvn -B -ntp test -Pcd-profile "-Dconfig.file=deploy/config.cd.properties" "-Dheadless=true"
.\deploy\profile-report.ps1
```

Chọn RunName mới khi chạy seed lại để giữ bằng chứng cũ. Các profile CD này kiểm tra
URL 8081 trước khi mở browser; không chạy cùng nhiều profile trong một lệnh.

## Report và workflow

- My Info: `ci-myinfo.log`, `target/cd-myinfo/surefire`, `target/cd-myinfo/allure-results`.
- Profile: `ci-profile.log`, `target/cd-profile/surefire`, `target/cd-profile/allure-results`.
- Artifact JSON: `target/cd-evidence/myinfo-summary.json`, `profile-summary.json`.
- Seed: `target/cd-seed/<RunName>/myinfo-seed-result.properties`.

Report yêu cầu đủ 7/2 lượt PASS, không có configuration failure; My Info còn yêu cầu
cả hai cleanup PASS. Issue tái hiện vẫn làm module và pipeline FAIL. Các module này
vẫn được chạy khi module khác fail, miễn seed và login smoke đã thành công.

Helper `takeScreenshotResult` được sửa để chờ spinner biến mất bằng locator; không
bắt buộc spinner phải còn tồn tại lúc chụp. Không thêm sleep hoặc tự retry testcase.
`TestBase.tearDown()` dùng `alwaysRun=true` để đóng browser cả khi setup/test lỗi.

## Kết quả ngày 03/11/2026

Seed `myinfo-run1` và `myinfo-run2` đều hoàn tất:

| Fixture key | ID run 1 | ID run 2 | Record sau run 2 | Duplicate |
|---|---:|---:|---:|---|
| AUTO CD Degree | 1 | 1 | 1 | NO |

Các prerequisites PIM/Recruitment được kiểm tra/tái sử dụng, không tạo lại.
DEV 8080 touched: **NO**. Toàn bộ seed/test dùng CD; container ID/running state/mount
của DEV trước/sau không đổi. Không thực hiện checksum database DEV.

| Module | PASS | FAIL | SKIP | Cleanup/configuration |
|---|---:|---:|---:|---|
| My Info, kết thúc 22:20:22 | 6 | 1 | 0 | Hai cleanup PASS, không lỗi config |
| Profile, kết thúc 22:21:12 | 1 | 1 | 0 | Không tạo data, không lỗi config |

- OHR42/#1928: Delete Education báo Error trên UI; sau refresh `stillExists=true`.
  Giữ FAIL, không đổi assertion. Employee/user tạm của lượt chạy đã được xóa qua UI.
- OHR44/#1923: click avatar lần hai vẫn mở menu nên FAIL; OHR45 click ngoài đóng menu PASS.
- My Info lần đầu dùng ESS nên OHR32 gặp field bị khóa: 5 PASS/2 FAIL, cleanup PASS.
  Đã đổi fixture sang Admin tạm để giữ role bộ test cũ, không bỏ field/assertion.
  Bằng chứng lượt đầu nằm ở `target/myinfo-first-run/`.
- Compile, parse XML/YAML và PowerShell report đã kiểm tra. Các report trả exit code
  khác 0 đúng với testcase FAIL. Không dùng retry hoặc expected-failure để đổi kết quả.
- Đây là kết quả **local CD**, chưa xác minh bản workflow mới trên GitHub Actions.

## Mốc bàn giao và phần tiếp theo

My Info/Profile đã hoàn tất integration + chạy thật + report. Chưa đưa code/step
chạy nửa chừng cho các module sau vào workflow:

1. Performance: cần employee có Job Title, quan hệ supervisor và hai KPI đúng thang điểm;
   chuỗi Add/Write/Delete Review phải cùng invocation, tìm/xóa đúng review của lượt chạy.
2. Time: cần cấu hình Monday và Customer/Project/Activity A/B riêng trên CD; giữ timesheet
   Not Submitted, reset giờ về 00:00. Không dùng timesheet DEV đã Submit.
3. Claim: cần Event và Claim nháp riêng trên CD, giữ fixture, chỉ cleanup attachment thử.
4. API/Security: hiện Base API/OAuth còn trỏ DEV 8080. Phải chuyển sang config, đăng ký
   OAuth client CD thực tế, lấy token động và chuẩn bị ESS/own/target ID thực tế trước khi bật.
5. Login hiện chỉ có smoke OHR1 trên CD; OHR2 và OHR3 chưa được bật trong workflow.

Các quyền giữ fixture Time/Claim đã được người dùng cho phép trong cuộc trò chuyện;
không cần hỏi lại cho cùng phạm vi fixture riêng, nhưng vẫn phải guard đúng CD.
