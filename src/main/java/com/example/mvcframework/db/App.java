package com.example.mvcframework.db;

import com.example.mvcframework.spring.annotation.Component;

@Component
public class App {
    public static final String BASE_PACKAGE_PATH = "com.example.mvcframework";
    public static String DB_HOST = "localhost";
    public static int DB_PORT = 3306;
    public static String DB_ID = "root";
    public static String DB_PASSWORD = "0913";
    public static String DB_NAME = "simpledb__test";
    public static boolean isProd = false;

    static {
        String profilesActive = System.getenv("SPRING_PROFILES_ACTIVE");

        if (profilesActive != null && profilesActive.equals("production")) {
            isProd = true;
        }

        if (isProd) {
            // 운영DB 정보
            DB_HOST = "localhost";
            DB_PORT = 3306;
            DB_ID = "root";
            DB_PASSWORD = "0913";
            DB_NAME = "simpledb__test";
        }
    }
}