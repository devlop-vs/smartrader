package com.smartrader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    public static String dbUrl = "mongodb://localhost:27017";
    public static String zmqAddress = "tcp://192.168.100.3:9999";

    public static void load() {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 读取配置项
        String dburl = properties.getProperty("db.url");
        String dbUsername = properties.getProperty("db.username");
        String dbPassword = properties.getProperty("db.password");

        String appTitle = properties.getProperty("app.title");
        String appVersion = properties.getProperty("app.version");
        String zmqAddress = properties.getProperty("zmq.address");

        // 输出配置项
        System.out.println("Database Url: " + dburl);
        System.out.println("Database Username: " + dbUsername);
        System.out.println("Database Password: " + dbPassword);
        System.out.println("Application Title: " + appTitle);
        System.out.println("Application Version: " + appVersion);
        System.out.println("Zmq Bind Address: " + zmqAddress);

        ConfigReader.dbUrl = dburl;
        ConfigReader.zmqAddress = zmqAddress;
    }
}
