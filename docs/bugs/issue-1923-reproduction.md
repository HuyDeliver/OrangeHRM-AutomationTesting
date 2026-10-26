# Issue #1923 — Click lại avatar nhưng menu tài khoản không đóng

> Cách chạy hiện tại (26/10/2026): `mvn test "-Dmodule=Profile"` qua `testng.xml`; lệnh này chạy toàn bộ module đã chọn, gồm cả issue. Các tên XML/lệnh riêng và số liệu bên dưới là lịch sử trước khi gộp. Xem [hướng dẫn module](../test-execution.md).

**Đã tái hiện bằng Selenium trên OrangeHRM OS 5.9 local ngày 18/10/2026 lúc 22:00:34 (UTC+7).** Click avatar lần đầu mở menu; click lại đúng avatar thì menu vẫn mở. Click bên ngoài đóng được menu.

Issue tham chiếu: [OrangeHRM #1923](https://github.com/orangehrm/orangehrm/issues/1923), được báo trên **5.8**. Kết quả dưới đây thuộc bản local **5.9**. Đây là lỗi tương tác UI/UX; phép thử này không đánh giá bảo mật.

## Các bước và kết quả chẩn đoán

1. Đăng nhập bằng tài khoản trong config hiện có.
2. Click ảnh avatar ở góc phải; xác nhận menu tài khoản xuất hiện.
3. Click lại đúng ảnh avatar.
4. Kiểm tra menu có đóng không.
5. Refresh trang, mở menu lại và xác nhận menu đang mở; click tiêu đề trang bên ngoài menu để kiểm tra đối chứng độc lập.

| Thao tác | Expected | Actual trên local 5.9 |
|---|---|---|
| Click avatar lần đầu | Menu mở | Menu mở |
| Click lại avatar | Menu đóng | **Menu vẫn mở** |
| Click ra ngoài menu | Menu đóng | Menu đóng |

Kết quả đối chứng cho thấy menu có thể đóng khi click bên ngoài; hành vi không đóng được quan sát khi click lại avatar. Không tạo, sửa hoặc xóa dữ liệu trong phép thử.

Bằng chứng chẩn đoán: [kết quả JSON](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1923-20261018/manual-results.json), [menu mở lần đầu](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1923-20261018/manual-menu-open.png), [sau click avatar lần hai](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1923-20261018/manual-avatar-second-click.png), [sau click ra ngoài](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1923-20261018/manual-outside-click.png).

## Hai testcase regression

[Issue1923RegressionTest.java](E:/Studying/GraduateProject/orangehrmtesting/src/test/java/com/hrm/TestCase/ProfileTab/Issue1923RegressionTest.java) extends `TestBase`, dùng `@BeforeMethod` để đăng nhập qua `TestUtil.userUtil()` và mở menu. Bước setup assert menu đã mở trước khi chạy thao tác cần kiểm tra.

| Testcase | Thao tác chính | Assertion |
|---|---|---|
| `clickingAvatarAgainShouldCloseProfileMenu` | Click lại avatar đang mở menu | Menu phải đóng |
| `clickingOutsideShouldCloseProfileMenu` | Mở menu rồi click tiêu đề trang bên ngoài | Menu phải đóng |

Giải thích cho Junior:

- **Input:** Hai thao tác click; không cần payload, employee ID hoặc dữ liệu test mới.
- **Selenium:** `clickAvatar()` bấm ảnh avatar; `clickOutsideMenu()` bấm tiêu đề trang. `isMenuClosed()` kiểm tra menu đã ẩn hoặc không còn trên trang.
- **Vì sao kiểm tra menu mở trước?** Nếu menu chưa từng mở, kiểm tra “menu đã đóng” có thể PASS dù thao tác mở menu bị lỗi.
- **PASS:** Menu đóng sau thao tác đang được kiểm tra.
- **FAIL ở test click lại avatar:** Khi setup đã xác nhận menu mở, menu vẫn chưa đóng sau click lần hai. Hành vi này phù hợp với issue #1923.
- **FAIL ở setup hoặc case đối chứng:** Cần xem log và screenshot để xác định lỗi đăng nhập, locator, tải trang hoặc hành vi khác; không tự gộp mọi lỗi thành issue #1923.

## Tái sử dụng và thời gian chờ

[ProfileMenuPage.java](E:/Studying/GraduateProject/orangehrmtesting/src/main/java/com/hrm/Pages/ProfileMenuPage.java) giữ locator và các thao tác menu theo POM. Click dùng `waitUtils.waitElementClick` hiện có; `TestUtil` đăng nhập, `Log` ghi từng bước và `TestListener` hỗ trợ screenshot/Allure khi FAIL. Không dùng Rest Assured, API hoặc database.

Không cần thêm phương thức vào `componentLocator`: phần dùng chung đã có cơ chế click trong `waitUtils`; các locator đặc thù avatar và menu nằm trong Page Object.

Để giảm thời gian chờ:

- Sau đăng nhập, **chỉ trong class test này**, implicit wait được đặt về `0`. Nếu vẫn giữ implicit wait 10 giây, việc tìm menu đã biến mất có thể kéo dài không cần thiết.
- Kiểm tra menu dùng điều kiện với thời gian chờ tối đa **2 giây**, trả về ngay khi đạt điều kiện. Hai giây là giới hạn chờ, không phải luôn đứng yên hai giây.
- Không dùng `Thread.sleep`; không sửa thời gian chờ chung trong `TestBase` hoặc các testcase khác.
- Tái sử dụng điều kiện chờ click hiện có; menu đã sẵn sàng thì click được thực hiện ngay.

## Chạy suite riêng

`testng_issue1923.xml` (suite lịch sử, đã gộp) chỉ chạy hai testcase này và listener hiện có; không thêm retry để che kết quả FAIL.

```powershell
mvn -o "-Dtestng.suite=testng_issue1923.xml" test
```

## Kết quả regression thực tế

Chạy trên local **5.9 ngày 18/10/2026 (UTC+7)**:

- Compile: **BUILD SUCCESS**, lúc **22:02:15**.
- Maven chạy suite riêng hoàn tất lúc **22:02:53**.
- TestNG: **2 tests, 1 PASS, 1 FAIL, 0 error, 0 skip**.

| Testcase | Actual | Kết quả |
|---|---|---|
| Click lại avatar | `menu đã đóng: false` | **FAIL** tại assertion dòng 46 |
| Click ra ngoài | `menu đã đóng: true` | **PASS** |

TestNG suite mất **15,06 giây**, bao gồm mở trình duyệt và đăng nhập cho cả hai case. Tổng Maven mất **22,389 giây**. Chưa có phép đo cùng testcase với cấu hình chờ cũ nên không kết luận mức cải thiện tốc độ.

FAIL ở case click lại avatar phát hiện hành vi đúng với issue #1923; case đối chứng đóng menu thành công. Maven báo `BUILD FAILURE` do assertion này, không phải lỗi compile. Giữ nguyên expected menu phải đóng.

Bằng chứng của lần chạy: [kết quả regression JSON](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1923-20261018/regression-results.json) và [screenshot lúc FAIL](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1923-20261018/regression-failure.png). Test không thay đổi dữ liệu nên không cần cleanup bản ghi.
