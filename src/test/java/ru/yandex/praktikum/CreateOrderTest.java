package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.model.Order;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest extends BaseTest {

    private final List<String> colors;

    public CreateOrderTest(List<String> colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("BLACK", "GREY")},
                {List.of()}
        };
    }

    @Step("Создание заказа с цветами: {colors}")
    public void sendCreateOrderRequest(List<String> colors) {
        OrderClient orderClient = new OrderClient();

        Order order = new Order(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                "4",
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                colors
        );

        orderClient.createOrder(order)
                .then()
                .log().all()
                .statusCode(SC_CREATED)
                .body("track", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с разными цветами")
    public void createOrderWithDifferentColorsTest() {
        sendCreateOrderRequest(colors);
    }
}
