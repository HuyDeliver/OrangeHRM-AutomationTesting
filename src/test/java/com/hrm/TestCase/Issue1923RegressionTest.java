package com.hrm.TestCase;

import java.time.Duration;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.ProfileMenuPage;
import com.hrm.Util.Log;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("Common UI")
@Feature("Issue #1923 - Profile dropdown")
public class Issue1923RegressionTest extends TestBase {
    private ProfileMenuPage profileMenu;

    @BeforeMethod
    public void loginAndOpenProfileMenu() {
        // Arrange
        Log.info("Bước 1: Đăng nhập bằng TestUtil hiện có");
        TestUtil.userUtil();
        // Chỉ áp dụng cho test này: không mất 10 giây tìm menu đã biến mất.
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        profileMenu = new ProfileMenuPage(driver);

        Log.info("Bước 2: Click avatar lần đầu để mở menu");
        profileMenu.clickAvatar();
        Assert.assertTrue(profileMenu.isMenuOpen(), "Setup lỗi: menu chưa mở sau click avatar lần đầu");
        Log.info("Đã xác nhận menu đang mở");
    }

    @Test(description = "OHR44: Issue #1923 - Click lại avatar phải đóng menu")
    public void clickingAvatarAgainShouldCloseProfileMenu() {
        // Act
        Log.info("Bước 3: Click lại đúng avatar để đóng menu");
        profileMenu.clickAvatar();

        // Assert
        boolean closed = profileMenu.isMenuClosed();
        Log.info("Expected: menu đóng | Actual menu đã đóng: " + closed);
        Assert.assertTrue(closed, "Bug #1923: menu vẫn mở sau khi click avatar lần hai");
    }

    @Test(description = "OHR45: Issue #1923 control - Click ra ngoài phải đóng menu")
    public void clickingOutsideShouldCloseProfileMenu() {
        // Act
        Log.info("Bước 3: Click tiêu đề trang bên ngoài menu (control)");
        profileMenu.clickOutsideMenu();

        // Assert
        boolean closed = profileMenu.isMenuClosed();
        Log.info("Expected: menu đóng | Actual menu đã đóng: " + closed);
        Assert.assertTrue(closed, "Control lỗi: menu vẫn mở sau khi click ra ngoài");
    }
}
