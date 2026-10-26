# Chạy test theo module

Ngày gom module: 26/10/2026.
Repository chỉ dùng một file testng.xml. Chọn module bằng tham số Maven module.

```powershell
mvn test -Dmodule=PIM
mvn test -Dmodule=Recruitment
mvn test "-Dmodule=Admin,PIM"
```

## Vị trí test

| Module | Package | Method @Test |
|---|---|---:|
| Login | com.hrm.TestCase.LoginTab | 3 |
| Admin | com.hrm.TestCase.AdminTab | 16 |
| PIM | com.hrm.TestCase.PIMTab | 8 |
| Recruitment | com.hrm.TestCase.Recruitment | 7 |
| MyInfo | com.hrm.TestCase.MyInfoTab | 7 |
| Performance | com.hrm.TestCase.PerformanceTab | 3 |
| Profile | com.hrm.TestCase.ProfileTab | 2 |
| Time | com.hrm.TestCase.TimeTab | 3 |
| Claim | com.hrm.TestCase.ClaimTab | 1 |
| PIMAPI | com.hrm.TestCase.TestAPI.PIMAPI | 7 |
| AdminAPI | com.hrm.TestCase.TestAPI.AdminAPI | 1 |
| RecruitmentAPI | com.hrm.TestCase.TestAPI.RecruitmentAPI | 3 |
| Security | com.hrm.TestCase.TestSecurity | 5 |

Tổng 57 class có test, 66 method. DataProvider mở rộng thành nhiều lượt chạy.
Class APIGetEmployeeListTest đang là placeholder không có @Test nên không đưa vào suite.

## Search, assertion và issue

- OHR1 thuộc Login.
- OHR4 và OHR15 thuộc Admin; issue #1897 ở cùng folder AdminTab.
- OHR23, OHR25 và issue #1876 thuộc PIM.
- OHR27, OHR30 và issue #1926 thuộc Recruitment.
- Issue #1928 thuộc MyInfo.
- Issue #1923 thuộc Profile; #1787/#1890 thuộc Time; #1820 thuộc Claim.

Không còn suite XML riêng cho issue, Search hoặc nhóm assertion.
Class được liệt kê trong module để giữ thứ tự và dependency của các flow Add/Edit/Delete hiện có.
Một class chỉ xuất hiện một lần trong file XML. TestListener được dùng chung; không đăng ký RetryListener.

## Fixture khi chạy chung

OHR25 dùng employee ID có tiền tố T, tách với employee của flow PIM chính.
Reject và Delete Candidate có tiền tố REJECT_/DELETE_; Add Candidate cleanup bản ghi của chính mình.
Thứ tự class và dữ liệu riêng giúp tránh thao tác nhầm giữa các test của cùng module.
Các fixture Time/Claim đã được giữ theo phê duyệt vẫn cần đúng tiền điều kiện trước khi chạy.

## Bằng chứng cũ và CI/CD

21 XML riêng đã gộp về testng.xml. Không sửa command/fullName trong JSON bằng chứng lịch sử.
Đường dẫn/class cũ trong báo cáo vẫn thuộc lần chạy trước khi gom; xem README để tra đường dẫn hiện tại.
Một số module chứa regression đang tái hiện bug, nên không mặc định mọi module phải PASS.

Kiểm tra cấu trúc đã xác nhận đủ OHR1–66, không trùng class, không thiếu group dependency.
Đã xác minh tên module không tồn tại làm build FAIL thay vì chạy nhầm hoặc báo PASS khi không có test.
Kết quả kiểm tra ngày 26/10/2026 trên môi trường local:

| Kiểm tra | Kết quả |
|---|---|
| `mvn -o -DskipTests test-compile` | BUILD SUCCESS |
| Danh mục test | 57 class, 66 method; giữ đủ OHR1–66 |
| `mvn -o -Dmodule=DOES_NOT_EXIST test` | Bị từ chối đúng như mong đợi, không chạy test |
| `mvn -o -Dmodule=Recruitment test` | 12 PASS, 0 FAIL, 0 SKIP |
| `mvn -o -Dmodule=RecruitmentAPI test` | 8 PASS, 0 FAIL, 0 SKIP |

Hai module trên đã chạy thực tế, bao gồm các lượt DataProvider. Chưa chạy lại toàn bộ 13 module sau khi gom.

Workflow CI và cách cấu hình runner, config, browser được mô tả tại [GitHub Actions](github-actions.md). Database/fixture local vẫn phải đáp ứng tiền điều kiện của module.
