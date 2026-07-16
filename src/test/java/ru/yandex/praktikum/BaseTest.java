package ru.yandex.praktikum;

import io.restassured.RestAssured;
import org.junit.Before;

public class BaseTest {

    @Before
    public void setUp() {
        // Явно отключаем прокси
        System.setProperty("http.proxyHost", "");
        System.setProperty("http.proxyPort", "");
        System.setProperty("https.proxyHost", "");
        System.setProperty("https.proxyPort", "");
        System.setProperty("java.net.useSystemProxies", "false");

        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        RestAssured.useRelaxedHTTPSValidation();
    }
}