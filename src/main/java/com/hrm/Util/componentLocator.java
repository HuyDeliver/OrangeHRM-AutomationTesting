package com.hrm.Util;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.qameta.allure.Allure;

public class componentLocator {

    public static void clickSideBar(WebDriver driver, String tab) {
        Log.info("Click vào tab " + tab);
        WebElement Tab = driver
                .findElement(By.xpath("//ul[@class='oxd-main-menu']/child::li[contains(.,'" + tab + "')]"));
        waitUtils.waitElementClick(driver, Tab);
    }

    public static void fillInput(WebDriver driver, String label, String value) {
        Log.info("Nhập " + label + "=" + value);

        By inputLocator = By.xpath("//label[contains(text(),'" + label + "')]/following::input[1]");

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
        Log.info("Nhập " + label + "=" + value);

        By inputLocator = By.xpath("//label[contains(text(),'" + label + "')]/following::input[@type='file'][1]");

        WebElement input = driver.findElement(inputLocator);
        input.sendKeys(value);
    }

    public static void fillTexArea(WebDriver driver, String label, String value) {
        Log.info("Nhập " + label + "=" + value);

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

    public static boolean checkTitleH6(WebDriver driver, String title) {
        Log.info("Kiểm tra đã vào được " + title + " Hay chưa");

        WebElement webTitle = driver
                .findElement(
                        By.cssSelector(".oxd-text.oxd-text--h6.orangehrm-main-title"));
        return new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(webTitle))
                .isDisplayed();
    }

    public static boolean checkTitleH5(WebDriver driver, String title) {
        Log.info("Kiểm tra đã vào được " + title + " Hay chưa");

        WebElement webTitle = driver
                .findElement(
                        By.cssSelector(".oxd-text.oxd-text--h5.oxd-table-filter-title"));
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

    public static void editButtonTable(WebDriver driver, String input) {
        List<WebElement> rows = driver.findElements(By.cssSelector(".oxd-table-card"));

        WebElement edit = rows.stream().filter(e -> e.getText().contains(input)).findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy " + input))
                .findElement(By.xpath(".//button[i[contains(@class,'bi-pencil')]]"));
        waitUtils.waitElementClick(driver, edit);
    }

    public static void deleteButtonTable(WebDriver driver, String input) {
        Log.info("Chọn xóa: " + input);
        List<WebElement> rows = driver.findElements(By.cssSelector(".oxd-table-card"));
        WebElement delete = rows.stream().filter(e -> e.getText().contains(input)).findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy " + input))
                .findElement(By.xpath(".//button[i[contains(@class,'bi-trash')]]"));
        waitUtils.waitElementClick(driver, delete);
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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Actions actions = new Actions(driver);

        By selectInput = By.xpath("//label[text()='" + label + "']/following::div[contains(@class,'oxd-select-text')]");

        if (option != null && !option.trim().isEmpty()) {

            WebElement input = wait.until(ExpectedConditions.elementToBeClickable(selectInput));
            js.executeScript("arguments[0].click();", input);

            By listbox = By.xpath("//div[@role='listbox']");
            wait.until(ExpectedConditions.visibilityOfElementLocated(listbox));

            By optionLocator = By
                    .xpath("//div[@role='listbox' and contains(@class,'oxd-select-dropdown')]//span[normalize-space(.)='"
                            + option + "']");
            WebElement optionEl = wait.until(ExpectedConditions.presenceOfElementLocated(optionLocator));
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", optionEl);

            wait.until(ExpectedConditions.elementToBeClickable(optionEl)).click();

        } else {
            actions.sendKeys(Keys.TAB).perform();
        }
    }

    public static void clickDropdownNavi(WebDriver driver, String nameNavi, String option) {
        Log.info("Click vào: " + nameNavi);
        By navi = By.xpath("//li[contains(@class, 'oxd-topbar-body-nav-tab') and contains(., '" + nameNavi + "')]");

        waitUtils.waitElementClick(driver, driver.findElement(navi));

        if (!option.isEmpty() && option != null) {
            By dropdown = By.cssSelector(".oxd-topbar-body-nav-tab .oxd-dropdown-menu");
            waitUtils.waitElementVisibility(driver, driver.findElement(dropdown));
            Log.info("Chọn: " + option);
            By optionChoose = By.xpath("//a[@class='oxd-topbar-body-nav-tab-link' and contains(.,'" + option + "')]");
            waitUtils.waitElementClick(driver, driver.findElement(optionChoose));
        }
    }

    public static void clickSideBarMyInfo(WebDriver driver, String tab) {
        Log.info("Click vào tab " + tab);
        WebElement Tab = driver
                .findElement(By.xpath("//div[@class='orangehrm-tabs']/child::div[contains(.,'" + tab + "')]"));
        waitUtils.waitElementClick(driver, Tab);
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

    public static boolean checkSearch(WebDriver driver, String nameSearch, String name, String city, String country) {
        Log.info(
                String.format("Kiểm tra [%s] với Name='%s', City='%s', Country='%s'", nameSearch, name, city, country));

        List<WebElement> rows = driver.findElements(By.cssSelector(".oxd-table-card"));

        if (rows.isEmpty()) {
            Log.warn("Bảng kết quả rỗng!");
            return false;
        }

        return rows.stream().anyMatch(row -> {

            String rowText = row.getText();

            boolean matchName = (name == null || name.trim().isEmpty()) || rowText.contains(name.trim());
            boolean matchCity = (city == null || city.trim().isEmpty()) || rowText.contains(city.trim());
            boolean matchCountry = (country == null || country.trim().isEmpty()) || rowText.contains(country.trim());

            return matchName && matchCity && matchCountry;
        });
    }

    public static boolean checkTable(WebDriver driver, String nameCheck, Map<String, String> criteria) {
        Log.info(String.format("Verify [%s] có chứa keyword: %s", nameCheck, criteria));

        List<WebElement> rows = driver.findElements(By.cssSelector(".oxd-table-card"));

        if (rows.isEmpty()) {
            Log.warn("Bảng kết quả rỗng!");
            return false;
        }

        // Lọc lấy danh sách các value không null/rỗng
        List<String> validValues = criteria.values().stream()
                .filter(val -> val != null && !val.trim().isEmpty())
                .map(String::trim)
                .collect(Collectors.toList());

        if (validValues.isEmpty())
            return true;

        return rows.stream().anyMatch(row -> {
            String rowText = row.getText();
            return validValues.stream().allMatch(rowText::contains);
        });
    }

    public static void chooseSelectAction(WebDriver driver, String label, String option) {
        if (option == null || option.trim().isEmpty()) {
            return;
        }
        Log.info("Chọn " + label + " = " + option);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Actions actions = new Actions(driver);

        By dropdownLocator = By.xpath("//label[normalize-space()='" + label
                + "']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text')]");

        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownLocator));

        actions.moveToElement(dropdown).click().perform();

        By optionLocator = By
                .xpath("//div[@role='listbox' and contains(@class,'oxd-select-dropdown')]//span[normalize-space(.)='"
                        + option + "']");
        WebElement optionEl = wait.until(ExpectedConditions.visibilityOfElementLocated(optionLocator));

        actions.moveToElement(optionEl).scrollToElement(optionEl).click().perform();
    }

