import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.apache.http.HttpStatus.SC_CREATED;
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

    private final List<String> color;
    private OrderApi orderApi;
    private int track;

    public OrderCreateTest(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Цвет: {0}")
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
                {Arrays.asList("BLACK")},
                {Arrays.asList("GREY")},
                {Arrays.asList("BLACK", "GREY")},
                {null}
        });
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        orderApi = new OrderApi();
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Проверка успешного создания заказа с различными вариантами цвета самоката")
    public void createOrderTest() {
        OrderModel order = new OrderModel(
                FIRST_NAME,
                LAST_NAME,
                ADDRESS,
                METRO_STATION,
                PHONE,
                RENT_TIME,
                LocalDate.now().plusDays(1).toString(),
                COMMENT,
                color
        );

        Response response = orderApi.createOrder(order);

        track = response
                .then()
                .statusCode(SC_CREATED)
                .body("track", notNullValue())
                .extract()
                .path("track");
    }

    @After
    public void cancelOrder() {
        if (track != 0) {
            orderApi.cancelOrder(track);
        }
    }
}