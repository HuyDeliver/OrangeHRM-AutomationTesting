package com.hrm.Util;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(Keys.BACK_SPACE);
        if (value != null && !value.isEmpty()) {
            input.sendKeys(value);
        }
        input.sendKeys(Keys.TAB);
    }

    public static void buttonSave(WebDriver driver) {
        Log.info("Click save");

        By buttonLocator = By.xpath("//div[@class='oxd-form-actions']/child::button[@type='submit']");

        WebElement button = driver.findElement(buttonLocator);
        waitUtils.waitElementClick(driver, button);
    }

    public static boolean checkTitle(WebDriver driver, String title) {
        Log.info("Kiểm tra đã vào được " + title + " Hay chưa");

        WebElement webTitle = driver
                .findElement(
                        By.cssSelector(".oxd-text.oxd-text--h6.orangehrm-main-title"));
        return new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(webTitle))
                .isDisplayed();
    }

    public static boolean checkToasstSuccess(WebDriver driver, String toast) {
        Log.info("Kiểm tra " + toast + " thành công hay chưa");
        WebElement toastSuccess = driver
                .findElement(By.cssSelector(".oxd-toast.oxd-toast--success.oxd-toast-container--toast"));
        return new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(toastSuccess))
                .isDisplayed();
    }

    public static void editButtonTable(WebDriver driver) {
        List<WebElement> rows = driver.findElements(By.cssSelector(".oxd-table-card"));

        int randomIndex = new Random().nextInt(rows.size());
        WebElement randomRow = rows.get(randomIndex);

        WebElement click = randomRow.findElement(By.xpath(".//button[i[contains(@class,'bi-pencil')]]"));
        waitUtils.waitElementClick(driver, click);
    }

    public static void deleteButtonTable(WebDriver driver) {
        List<WebElement> rows = driver.findElements(By.cssSelector(".oxd-table-card"));

        int randomIndex = new Random().nextInt(rows.size());
        WebElement randomRow = rows.get(randomIndex);

        WebElement deleteItem = randomRow.findElement(By.xpath(".//button[i[contains(@class,'bi-trash')]]"));
        waitUtils.waitElementClick(driver, deleteItem);
        By deleteModal = By
                .cssSelector(".oxd-dialog-container-default .oxd-sheet");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(deleteModal));

        Log.info("Xác nhận xóa");
        WebElement confirmDelete = driver.findElement(By.cssSelector(".orangehrm-modal-footer>button:nth-child(2)"));
        waitUtils.waitElementClick(driver, confirmDelete);
    }

    public static void chooseSelect(WebDriver driver, String label, String option) {
        Log.info("Chọn " + label + " = " + option);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        By selectInput = By.xpath("//label[text()='" + label + "']/following::div[contains(@class,'oxd-select-text')]");

        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(selectInput));
        js.executeScript("arguments[0].click();", input);

        By listbox = By.xpath("//div[@role='listbox']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(listbox));

        By optionLocator = By.xpath("//div[@role='listbox']//span[normalize-space()='" + option + "']");
        WebElement optionEl = wait.until(ExpectedConditions.presenceOfElementLocated(optionLocator));
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", optionEl);

        wait.until(ExpectedConditions.elementToBeClickable(optionEl)).click();

    }

    public static void clickDropdownNavi(WebDriver driver, String nameNavi, String option) {
        Log.info("Click vào: " + nameNavi);
        By navi = By.xpath("//li[contains(@class, 'oxd-topbar-body-nav-tab') and contains(., '" + nameNavi + "')]");

        waitUtils.waitElementClick(driver, driver.findElement(navi));

        By dropdown = By.cssSelector(".oxd-topbar-body-nav-tab .oxd-dropdown-menu");
        waitUtils.waitElementVisibility(driver, driver.findElement(dropdown));

        Log.info("Chọn: " + option);
        By optionChoose = By.xpath("//a[@class='oxd-topbar-body-nav-tab-link' and contains(.,'" + option + "')]");
        waitUtils.waitElementClick(driver, driver.findElement(optionChoose));
    }

    public static void clickButtonAdd(WebDriver driver) {
        By button = By.cssSelector(".orangehrm-paper-container .orangehrm-header-container button");
        waitUtils.waitElementClick(driver, driver.findElement(button));
    }

    public static void clickSearch(WebDriver driver) {
        Log.info("Click Search");
        WebElement buttonSearch = driver
                .findElement(By.xpath("//div[@class='oxd-form-actions']/child::button[@type='submit']"));
        waitUtils.waitElementClick(driver, buttonSearch);
    }

    public static boolean checkSearch(WebDriver driver, String nameSearch, String keyword) {
        Log.info("Kiểm tra " + nameSearch + " :" + keyword + " có tồn tại");

        List<WebElement> rows = driver.findElements(By.cssSelector(".oxd-table-card"));

        if (!rows.isEmpty()) {
            return rows.stream().anyMatch(row -> row.getText().contains(keyword));
        }

        return false;
    }
}
