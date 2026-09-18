import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CourierApi {

    public Response createCourier(CourierModel courier) {
        return given()
                .contentType("application/json")
                .body(courier)
                .when()
                .post(Endpoints.COURIER);
    }

    public Response loginCourier(CourierModel courier) {
        return given()
                .contentType("application/json")
                .body(courier)
                .when()
                .post(Endpoints.COURIER_LOGIN);
    }

    public Response deleteCourier(int courierId) {
        return given()
                .when()
                .delete(Endpoints.COURIER + "/" + courierId);
    }
}