# Issue #1787 — Ngày đầu tuần trong lịch Attendance

**Local OrangeHRM OS 5.9 hiển thị Timesheet bắt đầu Monday nhưng lịch Attendance bắt đầu Sunday.** Kiểm tra thủ công và Selenium đều quan sát được khác biệt này ngày 18/10/2026.

Tham chiếu: [issue #1787](https://github.com/orangehrm/orangehrm/issues/1787), báo cáo gốc trên **5.5**. Expected Monday của test lấy từ mô tả issue. Chưa có xác nhận nghiệp vụ rằng cấu hình **Timesheet Period** phải áp dụng cho mọi date picker; vì vậy kết quả FAIL tái hiện hiện tượng được báo, chưa tự nó chứng minh vi phạm một yêu cầu đã được phê duyệt.

## Điều kiện và phạm vi

- Giao diện tiếng Anh, đăng nhập bằng tài khoản trong config hiện có.
- Time đã được khởi tạo Monday trong lần làm #1890, không thay đổi cấu hình đó.
- Mở tuần từ `TestConfig.timesheetStartDate` để đọc tiêu đề ngày của Timesheet làm đối chiếu.
- Chỉ đọc UI: không Save, Submit, Punch In/Out, tạo, sửa hoặc xóa bản ghi.
- Không dùng Rest Assured, SQL hoặc API để thao tác dữ liệu.

## Kiểm tra thủ công trước automation

1. Đăng nhập → Time → Timesheets → My Timesheets.
2. Mở tuần **2026-10-12 to 2026-10-18**: cột ngày đầu hiển thị **Mon**.
3. Mở Attendance → My Records → click biểu tượng lịch ở ô Date.
4. Đọc hàng tên thứ: **S, M, T, W, T, F, S**. Lịch bắt đầu Sunday.

Hoàn tất lúc **23:02:16 ngày 18/10/2026 (UTC+7)**. Bằng chứng: [Timesheet](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1787-20261018/manual-timesheet-monday.png), [lịch Attendance](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1787-20261018/manual-attendance-calendar.png), [kết quả JSON](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1787-20261018/manual-results.json).

## Code và cách hiểu cho Junior

[Issue1787RegressionTest.java](E:/Studying/GraduateProject/orangehrmtesting/src/test/java/com/hrm/TestCase/TimeTab/Issue1787RegressionTest.java) có một testcase, chia Arrange / Act / Assert:

1. **Arrange:** dùng `TestUtil.timeUtil()` đăng nhập, mở Time; dùng `TimesheetPage` hiện có mở tuần. `getFirstWeekday()` đọc chữ `Mon` ở cột ngày đầu. Assertion này kiểm tra điều kiện đầu vào của phép thử.
2. **Act:** `AttendancePage.openMyRecords()` dùng `componentLocator.clickDropdownNavi()`; `openDatePicker()` dùng `waitUtils.waitElementClick()` mở lịch. Đây là click Selenium giống thao tác tay.
3. **Assert:** `getWeekdays()` trả danh sách bảy chữ trên lịch. Test kiểm tra đủ bảy chữ trước, rồi so chữ đầu với `M`, ký hiệu Monday trong giao diện này.

`List<String>` ở đây chỉ là danh sách chữ: `[S, M, T, W, T, F, S]`. `weekdays.get(0)` lấy phần tử đầu tiên, tức `S`. `Assert.assertEquals(..., "M", ...)` so actual `S` với expected `M`, nên FAIL.

- **PASS:** Timesheet bắt đầu Mon và lịch Attendance bắt đầu M như expected trong issue.
- **FAIL tại assertion cuối:** tái hiện khác biệt Sunday/Monday được báo. Cần xác nhận phạm vi yêu cầu trước khi phân loại là bug hay đề xuất cải tiến.
- **FAIL ở đăng nhập, locator hoặc điều kiện Timesheet:** chưa đủ căn cứ kết luận về #1787.

[AttendancePage.java](E:/Studying/GraduateProject/orangehrmtesting/src/main/java/com/hrm/Pages/TimePage/AttendancePage.java) chỉ chứa mở trang, mở lịch và đọc các thứ. Locator đã được kiểm tra trên DOM local: tiêu đề là `h5`, icon `.oxd-date-input-icon`, các thứ `.oxd-calendar-week-grid .oxd-calendar-day`.

Tái sử dụng `TestBase`, `TestUtil`, `TestConfig`, `componentLocator`, `waitUtils`, `Log` và `TestListener`. Log4j ghi từng bước và expected/actual; Allure lưu ảnh Timesheet, lịch Attendance và ảnh khi FAIL. Không thêm dependency hoặc framework.

Implicit wait chỉ đặt về 0 sau login trong test này. Chờ có điều kiện tối đa 5 giây khi trang/lịch tải; đủ điều kiện thì chạy tiếp. Không thêm `Thread.sleep`, không sửa wait chung.

## Kết quả chạy thật

```powershell
mvn -o -DskipTests test-compile
mvn -o "-Dtestng.suite=testng_issue1787.xml" test
```

Compile **BUILD SUCCESS**, hoàn tất **23:03:24**, mất **6,678 giây**. Test hoàn tất **23:03:47**, suite **11,72 giây**, Maven tổng **14,304 giây**.

| Kiểm tra | Expected | Actual | Kết quả |
|---|---|---|---|
| Điều kiện Timesheet | Mon | Mon | Đạt |
| Đọc lịch Attendance | Bảy nhãn | S M T W T F S | Đạt |
| Thứ đầu theo issue | M | S | **FAIL** |

TestNG: **1 test, 1 failure, 0 error, 0 skip**. Setup và teardown đều passed. Maven BUILD FAILURE do assertion cuối tại dòng 51, không phải compile/locator lỗi. Không đổi expected thành S để lấy PASS.

Bằng chứng: [kết quả automation](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1787-20261018/regression-results.json), [Timesheet Monday](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1787-20261018/regression-timesheet-monday.png), [lịch Attendance](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1787-20261018/regression-attendance-calendar.png), [ảnh FAIL](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1787-20261018/regression-failure.png).

## Giới hạn từ source local

Đọc source trong container `/var/www/html/src/` cho thấy `TimeSheetPeriod.vue` lưu `startDay` vào cấu hình Timesheet; `TimesheetService.php` dùng cấu hình đó tính khoảng tuần. `core/components/inputs/DateInput.vue` không truyền ngày đầu tuần từ cấu hình này vào lịch; `useLocale.ts` xử lý tên tháng/ngày.

Các chi tiết này giải thích việc hai thành phần dùng cấu hình khác nhau, nhưng không tự xác lập yêu cầu mọi lịch phải dùng cùng ngày đầu tuần. Test này giữ expected của issue để hỗ trợ trao đổi và tái kiểm tra khi yêu cầu được xác nhận.
