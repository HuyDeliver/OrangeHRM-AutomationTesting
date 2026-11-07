package com.hrm.Base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.hrm.Util.Config;
import com.hrm.Util.Log;
import com.hrm.Util.OAuthTokenManager;
import com.hrm.Util.WebDriverFactory;

import io.restassured.RestAssured;

public class TestBaseAPI {

    protected WebDriver driver;
    protected String token;

    @BeforeClass
    public void setupAPI() {

        // API tự động lấy host/port từ app.url trong config
        RestAssured.baseURI = OAuthTokenManager.getBaseUrl();
        RestAssured.basePath = "/web/index.php";

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Dùng đúng WebDriverFactory hiện tại của project
        driver = WebDriverFactory.createDriver();

        // Tự lấy Bearer Token
        token = OAuthTokenManager.getAccessToken(
                driver,
                Config.get("username"),
                Config.get("password"));

        Log.info("===== API SETUP =====");
        Log.info("Bearer token created successfully");
    }

    @AfterClass(alwaysRun = true)
    public void closeBrowser() {
        if (driver != null)
            driver.quit();
    }
}
