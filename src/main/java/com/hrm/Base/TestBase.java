package com.hrm.Base;

import java.io.ByteArrayInputStream;

import java.time.Duration;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.hrm.Util.Config;
import com.hrm.Util.WebDriverFactory;

import io.qameta.allure.Allure;

public class TestBase {
    public static WebDriver driver;

    @BeforeMethod
    public void setUp() {
        if (Boolean.getBoolean("cd.run") && !"http://localhost:8081/web/index.php/auth/login".equals(Config.get("app.url"))) {
            throw new IllegalStateException("CD profile chỉ được chạy trên CD 8081");
        }
        driver = WebDriverFactory.createDriver();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", System.getenv("HEADLESS")));
        if (headless) {
            driver.manage().window().setSize(new Dimension(1920, 1080));
        } else {
            driver.manage().window().maximize();
        }
        driver.get(Config.get("app.url"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    public void takeScreenshot(String testName) {
        // Chụp dạng byte[] thay vì FILE
        byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        // Đính thẳng vào report Allure
        Allure.addAttachment("Screenshot - " + testName, "image/png", new ByteArrayInputStream(screenshotBytes), ".png");
    }
}
