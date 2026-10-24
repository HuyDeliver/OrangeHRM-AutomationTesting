package com.hrm.TestCase.ClaimTab;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.ClaimPage.ClaimPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("Claim")
@Feature("Attachments")
public class Issue1820RegressionTest extends TestBase {
    private ClaimPage claimPage;
    private String comment;
    private boolean draftReady;

    @Test(description = "OHR50: File đính kèm Claim phải được lưu sau Save và refresh")
    public void uploadedClaimAttachmentShouldRemainAfterRefresh() throws IOException {
        // Arrange
        draftReady = false;
        Log.info("Bước 1: Đăng nhập Claim và mở Claim nháp riêng");
        TestUtil.claimUtil();
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        claimPage = new ClaimPage(driver);
        claimPage.openTestClaim(TestConfig.claimTestRemarks);
        Assert.assertEquals(claimPage.getRemarks(), TestConfig.claimTestRemarks, "Đang mở sai Claim fixture");
        Assert.assertEquals(claimPage.getField("Event"), TestConfig.claimTestEvent, "Sai Event của fixture");
        Assert.assertEquals(claimPage.getField("Currency"), TestConfig.claimTestCurrency, "Sai Currency của fixture");
        Assert.assertEquals(claimPage.getField("Status"), "Initiated", "Chỉ kiểm tra Claim còn ở trạng thái nháp");
        Assert.assertEquals(claimPage.getAttachmentCount(), 0, "Setup lỗi: fixture phải chưa có attachment");
        draftReady = true;

        Path file = Path.of(TestConfig.jobSpecification);
        Assert.assertTrue(Files.isRegularFile(file), "Không tìm thấy file thử: " + file);
        Assert.assertTrue(Files.size(file) > 0 && Files.size(file) <= 1_048_576, "File thử phải có dữ liệu và không quá 1MB");
        comment = "ISS1820_FILE_" + System.currentTimeMillis();
        Log.info("Bước 2: Reference Id=" + claimPage.getField("Reference Id")
                + " | File=" + file.getFileName() + " | Bytes=" + Files.size(file) + " | Comment=" + comment);

        // Act
        Log.info("Bước 3: Add Attachments, chọn file, nhập Comment rồi Save");
        claimPage.uploadAttachment(file.toString(), comment);
        Assert.assertFalse(claimPage.hasUploadError(), "Upload Claim báo lỗi trên UI; kiểm tra ảnh Allure");

        // Assert
        Log.info("Bước 4: Refresh rồi tìm đúng tên file và Comment vừa nhập");
        claimPage.reload();
        boolean saved = claimPage.hasAttachment(file.getFileName().toString(), comment);
        Log.info("Expected: file vẫn tồn tại sau refresh | Actual: saved=" + saved);
        claimPage.showAttachments();
        takeScreenshot("Claim attachment after refresh");
        Assert.assertTrue(saved, "File đính kèm không được lưu sau Save và refresh - kiểm tra issue 1820");
        Assert.assertEquals(claimPage.getAttachmentCount(), 1, "Phải có đúng một file vừa upload");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTestAttachment() {
        if (!draftReady || comment == null) return;
        Log.info("Cleanup: mở lại đúng Claim nháp và chỉ xóa file có Comment=" + comment);
        claimPage.openTestClaim(TestConfig.claimTestRemarks);
        Assert.assertEquals(claimPage.getRemarks(), TestConfig.claimTestRemarks, "Dừng cleanup: sai Claim fixture");
        Assert.assertEquals(claimPage.getField("Status"), "Initiated", "Dừng cleanup: Claim không còn là nháp");
        claimPage.deleteTestAttachment(comment);
        Assert.assertEquals(claimPage.getAttachmentCount(), 0, "Cleanup chưa thành công: attachment vẫn còn");
        Log.info("Cleanup PASS: không còn file thử; giữ Event và Claim nháp đã được phép");
    }
}
