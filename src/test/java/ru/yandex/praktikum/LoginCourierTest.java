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

    @Step("Проверка успешного логина")
    public void checkSuccessLogin() {
        courierClient.loginCourier(new LoginCredentials(login, password))
                .then()
                .statusCode(SC_OK)
                .body("id", notNullValue());
    }

    @Step("Проверка ошибки при отсутствии логина")
    public void checkMissingLoginError() {
        courierClient.loginCourier(new LoginCredentials(null, password))
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Step("Проверка ошибки при отсутствии пароля")
    public void checkMissingPasswordError() {
        courierClient.loginCourier(new LoginCredentials(login, null))
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Step("Проверка ошибки при неверном логине")
    public void checkWrongLoginError() {
        courierClient.loginCourier(new LoginCredentials("wrongLogin", password))
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Step("Проверка ошибки при неверном пароле")
    public void checkWrongPasswordError() {
        courierClient.loginCourier(new LoginCredentials(login, "wrongPass"))
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Step("Проверка ошибки при логине несуществующего пользователя")
    public void checkNonExistentUserError() {
        courierClient.loginCourier(new LoginCredentials("nonExistent_" + System.currentTimeMillis(), "pass"))
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Step("Удаление курьера после теста")
    public void deleteCourierAfterTest() {
        if (courierId > 0) {
            courierClient.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Логин курьера - успешная авторизация")
    public void loginCourierSuccessTest() {
        checkSuccessLogin();
    }

    @Test
    @DisplayName("Логин курьера - без логина возвращает ошибку")
    public void loginCourierWithoutLoginTest() {
        checkMissingLoginError();
    }

    @Test
    @DisplayName("Логин курьера - без пароля возвращает ошибку")
    public void loginCourierWithoutPasswordTest() {
        checkMissingPasswordError();
    }

    @Test
    @DisplayName("Логин курьера - неверный логин возвращает ошибку")
    public void loginCourierWrongLoginTest() {
        checkWrongLoginError();
    }

    @Test
    @DisplayName("Логин курьера - неверный пароль возвращает ошибку")
    public void loginCourierWrongPasswordTest() {
        checkWrongPasswordError();
    }

    @Test
    @DisplayName("Логин курьера - несуществующий пользователь возвращает ошибку")
    public void loginCourierNonExistentTest() {
        checkNonExistentUserError();
    }

    @After
    public void tearDown() {
        deleteCourierAfterTest();
    }
}
