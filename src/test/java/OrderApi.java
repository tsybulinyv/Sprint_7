import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderApi {

    public Response createOrder(OrderModel order) {
        return given()
                .contentType("application/json")
                .body(order)
                .when()
                .post(Endpoints.ORDERS);
    }

    public Response getOrderList() {
        return given()
                .when()
                .get(Endpoints.ORDERS);
    }

    public Response cancelOrder(int track) {
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("track", track);

        return given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .put(Endpoints.ORDERS_CANCEL);
    }
}