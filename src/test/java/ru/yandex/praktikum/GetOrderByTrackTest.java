package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.model.Order;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrderByTrackTest extends BaseTest {

    private OrderClient orderClient;
    private int track;

    @Before
    public void setUp() {
        orderClient = new OrderClient();

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
    }

    @Step("Проверка успешного получения заказа по треку")
    public void checkSuccessGetOrderByTrack() {
        orderClient.getOrderByTrack(track)
                .then()
                .statusCode(SC_OK)
                .body("order", notNullValue())
                .body("order.track", equalTo(track));
    }

    @Step("Проверка ошибки при запросе без номера трека")
    public void checkGetOrderWithoutTrackError() {
        orderClient.getOrderByTrack(0)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Step("Проверка ошибки при запросе с несуществующим треком")
    public void checkGetOrderNonExistentTrackError() {
        orderClient.getOrderByTrack(999999999)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Заказ не найден"));
    }

    @Step("Отмена заказа после теста")
    public void cancelOrderAfterTest() {
        if (track > 0) {
            orderClient.cancelOrder(track);
        }
    }

    @Test
    @DisplayName("Получение заказа по номеру - успешный сценарий")
    public void getOrderByTrackSuccessTest() {
        checkSuccessGetOrderByTrack();
    }

    @Test
    @DisplayName("Получение заказа по номеру - без номера возвращает ошибку")
    public void getOrderByTrackWithoutTrackTest() {
        checkGetOrderWithoutTrackError();
    }

    @Test
    @DisplayName("Получение заказа по номеру - несуществующий номер возвращает ошибку")
    public void getOrderByTrackNonExistentTest() {
        checkGetOrderNonExistentTrackError();
    }

    @After
    public void tearDown() {
        cancelOrderAfterTest();
    }
}
