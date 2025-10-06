package api.tests.post.favorites;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CombinedCasesTest {

    private String getToken() {
        Response authResponse = given()
                .baseUri("https://regions-test.2gis.com")
                .when()
                .post("/v1/auth/tokens");
        return authResponse.getDetailedCookie("token").getValue();
    }

    @Test
    @DisplayName("TC-28: Все поля валидные")
    void shouldAllFieldsValid() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Спортзал")
                .formParam("lat", 47.67)
                .formParam("lon", 77.854)
                .formParam("color", "RED")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(200)
                .body("title", equalTo("Спортзал"))
                .body("lat", equalTo(47.67f))
                .body("lon", equalTo(77.854f))
                .body("color", equalTo("RED"));
    }

    @Test
    @DisplayName("TC-29: Все поля не валидные")
    void shouldAllFieldsNotValid() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "")
                .formParam("lat", 100.67)
                .formParam("lon", -197.854)
                .formParam("color", "GOLD")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-30: Валидные lat/lon и невалидный title")
    void should() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "")
                .formParam("lat", 47.67)
                .formParam("lon", 77.854)
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-31: Валидные title и невалидный color")
    void should2() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Спортзал")
                .formParam("lat", 47.67)
                .formParam("lon", 77.854)
                .formParam("color", "PURPLE")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC-32: Валидные color и невалидный lat")
    void should3() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Спортзал")
                .formParam("lat", 147.67)
                .formParam("lon", 77.854)
                .formParam("color", "BLUE")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }
}
