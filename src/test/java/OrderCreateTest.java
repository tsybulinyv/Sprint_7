import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreateTest {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";

    private static final String FIRST_NAME = "Test";
    private static final String LAST_NAME = "Testov";
    private static final String ADDRESS = "Москва, улица Тестовая, дом 1";
    private static final int METRO_STATION = 4;
    private static final String PHONE = "+7 800 355 35 35";
    private static final int RENT_TIME = 5;
    private static final String COMMENT = "Тестовый заказ";

    private final String color;

    public OrderCreateTest(String color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Цвет: {0}")
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
                {"[\"BLACK\"]"},
                {"[\"GREY\"]"},
                {"[\"BLACK\", \"GREY\"]"},
                {null}
        });
    }

    @Before
    @Step("Настроить базовый URL")
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

    @Test
    @Step("Создать заказ с выбранным вариантом цвета")
    public void createOrderTest() {
        Response response = createOrder();

        response
                .then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Step("Создать заказ")
    private Response createOrder() {
        String requestBody = "{"
                + "\"firstName\":\"" + FIRST_NAME + "\","
                + "\"lastName\":\"" + LAST_NAME + "\","
                + "\"address\":\"" + ADDRESS + "\","
                + "\"metroStation\":" + METRO_STATION + ","
                + "\"phone\":\"" + PHONE + "\","
                + "\"rentTime\":" + RENT_TIME + ","
                + "\"deliveryDate\":\"" + LocalDate.now().plusDays(1) + "\","
                + "\"comment\":\"" + COMMENT + "\""
                + (color == null ? "" : ",\"color\":" + color)
                + "}";

        return given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/orders");
    }
}