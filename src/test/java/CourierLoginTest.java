import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class CourierLoginTest {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";

    private CourierApi courierApi;
    private CourierModel courier;
    private int courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        courierApi = new CourierApi();

        courier = generateCourierData();

        courierApi.createCourier(courier)
                .then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        courierId = getCourierId();
    }

    @Test
    @DisplayName("Успешная авторизация курьера")
    @Description("Проверка успешной авторизации существующего курьера с корректными логином и паролем")
    public void loginCourierTest() {
        Response response = courierApi.loginCourier(courier);

        response
                .then()
                .statusCode(SC_OK)
                .body("id", equalTo(courierId));
    }

    @Test
    @DisplayName("Авторизация без логина")
    @Description("Проверка ошибки авторизации без обязательного поля login")
    public void loginCourierWithoutLoginTest() {
        CourierModel loginRequest = new CourierModel(
                null,
                courier.getPassword(),
                null
        );

        Response response = courierApi.loginCourier(loginRequest);

        response
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация без пароля")
    @Description("Проверка ошибки авторизации без обязательного поля password")
    public void loginCourierWithoutPasswordTest() {
        CourierModel loginRequest = new CourierModel(
                courier.getLogin(),
                null,
                null
        );

        Response response = courierApi.loginCourier(loginRequest);

        response
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация с неправильным логином")
    @Description("Проверка ошибки авторизации при использовании неправильного логина")
    public void loginCourierWithWrongLoginTest() {
        CourierModel loginRequest = new CourierModel(
                courier.getLogin() + "wrong",
                courier.getPassword(),
                null
        );

        Response response = courierApi.loginCourier(loginRequest);

        response
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Авторизация с неправильным паролем")
    @Description("Проверка ошибки авторизации при использовании неправильного пароля")
    public void loginCourierWithWrongPasswordTest() {
        CourierModel loginRequest = new CourierModel(
                courier.getLogin(),
                courier.getPassword() + "wrong",
                null
        );

        Response response = courierApi.loginCourier(loginRequest);

        response
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Авторизация несуществующего курьера")
    @Description("Проверка ошибки авторизации курьера, которого нет в системе")
    public void loginNonexistentCourierTest() {
        String uniqueValue = String.valueOf(System.currentTimeMillis());

        CourierModel nonexistentCourier = new CourierModel(
                "courier" + uniqueValue,
                "password" + uniqueValue,
                null
        );

        Response response = courierApi.loginCourier(nonexistentCourier);

        response
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    private CourierModel generateCourierData() {
        String uniqueValue = String.valueOf(System.currentTimeMillis());

        return new CourierModel(
                "courier" + uniqueValue,
                "password" + uniqueValue,
                "Test"
        );
    }

    private int getCourierId() {
        return courierApi.loginCourier(courier)
                .then()
                .statusCode(SC_OK)
                .extract()
                .path("id");
    }

    @After
    public void deleteCourier() {
        if (courierId != 0) {
            courierApi.deleteCourier(courierId)
                    .then()
                    .statusCode(SC_OK);
        }
    }
}