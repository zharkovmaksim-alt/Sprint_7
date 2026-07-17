package ru.yandex.praktikum;

import io.qameta.allure.Step;
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

import static org.apache.http.HttpStatus.*;
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

    @Step("Проверка успешного принятия заказа")
    public void checkSuccessAcceptOrder() {
        orderClient.acceptOrder(orderId, courierId)
                .then()
                .statusCode(SC_OK)
                .body("ok", equalTo(true));
    }

    @Step("Проверка ошибки при отсутствии ID курьера")
    public void checkMissingCourierIdError() {
        orderClient.acceptOrder(orderId, 0)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Step("Проверка ошибки при неверном ID курьера")
    public void checkInvalidCourierIdError() {
        orderClient.acceptOrder(orderId, 999999)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Step("Проверка ошибки при отсутствии номера заказа")
    public void checkMissingOrderIdError() {
        orderClient.acceptOrder(0, courierId)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Заказа с таким id не существует"));
    }

    @Step("Проверка ошибки при неверном номере заказа")
    public void checkInvalidOrderIdError() {
        orderClient.acceptOrder(999999, courierId)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Заказа с таким id не существует"));
    }

    @Step("Отмена заказа и удаление курьера после теста")
    public void cleanUpAfterTest() {
        if (track > 0) {
            orderClient.cancelOrder(track);
        }
        if (courierId > 0) {
            courierClient.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Принятие заказа - успешный сценарий")
    public void acceptOrderSuccessTest() {
        checkSuccessAcceptOrder();
    }

    @Test
    @DisplayName("Принятие заказа - без ID курьера возвращает ошибку")
    public void acceptOrderWithoutCourierIdTest() {
        checkMissingCourierIdError();
    }

    @Test
    @DisplayName("Принятие заказа - неверный ID курьера возвращает ошибку")
    public void acceptOrderWithInvalidCourierIdTest() {
        checkInvalidCourierIdError();
    }

    @Test
    @DisplayName("Принятие заказа - без номера заказа возвращает ошибку")
    public void acceptOrderWithoutOrderIdTest() {
        checkMissingOrderIdError();
    }

    @Test
    @DisplayName("Принятие заказа - неверный номер заказа возвращает ошибку")
    public void acceptOrderWithInvalidOrderIdTest() {
        checkInvalidOrderIdError();
    }

    @After
    public void tearDown() {
        cleanUpAfterTest();
    }
}