package api.tests.post.favorites;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;

public class BoundaryTest {

    private String getToken() {
        Response authResponse = given()
                .baseUri("https://regions-test.2gis.com")
                .when()
                .post("/v1/auth/tokens");
        return authResponse.getDetailedCookie("token").getValue();
    }

    private void createFavorite(String title, double lat, double lon, int expectedStatus) {
        String token = getToken();
        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", title)
                .formParam("lat", lat)
                .formParam("lon", lon)
                .formParam("color", "RED")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(expectedStatus);
    }

    @Test
    @DisplayName("TC-19: Граничные значения title: 1 символ")
    void shouldCreateWithMinTitleLength() {
        createFavorite("R", 55.7558, 37.6173, 200);
    }

    @Test
    @DisplayName("TC-18: Граничные значения title: 999 символов")
    void shouldCreateWithMaxTitleLength() {
        createFavorite("A".repeat(999), 55.7558, 37.6173, 200);
    }

    @Test
    @DisplayName("TC-16: Граничные значения title: 1000 символов")
    void shouldRejectTooLongTitle() {
        createFavorite("A".repeat(1000), 55.7558, 37.6173, 400);
    }

    @Test
    @DisplayName("TC-17: Граничные значения title: пусто")
    void shouldCreateTitleWithNullValue() {
        createFavorite("", 55.7558, 37.6173, 400);
    }

    @ParameterizedTest
    @CsvSource({
            "90.0, 170.0, 200",
            "55.0, 180.0, 200",
            "-90.0, -170.0, 200",
            "-12.0, -180.0, 200",
            "90.0, 180.0, 200",
            "-90.0, -180.0, 200",
            "90.0, 0.0, 200",
            "-90.0, 0.0, 200",
            "0.0, 180.0, 200",
            "0.0, -180.0, 200",
            "0,0,200",
            "90.000001, 180.000000, 400",
            "90.000000, 180.000001, 400",
            "-90.000001, 180.000000, 400",
            "-90.000000, 180.000001, 400",
            "90.000001, 180.000001, 400",
            "-90.000001, 180.000001, 400",
            "90.000001, 0.0, 400",
            "-90.000001, 0.0, 400",
            "0.0, 180.000001, 400",
            "0.0, -180.000001, 400",
            "NaN, 0.0, 400",
            "0.0, NaN, 400",
            "Infinity, 0.0, 400",
            "0.0, -Infinity, 400"
    })
    @DisplayName("TC-20: Граничные значения lat/lon")
    void shouldValidateCoordinateBoundaries(double lat, double lon, int expectedStatus) {
        createFavorite("Парк", lat, lon, expectedStatus);
    }
}
