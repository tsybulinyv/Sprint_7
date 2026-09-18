import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";

    private OrderApi orderApi;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        orderApi = new OrderApi();
    }

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверка успешного получения списка заказов")
    public void getOrderListTest() {
        Response response = orderApi.getOrderList();

        response
                .then()
                .statusCode(SC_OK)
                .body("orders", notNullValue());
    }
}