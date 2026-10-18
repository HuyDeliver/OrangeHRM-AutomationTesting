# Issue #1820 — Upload attachment trong Claim

**Chưa tái hiện lỗi trên OrangeHRM OS 5.9 local với PDF đã thử.** Upload `JobSpecification.pdf` thành công, tên file và Comment vẫn có trong Attachments sau refresh. TestNG **1 PASS**, cleanup đã xóa file thử.

Tham chiếu: [issue #1820](https://github.com/orangehrm/orangehrm/issues/1820), báo cáo trên **5.6**: tạo Claim rồi upload file, Save báo “Unexpected error occurred”. Báo cáo không cung cấp file cụ thể. Kết quả local với một PDF không chứng minh lỗi đã được sửa trong mọi môi trường hoặc với mọi định dạng.

## Fixture đã được cho phép giữ

Local ban đầu không có Claim và Event. Người dùng đã cho phép tạo và giữ lại:

- Event active: `ISS1820 Attachment Test`.
- Một Claim nháp của tài khoản đăng nhập, Remarks: `ISS1820 reusable draft - attachment regression`.
- Currency: `Vietnamese Dong`; Status: **Initiated**.
- Reference Id thực tế: `202610180000001`, chỉ ghi làm bằng chứng; test không hard-code ID này.
- Không tạo expense, không Submit, không Cancel Claim.
- Sau kiểm tra: attachment trống; Event và Claim được giữ lại cho lần sau.

Claim đã tạo không có chức năng Delete trên UI local; Cancel chỉ đổi trạng thái. Vì vậy testcase mở lại đúng Claim theo Remarks, không tạo Claim mới mỗi lần chạy. Nếu fixture bị Submit, đổi marker hoặc có attachment khác, test dừng ở điều kiện setup.

File thử tái sử dụng `TestConfig.jobSpecification`: PDF có sẵn trong project, **224.012 bytes**, nhỏ hơn giới hạn **1.048.576 bytes (1 MB)**. Không thêm file hoặc framework mới.

## Kiểm tra thủ công qua Selenium trước automation

1. Tạo Event và Claim nháp qua UI sau khi được phép.
2. Kiểm tra đúng marker, Status Initiated và Attachments đang trống.
3. Click Add tại Attachments bằng `buttonAddMyInfo()`, chọn PDF bằng `fillFile()`, nhập Comment riêng.
4. Save bằng `buttonSave()`: quan sát toast **Success / Successfully Saved**.
5. Refresh: bảng còn `JobSpecification.pdf`, Comment đúng, size **224.01 kB**, type **application/pdf**.
6. Xóa đúng dòng có Comment của phép thử, tải lại và xác nhận attachment về 0.

Phép upload hoàn tất lúc **00:03:34 ngày 19/10/2026 (UTC+7)**; xác nhận fixture trống lúc **00:05:58**. File bằng chứng nằm trong thư mục mang ngày bắt đầu công việc 18/10.

Bằng chứng: [trước khi thử](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1820-20261018/baseline.json), [dữ liệu đã chọn](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1820-20261018/manual-upload-filled.png), [sau Save](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1820-20261018/manual-after-save.png), [sau refresh](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1820-20261018/manual-after-refresh.png), [kết quả](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1820-20261018/manual-results.json), [cleanup](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1820-20261018/manual-cleanup.png).

## Code và giải thích cho Junior

[Issue1820RegressionTest.java](E:/Studying/GraduateProject/orangehrmtesting/src/test/java/com/hrm/TestCase/ClaimTab/Issue1820RegressionTest.java) có **78 dòng**, [ClaimPage.java](E:/Studying/GraduateProject/orangehrmtesting/src/main/java/com/hrm/Pages/ClaimPage/ClaimPage.java) có **100 dòng**.

- **Arrange:** `TestUtil.claimUtil()` đăng nhập bằng config rồi mở Claim. POM mở My Claims, tìm đúng một dòng chứa Remarks riêng và click View Details. Test kiểm tra Remarks, Event, Currency, Status và attachment trống, rồi kiểm tra file có sẵn và không quá 1 MB.
- **Act:** `uploadAttachment()` click Add, gửi đường dẫn PDF vào input file, nhập Comment `ISS1820_FILE_<timestamp>`, rồi Save. Đây là thao tác Selenium trên UI; không dùng Rest Assured hoặc gọi API trực tiếp.
- **Assert:** không có error toast; sau refresh, một dòng phải chứa cả tên file và Comment vừa gửi. Bảng Attachments phải có đúng một dòng. Kiểm tra sau refresh giúp xác nhận dữ liệu được đọc lại từ hệ thống.
- **Cleanup:** `@AfterMethod(alwaysRun = true)` mở lại Claim, xác nhận đúng marker và còn Initiated, rồi xóa riêng dòng chứa Comment của lần chạy. Sau xóa, POM refresh và test xác nhận attachment về 0. Không xóa Claim/Event đã được phép giữ.

`Path` lấy tên file từ đường dẫn; `Files.size()` đọc dung lượng. `hasAttachment()` dùng lại `componentLocator.checkTable()` với hai giá trị tên file và Comment. `Assert.assertTrue(saved, ...)` đòi hỏi kết quả phải là `true`; không chỉnh assertion để tạo FAIL hoặc PASS.

**PASS:** PDF hợp lệ đã được lưu và còn xuất hiện sau refresh. **FAIL khi có error toast hoặc không thấy đúng file sau refresh:** có dấu hiệu upload không thành công; cần xem ảnh, log và điều kiện môi trường trước khi gán nguyên nhân cho #1820. **FAIL setup:** lỗi fixture/file/đăng nhập chưa chứng minh lỗi upload. **Cleanup FAIL:** dữ liệu thử chưa được dọn xong, cần kiểm tra riêng.

Giới hạn: test kiểm tra dòng metadata trên UI sau refresh, chưa kiểm tra tải xuống và so sánh nội dung nhị phân của file.

## Tái sử dụng component

Dùng lại `TestBase`, `Log` (Log4j + Allure), `TestListener`, `TestConfig`, `componentLocator` và `waitUtils`. Thêm `claimUtil()` và `SideBar.clickClaimPage()` theo đúng các module đang có.

POM gọi `clickDropdownNavi()`, `buttonAddMyInfo()`, `fillFile()`, `fillTexArea()`, `buttonSave()`, `checkTable()` và `deleteButtonTable()`. Nút Claim **View Details** là nút chữ, khác icon `bi-eye-fill` của `viewButtonTable()`, nên POM có locator riêng đã kiểm tra trên DOM local. Class `.orangehrm-card-table` thuộc bảng Attachments, không đếm Expenses.

Implicit wait chỉ đặt về 0 sau login trong test này. Chờ có điều kiện tối đa 5 giây cho tải trang, modal và kết quả Save; không thêm `Thread.sleep`, không sửa utility chung. Log ghi bước, Reference Id lấy động, tên file, dung lượng, Comment, expected/actual và cleanup. `showAttachments()` dùng Selenium Actions cuộn tới bảng trước khi chụp để ảnh thấy rõ dòng file. Ảnh sau refresh được gắn Allure; listener hiện có chụp ảnh khi FAIL.

## Chạy và kết quả thật

[testng_issue1820.xml](E:/Studying/GraduateProject/orangehrmtesting/testng_issue1820.xml) chỉ chạy testcase này, tuần tự, dùng listener hiện có và không retry.

```powershell
mvn -o -DskipTests test-compile
mvn -o "-Dtestng.suite=testng_issue1820.xml" test
```

Compile **BUILD SUCCESS**, hoàn tất **00:06:09 ngày 19/10/2026**, mất **6,622 giây**. Lần chạy cuối hoàn tất **00:10:15**, suite **16,13 giây**, Maven tổng **18,371 giây** và **BUILD SUCCESS**. Cả hai lần chạy đều PASS và cleanup PASS; expected và assertion không đổi.

| Kiểm tra | Expected | Actual | Kết quả |
|---|---|---|---|
| Claim trước upload | Đúng fixture, Initiated, 0 attachment | Đúng điều kiện | Đạt |
| Save | Không có error toast | Không có error toast | Đạt |
| Sau refresh | Có tên PDF và đúng Comment | `saved=true` | Đạt |
| Số file sau refresh | 1 | 1 | Đạt |
| Cleanup sau test | 0 attachment, giữ Claim nháp | 0 attachment | Đạt |

TestNG: **1 test, 0 failure, 0 error, 0 skip**. Setup, cleanup và teardown đều passed. Chưa tái hiện issue #1820 trên dữ liệu đã thử; không kết luận toàn bộ issue đã được sửa.

Kiểm tra DB bằng SELECT chỉ đọc sau lần chạy xác nhận độc lập: **1 Event active, 1 Claim INITIATED, 0 attachment, 0 expense**. Không ghi dữ liệu qua DB. [Kết quả trạng thái cuối](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1820-20261018/final-state-readonly.json).

Bằng chứng automation: [kết quả JSON](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1820-20261018/regression-results.json), [ảnh file sau refresh](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1820-20261018/regression-after-refresh.png), [fixture được giữ](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1820-20261018/fixture.json).

Source local được kiểm tra chỉ đọc trong container `/var/www/html/src/`: `ConfigService.php` quy định loại file/dung lượng, `AddAttachmentModal.vue` validate và Save, `ClaimAttachment.vue` tải lại/xóa file, `EmployeeClaimRequestAPI.php` chưa implement Delete Claim. Mọi thay đổi dữ liệu của phép thử đều qua Selenium UI.
