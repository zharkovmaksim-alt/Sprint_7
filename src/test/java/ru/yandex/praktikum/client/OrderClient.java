package ru.yandex.praktikum.client;

import io.restassured.response.Response;
import ru.yandex.praktikum.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String ORDER_PATH = "/api/v1/orders";

    public Response createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(ORDER_PATH);
    }

    public Response getOrdersList() {
        return given()
                .when()
                .get(ORDER_PATH);
    }

    public Response acceptOrder(int orderId, int courierId) {
        return given()
                .queryParam("courierId", courierId)
                .when()
                .put(ORDER_PATH + "/accept/" + orderId);
    }

    public Response getOrderByTrack(int track) {
        return given()
                .queryParam("t", track)
                .when()
                .get(ORDER_PATH + "/track");
    }

    public Response cancelOrder(int track) {
        return given()
                .header("Content-type", "application/json")
                .body("{\"track\": " + track + "}")
                .when()
                .put(ORDER_PATH + "/cancel");
    }
}
