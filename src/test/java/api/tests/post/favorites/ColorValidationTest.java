package api.tests.post.favorites;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ColorValidationTest {

    private String getToken() {
        Response authResponse = given()
                .baseUri("https://regions-test.2gis.com")
                .when()
                .post("/v1/auth/tokens");
        return authResponse.getDetailedCookie("token").getValue();
    }

    @Test
    @DisplayName("TC-13: Отсутствие цвета (color)")
    void shouldCreateFavoriteWithoutColor() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Парк")
                .formParam("lat", -85.755826)
                .formParam("lon", 179.234129)
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(200)
                .body("color", nullValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"RED", "GREEN", "BLUE", "YELLOW"})
    @DisplayName("TC-14: Допустимые значения цвета (color)")
    void shouldCreateFavoriteWithDifferentColors(String color) {
        String token = getToken();
        System.out.println("Token: " + token);

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Парк")
                .formParam("lat", -85.755826)
                .formParam("lon", 179.234129)
                .formParam("color", color)
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(200)
                .body("color", equalTo(color));
    }

    @ParameterizedTest
    @CsvSource({
            "'', 400", // пусто
            "null, 400", // null
            "blue, 400", // нижний регистр
            "Red, 400", // смешанный регистр
            "ORANGE, 400", // другой цвет
            "1, 400", // число
            "true, 400" // boolean
    })
    @DisplayName("TC-15: Не валидные значения color")
    void

    shouldColorInvalid(String color, int expected_status) {
        String token = getToken();
        System.out.println("Token: " + token);

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Парк")
                .formParam("lat", -85.755826)
                .formParam("lon", 179.234129)
                .formParam("color", color)
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(expected_status);
    }
}