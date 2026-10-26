package com.hrm.Util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class WebDriverFactory {
    public static WebDriver createDriver() {
        String browser = Config.get("browser");
        boolean headless = Boolean.parseBoolean(
                System.getProperty("headless", System.getenv("HEADLESS")));

        switch (browser.toLowerCase()) {
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("-headless", "--width=1920", "--height=1080");
                }
                return new FirefoxDriver(firefoxOptions);
            case "chrome":
            default:
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless=new", "--window-size=1920,1080");
                }
                return new ChromeDriver(chromeOptions);
        }
    }
}
