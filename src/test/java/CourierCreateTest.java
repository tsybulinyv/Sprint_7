import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class CourierCreateTest {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";

    private CourierApi courierApi;
    private CourierModel courier;
    private int courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        courierApi = new CourierApi();
    }

    @Test
    @DisplayName("Создание курьера")
    @Description("Проверка успешного создания курьера с уникальными данными")
    public void createCourierTest() {
        courier = generateCourierData();

        courierApi.createCourier(courier)
                .then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        courierId = getCourierId();
    }

    @Test
    @DisplayName("Создание дубликата курьера")
    @Description("Проверка невозможности создать двух одинаковых курьеров")
    public void createDuplicateCourierTest() {
        courier = generateCourierData();

        courierApi.createCourier(courier)
                .then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        courierId = getCourierId();

        courierApi.createCourier(courier)
                .then()
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Создание курьера без логина")
    @Description("Проверка ошибки при создании курьера без обязательного поля login")
    public void createCourierWithoutLoginTest() {
        courier = generateCourierData();

        CourierModel courierWithoutLogin = new CourierModel(
                null,
                courier.getPassword(),
                courier.getFirstName()
        );

        courierApi.createCourier(courierWithoutLogin)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    @Description("Проверка ошибки при создании курьера без обязательного поля password")
    public void createCourierWithoutPasswordTest() {
        courier = generateCourierData();

        CourierModel courierWithoutPassword = new CourierModel(
                courier.getLogin(),
                null,
                courier.getFirstName()
        );

        courierApi.createCourier(courierWithoutPassword)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без имени")
    @Description("Проверка ошибки при создании курьера без обязательного поля firstName")
    public void createCourierWithoutFirstNameTest() {
        courier = generateCourierData();

        CourierModel courierWithoutFirstName = new CourierModel(
                courier.getLogin(),
                courier.getPassword(),
                null
        );

        courierApi.createCourier(courierWithoutFirstName)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера с существующим логином")
    @Description("Проверка ошибки при попытке создать курьера с уже существующим логином")
    public void createCourierWithExistingLoginTest() {
        courier = generateCourierData();

        courierApi.createCourier(courier)
                .then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        courierId = getCourierId();

        CourierModel anotherCourier = new CourierModel(
                courier.getLogin(),
                "anotherPassword" + System.currentTimeMillis(),
                "AnotherTest"
        );

        courierApi.createCourier(anotherCourier)
                .then()
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
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