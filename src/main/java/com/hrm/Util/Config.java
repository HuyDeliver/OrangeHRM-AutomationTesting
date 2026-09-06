package com.hrm.Util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final Properties prop = new Properties();

    static {
        try {
            FileInputStream file = new FileInputStream("config.properties");
            prop.load(file);
        } catch (IOException e) {
            throw new RuntimeException("không đọc được file config.properties");
        }
    }

    public static String get(String key) {
        return prop.getProperty(key);
    }

}