    public static void buttonAddMyInfo(WebDriver driver, String title) {
        WebElement buttonAdd = driver
                .findElement(By.xpath("//h6[normalize-space()='" + title + "']/following-sibling::button"));
        waitUtils.waitElementClick(driver, buttonAdd);
    }

    public static void inputDropdown(WebDriver driver, String label, String keyword, String option) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }
        Log.info("Chọn " + label + " = " + keyword + " và chọn " + option);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        By dropdownInput = By.xpath("//label[normalize-space()='" + label
                + "']/ancestor::div[contains(@class,'oxd-input-group')]//input[1]");
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownInput));

        dropdown.sendKeys(Keys.CONTROL + "a");
        dropdown.sendKeys(Keys.BACK_SPACE);
        dropdown.sendKeys(keyword);

        By optionLocator = By
                .xpath("//div[@role='listbox' and contains(@class,'oxd-autocomplete-dropdown')]//span[normalize-space()='"
                        + option + "']");
        WebElement optionEl = wait.until(ExpectedConditions.visibilityOfElementLocated(optionLocator));
        optionEl.click();

        try {
            wait.until(ExpectedConditions.invisibilityOf(optionEl));
        } catch (Exception ignored) {
        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException ignored) {
        }
    }

    public static void viewButtonTable(WebDriver driver, String input) {
        Log.info("Chọn xem: " + input);
        List<WebElement> rows = driver.findElements(By.cssSelector(".oxd-table-card"));
        WebElement view = rows.stream().filter(e -> e.getText().contains(input)).findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy " + input))
                .findElement(By.xpath(".//button[i[contains(@class,'bi-eye-fill')]]"));
        waitUtils.waitElementClick(driver, view);
    }

    public static void takeScreenshotTable(WebDriver driver, String testName) {
        Log.info("Chụp ảnh kết quả");
        WebElement record = driver.findElement(
                By.xpath("//div[contains(@class,'orangehrm-horizontal-padding')]/child::span"));

        // Cuộn phần tử vào giữa màn hình
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "arguments[0].scrollIntoView({behavior: 'auto', block: 'center'});",
                record);

        byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment("Screenshot - " + testName, new ByteArrayInputStream(screenshotBytes));
    }

    public static void takeScreenshotResult(WebDriver driver, String testName) {
        Log.info("Chụp ảnh kết quả");
        WebElement loading = driver.findElement(
                By.xpath("//div[contains(@class, 'oxd-loading-spinner')]"));

        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.invisibilityOf(loading));

        byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment("Screenshot - " + testName, new ByteArrayInputStream(screenshotBytes));
    }
}
