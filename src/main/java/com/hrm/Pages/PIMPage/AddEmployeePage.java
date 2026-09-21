package com.hrm.Pages.PIMPage;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.Log;
import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

public class AddEmployeePage {
    private WebDriver driver;

    public AddEmployeePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//div[contains(@class,'oxd-input-group')]/following::input[@name='firstName']")
    private WebElement firstName;
    @FindBy(xpath = "//div[contains(@class,'oxd-input-group')]/following::input[@name='middleName']")
    private WebElement middleName;
    @FindBy(xpath = "//div[contains(@class,'oxd-input-group')]/following::input[@name='lastName']")
    private WebElement lastName;

    @FindBy(xpath = "//div[contains(@class,'oxd-input-group')]/following::input[@type='file']")
    private WebElement fileImage;
    @FindBy(xpath = "//div[@class='oxd-switch-wrapper']/descendant::span[contains(@class,'oxd-switch-input')]")
    private WebElement activeButton;

    public boolean isAddEmployeeVisible() {
        return componentLocator.checkTitleH6(driver, "Add Employee");
    }

    public void infoNewEmployee(String file, String firstname, String lastname, String middlename, String employeeID) {
        Log.info("Thêm ảnh chân dung");
        fileImage.sendKeys(file);
        Log.info("Nhập firstname: " + firstname);
        firstName.sendKeys(firstname);
        Log.info("Nhập middlename: " + middlename);
        middleName.sendKeys(middlename);
        Log.info("Nhập lastname: " + lastname);
        lastName.sendKeys(lastname);
        By loader = By.cssSelector(".oxd-form-loader");
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.invisibilityOfElementLocated(loader));
        componentLocator.fillInput(driver, "Employee Id", employeeID);
    }

    public void createLoginDetail(String name, String status, String pass, String confirmPass) {
        Log.info("Click Create Login Detail");
        waitUtils.waitElementClick(driver, activeButton);

        componentLocator.fillInput(driver, "Username", name);

        Log.info("Chọn status");
        Log.info("Chọn status: " + status);
        WebElement label = driver.findElement(
                By.xpath("//label[contains(normalize-space(),'" + status + "')]"));
        waitUtils.waitElementClick(driver, label);

        componentLocator.fillInput(driver, "Password", pass);
        componentLocator.fillInput(driver, "Confirm Password", confirmPass);

        componentLocator.buttonSave(driver);
    }

    public boolean isCreateNewEmployeeSuccess() {
        return componentLocator.checkToasstSuccess(driver, "Add New Employee");
    }
}
