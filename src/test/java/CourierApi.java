import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CourierApi {

    @Step("Создать курьера")
    public Response createCourier(CourierModel courier) {
        return given()
                .contentType("application/json")
                .body(courier)
                .when()
                .post(Endpoints.COURIER);
    }

    @Step("Авторизоваться курьером")
    public Response loginCourier(CourierModel courier) {
        return given()
                .contentType("application/json")
                .body(courier)
                .when()
                .post(Endpoints.COURIER_LOGIN);
    }

    @Step("Удалить курьера")
    public Response deleteCourier(int courierId) {
        return given()
                .when()
                .delete(Endpoints.COURIER + "/" + courierId);
    }
}