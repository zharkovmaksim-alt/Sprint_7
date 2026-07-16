package ru.yandex.praktikum;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.CourierClient;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.LoginCredentials;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest extends BaseTest {

    private CourierClient courierClient;
    private int courierId;
    private String login;
    private String password;

    @Before
    public void setUp() {
        courierClient = new CourierClient();

        login = "login_" + System.currentTimeMillis();
        password = "pass_" + System.currentTimeMillis();
        String firstName = "Name_" + System.currentTimeMillis();

        Courier courier = new Courier(login, password, firstName);
        courierClient.createCourier(courier);

        courierId = courierClient.loginCourier(new LoginCredentials(login, password))
                .then()
                .extract()
                .path("id");
    }

    @Test
    @DisplayName("Логин курьера - успешная авторизация")
    public void loginCourierSuccessTest() {
        courierClient.loginCourier(new LoginCredentials(login, password))
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Логин курьера - без логина возвращает ошибку")
    public void loginCourierWithoutLoginTest() {
        courierClient.loginCourier(new LoginCredentials(null, password))
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин курьера - без пароля возвращает ошибку")
    public void loginCourierWithoutPasswordTest() {
        courierClient.loginCourier(new LoginCredentials(login, null))
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин курьера - неверный логин возвращает ошибку")
    public void loginCourierWrongLoginTest() {
        courierClient.loginCourier(new LoginCredentials("wrongLogin", password))
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин курьера - неверный пароль возвращает ошибку")
    public void loginCourierWrongPasswordTest() {
        courierClient.loginCourier(new LoginCredentials(login, "wrongPass"))
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин курьера - несуществующий пользователь возвращает ошибку")
    public void loginCourierNonExistentTest() {
        courierClient.loginCourier(new LoginCredentials("nonExistent_" + System.currentTimeMillis(), "pass"))
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @After
    public void tearDown() {
        if (courierId > 0) {
            courierClient.deleteCourier(courierId);
        }
    }
}
