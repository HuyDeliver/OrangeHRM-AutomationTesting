# Issue #1890 — Timesheet có một dòng giờ trống

**Đã tái hiện mất dữ liệu giờ qua giao diện OrangeHRM OS 5.9 local ngày 18/10/2026.** Trong phép thử thủ công, thêm hai dòng mới: A có `01:00`, B đã chọn Project/Activity nhưng để nguyên bảy ô giờ trống. Sau Save và refresh, bảng hiển thị **No Records Found**, không có dòng A.

Tham chiếu: [OrangeHRM issue #1890](https://github.com/orangehrm/orangehrm/issues/1890), báo cáo gốc trên 5.7. Phép thử local xác nhận phần dữ liệu không được lưu; **không bắt được toast thành công**, nên không khẳng định đã quan sát được thông báo Success.

## Môi trường và dữ liệu được phép giữ

Người dùng đã cho phép khởi tạo Time với ngày đầu tuần **Monday** và giữ fixture riêng. Thiết lập khởi tạo không hoàn tác được bằng UI; project và timesheet này được giữ lại để chạy regression.

- Customer: `ISS1890 Customer`.
- Project: `ISS1890 Project`.
- Hai Activity: `ISS1890 Activity A`, `ISS1890 Activity B`.
- Timesheet của Admin, tuần **2026-10-12 đến 2026-10-18**, trạng thái **Not Submitted**.
- Sau mỗi test: chỉ còn dòng A, tổng giờ **00:00**; không có dòng B.

Kiểm tra DB chỉ đọc sau lần chạy cuối xác nhận đúng **1 Customer, 1 Project, 2 Activity**, chỉ **1 timesheet** của tuần đã cho phép, trạng thái **Not Submitted**. Chỉ còn một item A với tổng duration **0 giây**, không có item B hoặc dữ liệu Time ngoài fixture. Đây là giữ fixture 0 giờ theo phê duyệt, không phải xóa sạch dữ liệu.

Kiểm tra ban đầu chỉ đọc xác nhận Time chưa có dữ liệu; ảnh giao diện trước phép thử cũng ghi nhận **No Records Found**. Mọi thao tác khởi tạo và thay đổi dữ liệu của phép thử đều qua Selenium UI, không dùng Rest Assured, API hoặc SQL để ghi dữ liệu. Không Submit timesheet và không sửa timesheet ngoài fixture.

## Tái hiện thủ công trước khi viết test

1. Mở timesheet trống của đúng tuần fixture, click Edit.
2. Thêm dòng A, chọn Project/Activity A và nhập `01:00` ở ngày đầu.
3. Thêm dòng B, chọn Project/Activity B; không click hay nhập vào bảy ô giờ của B.
4. Save rồi refresh để đọc dữ liệu đã lưu.

| Kiểm tra | Expected | Actual local |
|---|---|---|
| B trước Save | Bảy ô giờ còn trống | Cả bảy ô đều có giá trị rỗng |
| A sau Save và refresh | Giữ `01:00` | Không có dòng A; **No Records Found** |
| Toast thành công | Chỉ ghi nhận nếu quan sát được | **Không bắt được** |

Phép thử hoàn tất lúc **22:36:20 ngày 18/10/2026 (UTC+7)**. Sau đó đã chuẩn bị lại dòng A với `00:00` làm baseline lặp lại cho automation, xác nhận lúc **22:39:22**.

Bằng chứng: [trước Save](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1890-20261018/manual-before-save.png), [sau Save](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1890-20261018/manual-after-save.png), [sau refresh](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1890-20261018/manual-after-refresh.png), [kết quả thủ công](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1890-20261018/manual-results.json), [baseline 00:00](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1890-20261018/zero-baseline.json).

## Hai testcase tự động

[Issue1890RegressionTest.java](E:/Studying/GraduateProject/orangehrmtesting/src/test/java/com/hrm/TestCase/TimeTab/Issue1890RegressionTest.java) extends `TestBase`. `@BeforeMethod` đăng nhập bằng `TestUtil.timeUtil()`, mở đúng tuần và kiểm tra trạng thái Not Submitted. Chỉ tiếp tục khi baseline có đúng dòng A của fixture, tổng `00:00`, chưa có B.

**Khác biệt cần nói rõ:** phép thử thủ công tạo mới cả A và B; automation dùng A đã tồn tại ở `00:00`, sửa A thành `01:00` rồi thêm B mới với giờ trống. Biến thể này tránh tạo thêm timesheet/project mỗi lần chạy và kiểm tra việc dòng trống cản lưu thay đổi hợp lệ. Không mô tả hai phép thử là hoàn toàn giống nhau.

| Test | Input và thao tác | Assertion sau Save + refresh |
|---|---|---|
| `validRowShouldKeepOneHourAfterSave` — đối chứng | A từ `00:00` thành `01:00`, không thêm B | Giờ ngày đầu của A bằng `01:00` |
| `emptyRowShouldNotDiscardValidHours` — regression | A từ `00:00` thành `01:00`, thêm B với bảy ô giờ trống | B thực sự trống trước Save; giờ ngày đầu của A sau refresh vẫn bằng `01:00` |

`@AfterMethod(alwaysRun = true)` bỏ B nếu B đã được lưu, trả A về `00:00`, refresh và xác nhận baseline. Trước khi reset, POM kiểm tra đúng tuần, trạng thái Not Submitted, tên project và Activity của fixture; nếu sai điều kiện hoặc thấy dòng ngoài fixture thì dừng. Sau mỗi refresh cũng kiểm tra vẫn đúng tuần. Không xóa Customer, Project, Activity hoặc timesheet đã được phép giữ.

## Giải thích cho Junior

- **Input:** tuần, Project/Activity và `01:00` lấy từ `TestConfig`; B để trống thật, không nhập `00:00` thay cho trống.
- **Selenium làm gì?** Click Edit, nhập giờ, chọn Project/Activity, click Save rồi refresh, giống thao tác bằng tay.
- **Vì sao có case đối chứng?** Xác nhận một dòng hợp lệ lưu được khi chưa có B. Nếu cả hai case đều FAIL, cần kiểm tra setup hoặc tính năng lưu trước khi quy lỗi cho dòng trống.
- **Vì sao refresh?** Để đọc lại dữ liệu hệ thống đã lưu, không chỉ nhìn giá trị vừa gõ hoặc thông báo trên màn hình.
- **Assertion kiểm tra gì?** `Assert.assertEquals(actual, "01:00", ...)` so giờ đã lưu với giờ đã nhập. Không đổi expected thành `00:00` để làm test PASS.
- **PASS nghĩa là gì?** A giữ `01:00` sau khi tải lại, kể cả có B trống trong case regression.
- **FAIL nghĩa là gì?** Nếu đối chứng PASS, B đã được xác nhận trống và A không giữ `01:00`, có bằng chứng hành vi lưu sai của biến thể regression. Lỗi đăng nhập, locator hoặc setup riêng chưa chứng minh issue #1890.
- **Giới hạn:** đây là kiểm tra UI, không đo HTTP status và không khẳng định API trả 500. Test cũng không chứng minh nguyên nhân kỹ thuật hoặc toast Success nếu chưa quan sát được.

## Tái sử dụng component và chờ ngắn

[TimesheetPage.java](E:/Studying/GraduateProject/orangehrmtesting/src/main/java/com/hrm/Pages/TimePage/TimesheetPage.java) dùng `componentLocator.clickDropdownNavi()` để mở My Timesheets và `waitUtils.waitElementClick()` cho click. Ô giờ nằm trong bảng, Save Timesheet nằm ngoài `.oxd-form-actions`, nên các locator riêng trong POM phục vụ cấu trúc màn hình này; không viết lại component chung.

Log4j ghi từng bước, baseline, giờ expected/actual và reset. Listener hiện có lưu screenshot khi FAIL; test còn chụp ảnh case đối chứng và A/B trước Save.

Chỉ class này đặt implicit wait về **0 sau đăng nhập**. Các bước tải trang/chọn option dùng điều kiện chờ tối đa **5 giây**, đủ điều kiện thì chạy tiếp ngay. Không dùng `Thread.sleep`, không đổi wait chung của các module khác.

## Chạy và kết quả TestNG

[testng_issue1890.xml](E:/Studying/GraduateProject/orangehrmtesting/testng_issue1890.xml) chỉ chạy hai testcase này, chạy tuần tự, dùng listener hiện có và không retry để che kết quả FAIL.

```powershell
mvn -o "-Dtestng.suite=testng_issue1890.xml" test
```

Compile **BUILD SUCCESS** lúc **22:42:01 ngày 18/10/2026 (UTC+7)**, mất **24,069 giây**. Lần chạy đầu hoàn tất lúc **22:43:22**, ghi nhận 1 PASS và 1 FAIL. Sau khi bổ sung kiểm tra đúng tuần sau refresh và trước reset, đã chạy lại cùng expected và assertion.

Lần chạy cuối hoàn tất lúc **22:45:15**, có **2 tests: 1 PASS, 1 FAIL, 0 error, 0 skip**. Suite mất **35,89 giây**; Maven tổng **38,229 giây** và báo BUILD FAILURE vì regression phát hiện hành vi sai, không phải compile lỗi.

| Testcase | Expected A sau refresh | Actual A sau refresh | Kết quả | Reset sau test |
|---|---|---|---|---|
| Đối chứng: chỉ sửa A | `01:00` | `01:00` | **PASS** | Passed: A `00:00`, B không tồn tại |
| Regression: sửa A và thêm B trống | `01:00` | `00:00` | **FAIL** tại assertion dòng 71 | Passed: A `00:00`, B không tồn tại |

Đối chứng xác nhận A có thể lưu `01:00`. Khi có B với bảy ô giờ trống, thay đổi `00:00 → 01:00` của A không được lưu dù đã Save; refresh vẫn đọc lại `00:00`. Đây là kết quả thật của biến thể automation, khác kết quả thủ công hai dòng mới là **No Records Found**. Không đổi assertion để lấy PASS và không khẳng định đã quan sát toast Success.

Bằng chứng automation: [kết quả JSON](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1890-20261018/regression-results.json), [đối chứng lưu 01:00](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1890-20261018/regression-control.png), [A có giờ và B trống trước Save](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1890-20261018/regression-before-save.png), [ảnh lúc regression FAIL](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1890-20261018/regression-failure.png).
