package api.tests.post.favorites;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class RequiredFieldsTest {

    private String getToken() {
        Response authResponse = given()
                .baseUri("https://regions-test.2gis.com")
                .when()
                .post("/v1/auth/tokens");
        return authResponse.getDetailedCookie("token").getValue();
    }

    @Test
    @DisplayName("TC-1: Все обязательные поля")
    void shouldContainAllRequiredFields() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Красная площадь")
                .formParam("lat", 12.67)
                .formParam("lon", 110.854)
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(200)
                .body("title", equalTo("Красная площадь"))
                .body("lat", equalTo(12.67f))
                .body("lon", equalTo(110.854f))
                .body("color", equalTo(null));
    }

    @Test
    @DisplayName("TC-2: Отсутствие title")
    void shouldFieldBeOmittedTitle() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("lat", 12.67)
                .formParam("lon", 110.854)
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-3: Отсутствие lat")
    void shouldFieldBeOmittedLat() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Красная площадь")
                .formParam("lon", 110.854)
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-4: Отсутствие lon")
    void shouldFieldBeOmittedLon() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Красная площадь")
                .formParam("lat", 110.854)
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-5: lat вне диапазона")
    void shouldLatOutOfRange() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Красная площадь")
                .formParam("lat", 102.675)
                .formParam("lon", 110.854)
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-6: lon вне диапазона")
    void shouldLonOutOfRange() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Красная площадь")
                .formParam("lat", 12.675)
                .formParam("lon", -210.8548)
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-7: lat строка")
    void shouldLatString() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Красная площадь")
                .formParam("lat", "abc")
                .formParam("lon", 50.854)
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-8: lon строка")
    void shouldLonString() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Красная площадь")
                .formParam("lat", -50.854)
                .formParam("lon", "abc")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-9: title число")
    void shouldTitleNumber() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", 123)
                .formParam("lat", -50.854)
                .formParam("lon", -89.854)
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-10: title пустой")
    void shouldTitleEmpty() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "")
                .formParam("lat", -50.854)
                .formParam("lon", -89.854)
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-11: title больше 999 символов")
    void shouldTitleMore999Сharacters() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "A".repeat(1020))
                .formParam("lat", -50.854)
                .formParam("lon", -89.854)
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-12: title с кириллицей, латиницей, цифрами и знаками")
    void shouldTitleConsistOfCyrillicAndLatinAndNumbersAndLetters() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title","Проспект мира 180, рядом с садом.")
                .formParam("lat", -50.854)
                .formParam("lon", -89.854)
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(200)
                .body("title", equalTo("Проспект мира 180, рядом с садом."));
    }
}