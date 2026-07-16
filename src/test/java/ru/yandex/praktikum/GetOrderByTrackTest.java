package ru.yandex.praktikum;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.model.Order;

import java.util.List;

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

    @Test
    @DisplayName("Получение заказа по номеру - успешный сценарий")
    public void getOrderByTrackSuccessTest() {
        orderClient.getOrderByTrack(track)
                .then()
                .statusCode(200)
                .body("order", notNullValue())
                .body("order.track", equalTo(track));
    }

    @Test
    @DisplayName("Получение заказа по номеру - без номера возвращает ошибку")
    public void getOrderByTrackWithoutTrackTest() {
        orderClient.getOrderByTrack(0)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Получение заказа по номеру - несуществующий номер возвращает ошибку")
    public void getOrderByTrackNonExistentTest() {
        orderClient.getOrderByTrack(999999999)
                .then()
                .statusCode(404)
                .body("message", equalTo("Заказ не найден"));
    }

    @After
    public void tearDown() {
        if (track > 0) {
            orderClient.cancelOrder(track);
        }
    }
}
