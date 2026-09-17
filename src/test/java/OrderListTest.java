import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";

    @Before
    @Step("Настроить базовый URL")
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

    @Test
    @Step("Получить список заказов")
    public void getOrderListTest() {
        Response response = getOrderList();

        response
                .then()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Step("Получить список заказов")
    private Response getOrderList() {
        return given()
                .when()
                .get("/api/v1/orders");
    }
}