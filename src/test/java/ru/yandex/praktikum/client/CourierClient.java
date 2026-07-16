package ru.yandex.praktikum.client;

import io.restassured.response.Response;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.LoginCredentials;

import static io.restassured.RestAssured.given;

public class CourierClient {

    private static final String COURIER_PATH = "/api/v1/courier";

    public Response createCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(COURIER_PATH);
    }

    public Response loginCourier(LoginCredentials credentials) {
        return given()
                .header("Content-type", "application/json")
                .body(credentials)
                .when()
                .post(COURIER_PATH + "/login");
    }

    public Response deleteCourier(int courierId) {
        return given()
                .when()
                .delete(COURIER_PATH + "/" + courierId);
    }
}