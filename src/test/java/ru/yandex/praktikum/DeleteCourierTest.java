package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.CourierClient;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.LoginCredentials;

import static org.hamcrest.Matchers.equalTo;

public class DeleteCourierTest extends BaseTest {

    private CourierClient courierClient;
    private int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();

        String login = "delete_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        String firstName = "Name_" + System.currentTimeMillis();

        Courier courier = new Courier(login, password, firstName);
        courierClient.createCourier(courier);

        courierId = courierClient.loginCourier(new LoginCredentials(login, password))
                .then()
                .extract()
                .path("id");
    }

    @Step("Проверка успешного удаления курьера")
    public void checkSuccessDeleteCourier() {
        courierClient.deleteCourier(courierId)
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));
        courierId = 0;
    }

    @Step("Проверка ошибки при удалении без ID")
    public void checkDeleteWithoutIdError() {
        courierClient.deleteCourier(0)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для удаления курьера"));
    }

    @Step("Проверка ошибки при удалении несуществующего курьера")
    public void checkDeleteNonExistentError() {
        courierClient.deleteCourier(999999)
                .then()
                .statusCode(404)
                .body("message", equalTo("Курьер с id 999999 не найден"));
    }

    @Step("Удаление курьера после теста")
    public void deleteCourierAfterTest() {
        if (courierId > 0) {
            courierClient.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Удаление курьера - успешный сценарий")
    public void deleteCourierSuccessTest() {
        checkSuccessDeleteCourier();
    }

    @Test
    @DisplayName("Удаление курьера - без ID возвращает ошибку")
    public void deleteCourierWithoutIdTest() {
        checkDeleteWithoutIdError();
    }

    @Test
    @DisplayName("Удаление курьера - несуществующий ID возвращает ошибку")
    public void deleteCourierNonExistentTest() {
        checkDeleteNonExistentError();
    }

    @After
    public void tearDown() {
        deleteCourierAfterTest();
    }
}