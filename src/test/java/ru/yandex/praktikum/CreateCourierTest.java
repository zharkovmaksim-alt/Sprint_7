package ru.yandex.praktikum;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.CourierClient;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.LoginCredentials;

import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest extends BaseTest {

    private CourierClient courierClient;
    private Courier courier;
    private int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    @Test
    @DisplayName("Создание курьера - успешный сценарий")
    public void createCourierSuccessTest() {
        String login = "courier_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        String firstName = "Name_" + System.currentTimeMillis();

        courier = new Courier(login, password, firstName);

        courierClient.createCourier(courier)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        courierId = courierClient.loginCourier(new LoginCredentials(login, password))
                .then()
                .extract()
                .path("id");
    }

    @Test
    @DisplayName("Создание курьера - нельзя создать двух одинаковых")
    public void createDuplicateCourierTest() {
        String login = "duplicate_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        String firstName = "Name_" + System.currentTimeMillis();

        courier = new Courier(login, password, firstName);

        courierClient.createCourier(courier)
                .then()
                .statusCode(201);

        courierClient.createCourier(courier)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));

        courierId = courierClient.loginCourier(new LoginCredentials(login, password))
                .then()
                .extract()
                .path("id");
    }

    @Test
    @DisplayName("Создание курьера - без логина возвращает ошибку")
    public void createCourierWithoutLoginTest() {
        String password = "pass_" + System.currentTimeMillis();
        String firstName = "Name_" + System.currentTimeMillis();

        courier = new Courier(null, password, firstName);

        courierClient.createCourier(courier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера - без пароля возвращает ошибку")
    public void createCourierWithoutPasswordTest() {
        String login = "courier_" + System.currentTimeMillis();
        String firstName = "Name_" + System.currentTimeMillis();

        courier = new Courier(login, null, firstName);

        courierClient.createCourier(courier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера - без имени (успешно)")
    public void createCourierWithoutFirstNameTest() {
        String login = "courier_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();

        courier = new Courier(login, password, null);

        courierClient.createCourier(courier)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        courierId = courierClient.loginCourier(new LoginCredentials(login, password))
                .then()
                .extract()
                .path("id");
    }

    @Test
    @DisplayName("Создание курьера - с уже существующим логином возвращает ошибку")
    public void createCourierWithExistingLoginTest() {
        String login = "existing_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        String firstName = "Name_" + System.currentTimeMillis();

        courier = new Courier(login, password, firstName);

        courierClient.createCourier(courier)
                .then()
                .statusCode(201);

        Courier courierWithSameLogin = new Courier(login, "otherPass", "OtherName");
        courierClient.createCourier(courierWithSameLogin)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));

        courierId = courierClient.loginCourier(new LoginCredentials(login, password))
                .then()
                .extract()
                .path("id");
    }

    @After
    public void tearDown() {
        if (courierId > 0) {
            courierClient.deleteCourier(courierId);
        }
    }
}