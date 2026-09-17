import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";

    private String login;
    private String password;
    private int courierId;

    @Before
    @Step("Настроить базовый URL")
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

    @Test
    @Step("Проверить успешную авторизацию курьера")
    public void loginCourierTest() {
        generateCourierData();

        createCourier();

        courierId = getCourierId();

        Response response = loginCourier(login, password);

        response
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @Step("Проверить авторизацию без логина")
    public void loginCourierWithoutLoginTest() {
        generateCourierData();

        createCourier();

        courierId = getCourierId();

        Response response = loginCourierWithoutLogin();

        response
                .then()
                .statusCode(400)
                .body("message", notNullValue());
    }

    @Test
    @Step("Проверить авторизацию без пароля")
    public void loginCourierWithoutPasswordTest() {
        generateCourierData();

        createCourier();

        courierId = getCourierId();

        Response response = loginCourierWithoutPassword();

        response
                .then()
                .statusCode(400)
                .body("message", notNullValue());
    }

    @Test
    @Step("Проверить авторизацию с неправильным логином")
    public void loginCourierWithWrongLoginTest() {
        generateCourierData();

        createCourier();

        courierId = getCourierId();

        String wrongLogin = login + "wrong";

        Response response = loginCourier(wrongLogin, password);

        response
                .then()
                .statusCode(404)
                .body("message", notNullValue());
    }

    @Test
    @Step("Проверить авторизацию с неправильным паролем")
    public void loginCourierWithWrongPasswordTest() {
        generateCourierData();

        createCourier();

        courierId = getCourierId();

        String wrongPassword = password + "wrong";

        Response response = loginCourier(login, wrongPassword);

        response
                .then()
                .statusCode(404)
                .body("message", notNullValue());
    }

    @Test
    @Step("Проверить авторизацию несуществующего курьера")
    public void loginNonexistentCourierTest() {
        generateCourierData();

        Response response = loginCourier(login, password);

        response
                .then()
                .statusCode(404)
                .body("message", notNullValue());
    }

    @Step("Сгенерировать уникальные данные курьера")
    private void generateCourierData() {
        String uniqueValue = String.valueOf(System.currentTimeMillis());

        login = "courier" + uniqueValue;
        password = "password" + uniqueValue;
    }

    @Step("Создать курьера")
    private void createCourier() {
        given()
                .header("Content-type", "application/json")
                .body("{"
                        + "\"login\":\"" + login + "\","
                        + "\"password\":\"" + password + "\","
                        + "\"firstName\":\"Test\""
                        + "}")
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", notNullValue());
    }

    @Step("Авторизоваться с логином и паролем")
    private Response loginCourier(String courierLogin, String courierPassword) {
        return given()
                .header("Content-type", "application/json")
                .body("{"
                        + "\"login\":\"" + courierLogin + "\","
                        + "\"password\":\"" + courierPassword + "\""
                        + "}")
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Авторизоваться без логина")
    private Response loginCourierWithoutLogin() {
        return given()
                .header("Content-type", "application/json")
                .body("{"
                        + "\"password\":\"" + password + "\""
                        + "}")
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Авторизоваться без пароля")
    private Response loginCourierWithoutPassword() {
        return given()
                .header("Content-type", "application/json")
                .body("{"
                        + "\"login\":\"" + login + "\""
                        + "}")
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Авторизоваться и получить id курьера")
    private int getCourierId() {
        return loginCourier(login, password)
                .then()
                .statusCode(200)
                .extract()
                .path("id");
    }

    @After
    @Step("Удалить созданного курьера")
    public void deleteCourier() {
        if (courierId != 0) {
            given()
                    .when()
                    .delete("/api/v1/courier/" + courierId)
                    .then()
                    .statusCode(200);
        }
    }
}