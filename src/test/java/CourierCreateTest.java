import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierCreateTest {

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
    @Step("Создать курьера с уникальными данными")
    public void createCourierTest() {
        generateCourierData();

        Response createResponse = createCourier();

        createResponse
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        courierId = getCourierId();
    }

    @Test
    @Step("Проверить, что нельзя создать двух одинаковых курьеров")
    public void createDuplicateCourierTest() {
        generateCourierData();

        createCourier()
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        courierId = getCourierId();

        Response duplicateResponse = createCourier();

        duplicateResponse
                .then()
                .statusCode(409)
                .body("message", notNullValue());
    }

    @Test
    @Step("Проверить создание курьера без логина")
    public void createCourierWithoutLoginTest() {
        generateCourierData();

        Response response = createCourier(
                "{"
                        + "\"password\":\"" + password + "\","
                        + "\"firstName\":\"Test\""
                        + "}"
        );

        response
                .then()
                .statusCode(400)
                .body("message", notNullValue());
    }

    @Test
    @Step("Проверить создание курьера без пароля")
    public void createCourierWithoutPasswordTest() {
        generateCourierData();

        Response response = createCourier(
                "{"
                        + "\"login\":\"" + login + "\","
                        + "\"firstName\":\"Test\""
                        + "}"
        );

        response
                .then()
                .statusCode(400)
                .body("message", notNullValue());
    }

    @Test
    @Step("Проверить создание курьера без имени")
    public void createCourierWithoutFirstNameTest() {
        generateCourierData();

        Response response = createCourier(
                "{"
                        + "\"login\":\"" + login + "\","
                        + "\"password\":\"" + password + "\""
                        + "}"
        );

        if (response.statusCode() == 201) {
            courierId = getCourierId();
        }

        response
                .then()
                .statusCode(400)
                .body("message", notNullValue());
    }

    @Test
    @Step("Проверить создание курьера с существующим логином")
    public void createCourierWithExistingLoginTest() {
        generateCourierData();

        createCourier()
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        courierId = getCourierId();

        String anotherPassword = "anotherPassword" + System.currentTimeMillis();

        Response response = createCourier(
                "{"
                        + "\"login\":\"" + login + "\","
                        + "\"password\":\"" + anotherPassword + "\","
                        + "\"firstName\":\"AnotherTest\""
                        + "}"
        );

        response
                .then()
                .statusCode(409)
                .body("message", notNullValue());
    }

    @Step("Сгенерировать уникальные данные курьера")
    private void generateCourierData() {
        String uniqueValue = String.valueOf(System.currentTimeMillis());

        login = "courier" + uniqueValue;
        password = "password" + uniqueValue;
    }

    @Step("Создать курьера")
    private Response createCourier() {
        return given()
                .header("Content-type", "application/json")
                .body("{"
                        + "\"login\":\"" + login + "\","
                        + "\"password\":\"" + password + "\","
                        + "\"firstName\":\"Test\""
                        + "}")
                .when()
                .post("/api/v1/courier");
    }

    @Step("Создать курьера с переданным телом")
    private Response createCourier(String requestBody) {
        return given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Авторизоваться и получить id курьера")
    private int getCourierId() {
        return given()
                .header("Content-type", "application/json")
                .body("{"
                        + "\"login\":\"" + login + "\","
                        + "\"password\":\"" + password
                        + "\"}")
                .when()
                .post("/api/v1/courier/login")
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