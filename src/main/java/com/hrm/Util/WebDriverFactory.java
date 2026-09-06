package com.hrm.Util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebDriverFactory {
    public static WebDriver createDriver() {
        String browser = Config.get("browser");

        switch (browser.toLowerCase()) {
            case "firefox":
                return new FirefoxDriver();
            case "chrome":
            default:
                return new ChromeDriver();
        }
    }
}
