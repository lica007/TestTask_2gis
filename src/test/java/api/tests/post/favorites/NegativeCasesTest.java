package api.tests.post.favorites;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class NegativeCasesTest {

    private String getToken() {
        Response authResponse = given()
                .baseUri("https://regions-test.2gis.com")
                .when()
                .post("/v1/auth/tokens");
        return authResponse.getDetailedCookie("token").getValue();
    }

    @Test
    @DisplayName("TC-21: Отсутствие всех полей")
    void shouldCreateAllEmptyFields() {
        String token = getToken();
        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-22: Отсутствие lon и lat")
    void shouldLonAndLatAndColorMissing() {
        String token = getToken();
        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "ТЦ")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-23: Отсутствие title")
    void shouldTitleAndColorMissing() {
        String token = getToken();
        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("lat", 12.675)
                .formParam("lon", -80.8548)
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-24: lat = null")
    void shouldLatNull() {
        String token = getToken();
        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "ТЦ Планета")
                .formParam("lat", "")
                .formParam("lon", 75.154929)
                .formParam("color", "GREEN")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-25: lon = null")
    void shouldLonNull() {
        String token = getToken();
        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "ТЦ Планета")
                .formParam("lat", 75.154929)
                .formParam("lon", "")
                .formParam("color", "GREEN")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-26: title как число в строке")
    void shouldTitleNumberAString() {
        String token = getToken();
        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "555")
                .formParam("lat", 75.154929)
                .formParam("lon", -80.8548)
                .formParam("color", "GREEN")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("TC-27: Спецсимволы в строке поля title")
    void should() {
        String token = getToken();
        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "№@#$%")
                .formParam("lat", 75.154929)
                .formParam("lon", -80.8548)
                .formParam("color", "GREEN")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }
}
