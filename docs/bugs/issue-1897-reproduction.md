# Issue #1897 — Sửa khoảng lương của Pay Grade A làm đổi cả Pay Grade B

> Cách chạy hiện tại (26/10/2026): `mvn test "-Dmodule=Admin"` qua `testng.xml`; lệnh này chạy toàn bộ module đã chọn, gồm cả issue. Các tên XML/lệnh riêng và số liệu bên dưới là lịch sử trước khi gộp. Xem [hướng dẫn module](../test-execution.md).

**Đã tái hiện bằng giao diện trên OrangeHRM OS 5.9 local ngày 18/10/2026.** Chỉ sửa khoảng lương EUR của Pay Grade A, nhưng mở lại Pay Grade B thì khoảng lương EUR của B cũng đổi theo.

Issue tham chiếu: [OrangeHRM #1897](https://github.com/orangehrm/orangehrm/issues/1897), được báo trên **5.7**. Kết quả ở đây thuộc local **5.9**. Đây là lỗi cập nhật dữ liệu giữa các Pay Grade; chưa đủ bằng chứng để gọi là lỗ hổng bảo mật hoặc kết luận mọi currency đều bị ảnh hưởng.

## Môi trường và dữ liệu chẩn đoán

- Màn hình: **Admin → Job → Pay Grades**.
- Đăng nhập bằng tài khoản trong config hiện có.
- Danh sách ban đầu có một Pay Grade **Engineer**, currency **Vietnamese Dong**.
- Currency được chọn cho phép thử: **EUR - Euro**, đã kiểm tra chưa được dùng trong các Pay Grade có sẵn. Điều kiện này tránh sửa trùng currency đang gắn với dữ liệu người dùng khi bug xảy ra.
- Tạo hai Pay Grade riêng: `ISS1897_1792296044596_A` và `ISS1897_1792296044596_B`.
- Mỗi grade có một dòng Euro với khoảng lương khác nhau để dễ nhận ra thay đổi ngoài ý muốn.

Các tên và số tiền dưới đây là dữ liệu của phép thử chẩn đoán đã chạy, không phải ID hoặc dữ liệu có sẵn của người dùng.

## Các bước tái hiện

1. Mở Pay Grades và kiểm tra currency dành cho test chưa được dùng trong dữ liệu hiện có.
2. Tạo A, thêm **EUR - Euro**, Minimum Salary **1000**, Maximum Salary **2000**.
3. Tạo B, thêm **EUR - Euro**, Minimum Salary **3000**, Maximum Salary **4000**.
4. Mở lại B, ghi nhận dòng Euro hiện tại làm baseline.
5. Mở A, chỉ sửa dòng Euro của A thành **1500–2500**, rồi Save.
6. Rời trang và mở lại A, xác nhận A đã lưu đúng khoảng mới.
7. Rời trang và mở lại B, so sánh dòng Euro với baseline của B. Không edit hoặc Save B sau khi sửa A.

| Quan sát | Pay Grade A | Pay Grade B |
|---|---|---|
| Trước khi sửa | Euro / 1,000.00 / 2,000.00 | Euro / 3,000.00 / 4,000.00 |
| Thao tác sửa | Đổi thành 1,500.00–2,500.00 rồi Save | Không sửa |
| Expected khi mở lại | Euro / 1,500.00 / 2,500.00 | Euro / 3,000.00 / 4,000.00 |
| Actual khi mở lại | Euro / 1,500.00 / 2,500.00 | **Euro / 1,500.00 / 2,500.00** |

**Expected:** Sửa khoảng lương của một currency trong A chỉ được cập nhật dòng tương ứng của A. B phải giữ nguyên khoảng lương đã lưu trước đó.

**Actual:** A lưu đúng giá trị mới, nhưng B cũng đổi từ 3000–4000 sang 1500–2500. Mở lại từng grade giúp phân biệt thay đổi đã lưu với việc chỉ nhìn nhầm dữ liệu còn trên form A.

**Kết luận chẩn đoán:** Đã xác nhận cập nhật ngoài ý muốn sang B trong dữ liệu đối chứng có cùng currency. Kết quả UI chứng minh hành vi sai nhưng không tự chứng minh query SQL hoặc nguyên nhân ORM cụ thể.

## Bằng chứng và cleanup chẩn đoán

- [Baseline và hai tên grade riêng](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1897-20261018/baseline.json).
- [A trước sửa](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1897-20261018/grade-A-before.json), [B trước sửa](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1897-20261018/grade-B-before.json) và [ảnh baseline B](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1897-20261018/grade-B-before.png).
- [A sau sửa](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1897-20261018/grade-A-after.json), [ảnh A sau sửa](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1897-20261018/grade-A-after.png).
- [B sau khi chỉ sửa A](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1897-20261018/grade-B-after.json), [ảnh B bị đổi theo A](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1897-20261018/grade-B-after.png).
- [Cleanup đã xác nhận](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1897-20261018/cleanup.json): xóa đúng A và B bằng UI lúc **11:06:29 ngày 18/10/2026**. Danh sách trở về Pay Grade Engineer / Vietnamese Dong như baseline, `originalListPreserved=true`.

Không xóa, sửa tên hoặc chỉnh khoảng lương của grade Engineer. Cleanup chỉ nhắm tới hai tên grade do phép thử tạo; không dùng API hoặc database để dọn dữ liệu.

## Regression automation — Selenium/POM/TestNG/Log4j

[Issue1897RegressionTest.java](E:/Studying/GraduateProject/orangehrmtesting/src/test/java/com/hrm/TestCase/AdminTab/Issue1897RegressionTest.java) được tổ chức thành một testcase ngắn. `UserManagePage` mở Pay Grades; `PayGradePage` giữ thao tác trên form và bảng. Test data lấy từ `TestConfig`, đăng nhập bằng config hiện có; không dùng Rest Assured, cookie hoặc ID hard-code.

Flow của regression:

1. Kiểm tra toàn bộ Pay Grades hiện có để chắc chắn currency dành riêng cho test chưa được dùng; nếu không bảo đảm điều kiện thì dừng trước khi tạo/sửa dữ liệu.
2. Tạo hai tên grade có marker riêng cho lần chạy; thêm cùng currency nhưng hai khoảng lương khác nhau.
3. Đọc dòng currency của B trên UI làm baseline động.
4. Edit currency của A, Save; mở lại A và kiểm tra A đã lưu đúng khoảng mới. Đây là bước đối chứng để tránh báo PASS khi thao tác sửa A chưa thực hiện thành công.
5. Mở lại B và assert dòng currency vẫn bằng baseline đã đọc ở bước 3.
6. Dọn đúng hai grade riêng bằng UI trong teardown, kể cả khi assertion phát hiện bug và FAIL.

Page Object tái sử dụng các component chọn dropdown, điền input, Save, Add, Edit và Delete theo cấu trúc project; giữ nguyên source component. Riêng form Edit Pay Grade có hai nút Save, nên thao tác lưu currency dùng locator hẹp trong `PayGradePage` để bấm đúng Save của form currency, tránh nút Save tên grade.

### Giải thích cho Junior

- **Input:** Cùng currency, hai grade riêng A/B và ba khoảng lương: ban đầu A, ban đầu B, giá trị mới của A.
- **Vì sao phải có B?** Nếu chỉ sửa A rồi kiểm tra A, đó là test chức năng lưu bình thường. B giúp phát hiện ảnh hưởng ngoài đối tượng đang sửa.
- **Vì sao đọc baseline B?** So sánh với dòng UI đã lưu trước thao tác, thay vì tự đoán định dạng số tiền hoặc lấy giá trị mới của A làm expected cho B.
- **Assertion:** A phải có khoảng mới; B phải giữ dòng baseline cũ khi mở lại.
- **PASS:** A đổi đúng và B không đổi trong điều kiện của lần chạy.
- **FAIL:** Nếu đã xác nhận sửa A thành công nhưng B đổi theo, regression phát hiện hành vi đúng với issue. Nếu lỗi login, thiếu currency hoặc không lưu được dữ liệu setup, cần báo lỗi bước đó, không gộp thành bug #1897.
- **Giới hạn:** Test chỉ quan sát UI, không đo HTTP status hoặc nội dung SQL. Một lần PASS không chứng minh mọi currency, mọi grade và mọi môi trường đều an toàn.

### Kết quả TestNG

Đã chạy class regression riêng trên local **5.9**, kết thúc lúc **11:11:24 ngày 18/10/2026 (UTC+7)**:

- Compile: `mvn -o -DskipTests test-compile` — **BUILD SUCCESS**, lúc 11:08:05.
- TestNG: **1 test, 0 PASS, 1 FAIL, 0 error, 0 skip**.
- Pay Grade A của lần chạy: `ISS1897_A_1792296524632`.
- Pay Grade B của lần chạy: `ISS1897_B_1792296524632`.
- A ban đầu **1000–2000**, B ban đầu **3000–4000**; cả hai được assert trước thao tác sửa.
- Sau Save và refresh, A hiển thị **1500–2500**, đúng input mới.
- Mở lại B: expected **3000–4000**, actual **1500–2500**. Assertion so sánh B với baseline FAIL tại dòng 70 của testcase.
- Cleanup `@AfterMethod`: **passed**, đã xóa đúng hai grade của lần chạy, hoàn tất lúc 11:11:23. Kiểm tra DB chỉ đọc sau cleanup xác nhận chỉ còn Engineer / VND / **10000–20000**, không còn grade `ISS1897*` hoặc assignment EUR.

**FAIL này phát hiện bug #1897 trên local:** A đã được xác nhận lưu đúng, nhưng B bị thay đổi dù testcase không sửa B. Maven báo `BUILD FAILURE` vì assertion này, không phải lỗi compile. Giữ nguyên expected B, không đổi assertion để lấy PASS.

Chạy lại bằng suite riêng:

```powershell
mvn -o "-Dtestng.suite=testng_issue1897.xml" test
```

Bằng chứng của lần chạy TestNG: [kết quả JSON](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1897-20261018/regression-results.json) và [ảnh lúc assertion FAIL](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1897-20261018/regression-failure.png). Log từng bước và screenshot cũng được ghi vào Allure qua listener hiện có.
