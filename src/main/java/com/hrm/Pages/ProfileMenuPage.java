package com.hrm.Pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.waitUtils;

public class ProfileMenuPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final By menu = By.cssSelector(".oxd-userdropdown [role='menu']");

    @FindBy(css = ".oxd-userdropdown-img")
    private WebElement avatar;

    @FindBy(css = ".oxd-topbar-header-breadcrumb-module")
    private WebElement pageTitle;

    public ProfileMenuPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        PageFactory.initElements(driver, this);
    }

    public void clickAvatar() {
        waitUtils.waitElementClick(driver, avatar);
    }

    public void clickOutsideMenu() {
        waitUtils.waitElementClick(driver, pageTitle);
    }

    public boolean isMenuOpen() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(menu));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isMenuClosed() {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(menu));
        } catch (TimeoutException e) {
            return false;
        }
    }
}
