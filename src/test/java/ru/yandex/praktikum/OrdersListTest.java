package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import ru.yandex.praktikum.client.OrderClient;

import static org.hamcrest.Matchers.notNullValue;

public class OrdersListTest extends BaseTest {

    @Step("Отправка запроса на получение списка заказов")
    public void sendGetOrdersRequest() {
        OrderClient orderClient = new OrderClient();

        orderClient.getOrdersList()
                .then()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Получение списка заказов - успешный запрос")
    public void getOrdersListSuccessTest() {
        sendGetOrdersRequest();
    }
}