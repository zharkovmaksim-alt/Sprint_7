package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.CourierClient;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.LoginCredentials;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest extends BaseTest {

    private CourierClient courierClient;
    private Courier courier;
    private int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    @Step("Создание курьера с логином: {courier.login}")
    public void sendCreateCourierRequest(Courier courier) {
        courierClient.createCourier(courier);
    }

    @Step("Логин курьера с логином: {login}")
    public int loginCourierAndGetId(String login, String password) {
        return courierClient.loginCourier(new LoginCredentials(login, password))
                .then()
                .log().all()
                .extract()
                .path("id");
    }

    @Step("Проверка успешного создания курьера")
    public void checkSuccessResponse() {
        courierClient.createCourier(courier)
                .then()
                .log().all()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Step("Проверка ошибки при создании дубликата курьера")
    public void checkDuplicateCourierError() {
        courierClient.createCourier(courier)
                .then()
                .log().all()
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Step("Проверка ошибки при отсутствии логина")
    public void checkMissingLoginError() {
        courierClient.createCourier(courier)
                .then()
                .log().all()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Step("Проверка ошибки при отсутствии пароля")
    public void checkMissingPasswordError() {
        courierClient.createCourier(courier)
                .then()
                .log().all()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Step("Проверка ошибки при использовании существующего логина")
    public void checkExistingLoginError(Courier courierWithSameLogin) {
        courierClient.createCourier(courierWithSameLogin)
                .then()
                .log().all()
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Step("Удаление курьера после теста")
    public void deleteCourierAfterTest() {
        if (courierId > 0) {
            courierClient.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Создание курьера - успешный сценарий")
    public void createCourierSuccessTest() {
        String login = "courier_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        String firstName = "Name_" + System.currentTimeMillis();

        courier = new Courier(login, password, firstName);

        checkSuccessResponse();

        courierId = loginCourierAndGetId(login, password);
    }

    @Test
    @DisplayName("Создание курьера - нельзя создать двух одинаковых")
    public void createDuplicateCourierTest() {
        String login = "duplicate_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        String firstName = "Name_" + System.currentTimeMillis();

        courier = new Courier(login, password, firstName);

        sendCreateCourierRequest(courier);
        checkDuplicateCourierError();

        courierId = loginCourierAndGetId(login, password);
    }

    @Test
    @DisplayName("Создание курьера - без логина возвращает ошибку")
    public void createCourierWithoutLoginTest() {
        String password = "pass_" + System.currentTimeMillis();
        String firstName = "Name_" + System.currentTimeMillis();

        courier = new Courier(null, password, firstName);

        checkMissingLoginError();
    }

    @Test
    @DisplayName("Создание курьера - без пароля возвращает ошибку")
    public void createCourierWithoutPasswordTest() {
        String login = "courier_" + System.currentTimeMillis();
        String firstName = "Name_" + System.currentTimeMillis();

        courier = new Courier(login, null, firstName);

        checkMissingPasswordError();
    }

    @Test
    @DisplayName("Создание курьера - без имени (успешно)")
    public void createCourierWithoutFirstNameTest() {
        String login = "courier_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();

        courier = new Courier(login, password, null);

        checkSuccessResponse();

        courierId = loginCourierAndGetId(login, password);
    }

    @Test
    @DisplayName("Создание курьера - с уже существующим логином возвращает ошибку")
    public void createCourierWithExistingLoginTest() {
        String login = "existing_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        String firstName = "Name_" + System.currentTimeMillis();

        courier = new Courier(login, password, firstName);

        sendCreateCourierRequest(courier);

        Courier courierWithSameLogin = new Courier(login, "otherPass", "OtherName");
        checkExistingLoginError(courierWithSameLogin);

        courierId = loginCourierAndGetId(login, password);
    }

    @After
    public void tearDown() {
        deleteCourierAfterTest();
    }
}