package ru.yandex.praktikum;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.CourierClient;
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.LoginCredentials;
import ru.yandex.praktikum.model.Order;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class AcceptOrderTest extends BaseTest {

    private CourierClient courierClient;
    private OrderClient orderClient;
    private int courierId;
    private int track;
    private int orderId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        orderClient = new OrderClient();

        String login = "accept_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        String firstName = "Name_" + System.currentTimeMillis();

        Courier courier = new Courier(login, password, firstName);
        courierClient.createCourier(courier);

        courierId = courierClient.loginCourier(new LoginCredentials(login, password))
                .then()
                .extract()
                .path("id");

        Order order = new Order(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                "4",
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                List.of("BLACK")
        );

        track = orderClient.createOrder(order)
                .then()
                .extract()
                .path("track");

        orderId = orderClient.getOrderByTrack(track)
                .then()
                .extract()
                .path("order.id");
    }

    @Test
    @DisplayName("Принятие заказа - успешный сценарий")
    public void acceptOrderSuccessTest() {
        orderClient.acceptOrder(orderId, courierId)
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Принятие заказа - без ID курьера возвращает ошибку")
    public void acceptOrderWithoutCourierIdTest() {
        orderClient.acceptOrder(orderId, 0)
                .then()
                .statusCode(404)
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Принятие заказа - неверный ID курьера возвращает ошибку")
    public void acceptOrderWithInvalidCourierIdTest() {
        orderClient.acceptOrder(orderId, 999999)
                .then()
                .statusCode(404)
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Принятие заказа - без номера заказа возвращает ошибку")
    public void acceptOrderWithoutOrderIdTest() {
        orderClient.acceptOrder(0, courierId)
                .then()
                .statusCode(404)
                .body("message", equalTo("Заказа с таким id не существует"));
    }

    @Test
    @DisplayName("Принятие заказа - неверный номер заказа возвращает ошибку")
    public void acceptOrderWithInvalidOrderIdTest() {
        orderClient.acceptOrder(999999, courierId)
                .then()
                .statusCode(404)
                .body("message", equalTo("Заказа с таким id не существует"));
    }

    @After
    public void tearDown() {
        if (track > 0) {
            orderClient.cancelOrder(track);
        }
        if (courierId > 0) {
            courierClient.deleteCourier(courierId);
        }
    }
}
