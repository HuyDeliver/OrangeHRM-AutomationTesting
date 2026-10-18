# Issue #1926 — Shortlist Candidate

**Chưa tái hiện được issue trên OrangeHRM OS 5.9 local trong phép thử chẩn đoán ngày 18/10/2026.** Candidate riêng ứng tuyển **Devops** được Shortlist thành công, giữ trạng thái sau refresh và có history.

Issue tham chiếu: [OrangeHRM #1926](https://github.com/orangehrm/orangehrm/issues/1926). Báo cáo gốc thuộc **5.8 demo**, mô tả lỗi 500 khi Shortlist vacancy **Senior QA Lead** và dùng **Payroll Administrator** làm đối chứng. Local hiện có ba vacancy Active: **Devops, Sales IT, Tester**; không có hai vacancy của báo cáo gốc.

Vì dữ liệu và version khác nhau, kết quả local không đủ để kết luận issue gốc đã được sửa. Test dưới đây kiểm tra cùng thao tác Shortlist với các vacancy thực tế đang có; không tự tạo vacancy giả để thay thế dữ liệu gốc.

## Chẩn đoán bằng giao diện

1. Đăng nhập Recruitment bằng config hiện có.
2. Tạo candidate riêng với marker `ISS1926_1792336258332`, chọn vacancy **Devops**.
3. Xác nhận trạng thái ban đầu là **Application Initiated**.
4. Click **Shortlist**, nhập Notes rồi **Save**.
5. Kiểm tra trạng thái **Shortlisted**; refresh và kiểm tra lại trạng thái cùng history.
6. Xóa đúng candidate có marker của phép thử bằng UI.

| Quan sát | Expected | Actual |
|---|---|---|
| Trước Shortlist | Application Initiated | Application Initiated |
| Sau Shortlist và Save | Shortlisted | Shortlisted |
| Sau refresh | Vẫn Shortlisted | Vẫn Shortlisted |
| History | Có thao tác Shortlisted | Có “Shortlisted for Devops” |

Chẩn đoán hoàn tất lúc **22:12:57 ngày 18/10/2026 (UTC+7)**. Cleanup bằng UI lúc **22:12:59**, chỉ xóa candidate test; không sửa candidate hoặc vacancy có sẵn.

Bằng chứng: [vacancy local](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1926-20261018/vacancies-before.png), [candidate trước thao tác](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1926-20261018/own-candidate-before.png), [sau refresh](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1926-20261018/manual-after-refresh.png), [kết quả JSON](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1926-20261018/manual-results.json), [cleanup JSON](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1926-20261018/manual-cleanup.json).

## Regression đơn giản, tái sử dụng code hiện có

[Issue1926RegressionTest.java](E:/Studying/GraduateProject/orangehrmtesting/src/test/java/com/hrm/TestCase/Recruitment/Issue1926RegressionTest.java) extends `TestBase`, có **một method test** và `DataProvider` chạy lần lượt ba vacancy từ `TestConfig`: Devops, Sales IT, Tester.

- Đăng nhập bằng `TestUtil.recruitmentUtil()`.
- Tạo candidate bằng hàm điền form đầy đủ của `AddCandidatePage` hiện có; mỗi lần chạy có marker và email riêng.
- Mở rộng `ApplicationStage` hiện có để Shortlist, đọc trạng thái, refresh và kiểm tra history.
- Dùng `componentLocator` cho điền Notes, Save, kiểm tra bảng và xóa candidate; click dùng `waitUtils` hiện có.
- Log4j ghi từng bước và vacancy; Allure lưu screenshot sau khi xác nhận Shortlisted. Listener hiện có chụp ảnh khi FAIL.
- `@AfterMethod` xóa đúng candidate có marker của lần test, kể cả khi assertion FAIL. Không xóa candidate mẫu hoặc vacancy có sẵn.

Không tạo framework, service hoặc Page Object thay thế cho các trang đã có. Không dùng Rest Assured, API hoặc database để thao tác dữ liệu.

## Giải thích cho Junior

- **Input:** Một vacancy thật từ config và thông tin candidate test riêng. Cùng logic test được chạy lại với ba vacancy, không chép ba method giống nhau.
- **Vì sao kiểm tra Application Initiated trước?** Đảm bảo candidate mới chưa được Shortlist từ trước, để kết quả cuối phản ánh thao tác đang kiểm tra.
- **Vì sao phải Save?** Click Shortlist mới mở form; Save mới gửi thao tác chuyển trạng thái qua UI.
- **Vì sao kiểm tra sau refresh?** Nếu chỉ nhìn trạng thái ngay sau Save, có thể chưa biết thay đổi đã được lưu. Refresh buộc trang tải lại dữ liệu.
- **Vì sao kiểm tra history?** Xác nhận hệ thống ghi nhận thao tác Shortlisted cùng với trạng thái hiện tại.
- **PASS:** Với vacancy của lần chạy, candidate chuyển sang Shortlisted, giữ trạng thái sau refresh và có history.
- **FAIL:** Đọc log để biết lỗi xảy ra ở setup, thao tác Shortlist hay assertion sau refresh. Không tự gọi mọi lỗi là issue #1926.
- **Giới hạn:** Đây là kiểm tra UI; không đo HTTP status. Không kết luận “API trả 500” từ một assertion UI FAIL. PASS trên ba vacancy local cũng không chứng minh issue gốc đã hết trên mọi dữ liệu.

## Thời gian chờ và lần chạy setup đầu

Sau đăng nhập, chỉ class regression này đặt implicit wait về `0`; các bước cần tải trang dùng điều kiện chờ tối đa **5 giây**, đạt điều kiện thì chạy tiếp ngay. Không dùng `Thread.sleep` và không đổi thời gian chờ chung của framework.

Lần chạy đầu có ba lỗi setup vì nút Add chưa render khi implicit wait bằng 0; chưa tạo candidate. Đã thêm điều kiện chờ nút Add và form Add Candidate trong `CandidatesPage.openAddCandidatePage()`. Đây là sửa đồng bộ thao tác Selenium, không đổi expected hoặc assertion để lấy PASS.

## Chạy suite và kết quả TestNG

[testng_issue1926.xml](E:/Studying/GraduateProject/orangehrmtesting/testng_issue1926.xml) chỉ chạy regression này với listener hiện có.

```powershell
mvn -o "-Dtestng.suite=testng_issue1926.xml" test
```

Lần chạy sau khi sửa chờ setup hoàn tất lúc **22:18:49 ngày 18/10/2026 (UTC+7)**:

- Compile trước khi chạy: **BUILD SUCCESS**, lúc **22:15:39**, mất **3,926 giây**.
- TestNG: **3 PASS, 0 FAIL, 0 error, 0 skip**; suite khoảng **58,79 giây**.
- Maven: **BUILD SUCCESS**, tổng thời gian khoảng **1 phút 1 giây**; source đã compile nên không cần compile lại.

| Vacancy | Sau Save | Sau refresh | History | Cleanup candidate riêng | Kết quả |
|---|---|---|---|---|---|
| Devops | Shortlisted | Shortlisted | Có Shortlisted | Passed | **PASS** |
| Sales IT | Shortlisted | Shortlisted | Có Shortlisted | Passed | **PASS** |
| Tester | Shortlisted | Shortlisted | Có Shortlisted | Passed | **PASS** |

Kiểm tra độc lập sau cleanup bằng DB chỉ đọc xác nhận **0 candidate có marker ISS1926**, ba candidate gốc giữ nguyên trạng thái và ba vacancy vẫn Active với Hiring Manager ban đầu. Toàn bộ thao tác tạo, Shortlist và xóa trong testcase đều qua Selenium UI.

**Chưa tái hiện #1926 trên ba vacancy local được kiểm tra.** Không có Senior QA Lead và Payroll Administrator của báo cáo gốc nên không kết luận bug đã được sửa. Giữ nguyên expected Shortlisted và history; không đổi assertion để làm test PASS.

Bằng chứng TestNG: [kết quả JSON](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1926-20261018/regression-results.json), [Devops sau refresh](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1926-20261018/regression-Devops.png), [Sales IT sau refresh](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1926-20261018/regression-Sales-IT.png), [Tester sau refresh](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1926-20261018/regression-Tester.png).
