# GitHub Actions

Workflow hiện tại: `.github/workflows/orangehrm-ci.yml`.

Push/merge main → CI compile → deploy tự động OrangeHRM CD tại 8081 → readiness → OHR1 Login smoke headless → report.

Pull Request chỉ compile. Run workflow trên main chạy lại cùng pipeline, không còn chọn module hoặc checkbox Deploy ở phase đầu.

Xem [hướng dẫn CI/CD đầy đủ](docker-cd.md) để khởi tạo một lần, đặt hai Secret CD, cấu hình runner và đọc kết quả. Lệnh chạy module local trong [test-execution.md](test-execution.md) vẫn giữ nguyên.

Report đầy đủ giữ trên runner. GitHub chỉ nhận summary PASS/FAIL/SKIP không chứa config, password, token hay raw response.
