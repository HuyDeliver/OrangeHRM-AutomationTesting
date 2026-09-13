package com.hrm.Util;

import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class componentLocator {
    public static void fillInput(WebDriver driver, String label, String value) {
        Log.info("Nhập " + label);

        By inputLocator = By.xpath("//label[text()='" + label + "']/following::input[1]");

        WebElement input = driver.findElement(inputLocator);
        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));

        input.sendKeys(Keys.BACK_SPACE);

        if (value != null && !value.isEmpty()) {
            input.sendKeys(value);
        }
        input.sendKeys(Keys.TAB);
    }

    public static void fillFile(WebDriver driver, String label, String value) {
        Log.info("Nhập " + label);

        By inputLocator = By.xpath("//label[text()='" + label + "']/following::input[@type='file'][1]");

        WebElement input = driver.findElement(inputLocator);
        input.sendKeys(value);
    }

    public static void fillTexArea(WebDriver driver, String label, String value) {
        Log.info("Nhập " + label);

        By textLocator = By.xpath("//label[text()='" + label + "']/following::textarea[1]");

        WebElement input = driver.findElement(textLocator);
        input.clear();
        input.sendKeys(value);
    }

    public static void buttonSave(WebDriver driver) {
        Log.info("Click save");

        By buttonLocator = By.xpath("//div[@class='oxd-form-actions']/child::button[@type='submit']");

        WebElement button = driver.findElement(buttonLocator);
        waitUtils.waitElementClick(driver, button);
    }

    public static boolean checkTitle(WebDriver driver, String title) {
        Log.info("Kiểm tra đã vào được " + title + "Hay chưa");

        By webTitle = By.cssSelector(".orangehrm-card-container>h6");

        return driver.findElement(webTitle).isDisplayed();
    }

    public static boolean checkToasstSuccess(WebDriver driver, String toast) {
        Log.info("Kiểm tra " + toast + " thành công hay chưa");

        By toastSuccess = By.cssSelector(".oxd-toast.oxd-toast--success.oxd-toast-container--toast");
        return driver.findElement(toastSuccess).isDisplayed();
    }

    public static WebElement editButtonTable(WebDriver driver) {
        List<WebElement> rows = driver.findElements(By.cssSelector(".oxd-table-card"));

        int randomIndex = new Random().nextInt(rows.size());
        WebElement randomRow = rows.get(randomIndex);

        return randomRow.findElement(By.xpath(".//button[i[contains(@class,'bi-pencil')]]"));
    }

    public static WebElement deleteButtonTable(WebDriver driver) {
        List<WebElement> rows = driver.findElements(By.cssSelector(".oxd-table-card"));

        int randomIndex = new Random().nextInt(rows.size());
        WebElement randomRow = rows.get(randomIndex);

        return randomRow.findElement(By.xpath(".//button[i[contains(@class,'bi-trash')]]"));
    }
}
