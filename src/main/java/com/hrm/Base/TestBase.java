package com.hrm.Base;

import java.io.ByteArrayInputStream;

import java.time.Duration;

import org.openqa.selenium.OutputType;
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
        driver = WebDriverFactory.createDriver();
        driver.manage().window().maximize();
        driver.get(Config.get("app.url"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    public void takeScreenshot(String testName) {
        // Chụp dạng byte[] thay vì FILE
        byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        // Đính thẳng vào report Allure
        Allure.addAttachment("Screenshot - " + testName, new ByteArrayInputStream(screenshotBytes));
    }
}
