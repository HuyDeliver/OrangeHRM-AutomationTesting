package com.hrm.Base;

import org.openqa.selenium.WebDriver;
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

        // API chạy trên OrangeHRM local
        RestAssured.baseURI = "http://localhost:8080";
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
}