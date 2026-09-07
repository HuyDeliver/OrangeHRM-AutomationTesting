package com.hrm.Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class SideBar {
    private WebDriver driver;

    public SideBar(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

}
