package api.tests.post.favorites;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class StructureResponseTest {

    private String getToken() {
        Response authResponse = given()
                .baseUri("https://regions-test.2gis.com")
                .when()
                .post("/v1/auth/tokens");
        return authResponse.getDetailedCookie("token").getValue();
    }

    @Test
    @DisplayName("TC33: Проверка на наличие всех полей в ответе")
    void shouldContainAllExpectedFieldsInResponse() {
        String token = getToken();
        System.out.println("Token: " + token);

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Парк")
                .formParam("lat", -85.755826)
                .formParam("lon", 179.234129)
                .formParam("color", "RED")
                .log().all()
                .when()
                .post("/v1/favorites")
        .then()
                .log().all()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", notNullValue())
                .body("lat", notNullValue())
                .body("lon", notNullValue())
                .body("color", notNullValue())
                .body("created_at", notNullValue());
    }

    @Test
    @DisplayName("TC34: Проверка на соответствие в ответе передоваемых данных")
    void shouldContainResponseSpecifiedData() {
        String token = getToken();
        System.out.println("Token: " + token);

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Водопад")
                .formParam("lat", 12.894267)
                .formParam("lon", 0.854307)
                .formParam("color", "BLUE")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(200)
                .body("title", equalTo("Водопад"))
                .body("lat", equalTo(12.894267f))
                .body("lon", equalTo(0.854307f))
                .body("color", equalTo("BLUE"));
    }

    @Test
    @DisplayName("TC35: Проверка формата ответа")
    void shouldValidateResponseFormat() {
        String token = getToken();
        System.out.println("Token: " + token);

        Response response = given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "ТЦ Планета")
                .formParam("lat", 5.751826)
                .formParam("lon", 90.154929)
                .formParam("color", "GREEN")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(200)
                .extract().response();

        assertThat(response.path("id"), instanceOf(Long.class));
        assertThat(response.path("title"), instanceOf(String.class));
        assertThat(response.path("lat"), anyOf(instanceOf(Float.class), instanceOf(Double.class)));
        assertThat(response.path("lon"), anyOf(instanceOf(Float.class), instanceOf(Double.class)));
        assertThat(response.path("color"), instanceOf(String.class));
        assertThat(response.path("created_at"), instanceOf(String.class));
        assertThat((String) response.path("created_at"),
                matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{3})?[+-]\\d{2}:\\d{2}"));
    }

    @Test
    @DisplayName("TC36: Проверка уникальности id")
    void shouldReturnUniqueIdsForEachFavorite() {
        String token = getToken();
        System.out.println("Token: " + token);

        long place1 = given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "ТЦ МореМолл")
                .formParam("lat", 5.751826)
                .formParam("lon", 90.154929)
                .formParam("color", "YELLOW")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(200)
                .extract().path("id");

        long place2 = given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "ТЦ Планета")
                .formParam("lat", 84.001826)
                .formParam("lon", 55.154929)
                .formParam("color", "GREEN")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(200)
                .extract().path("id");

        assertNotEquals(place1,place2);
    }

    @Test
    @DisplayName("TC37: Проверка возрастающего id")
    void shouldReturnIncrementedIdsForNewFavorites() {
        String token = getToken();
        System.out.println("Token: " + token);

        long place1 = given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "ТЦ МореМолл")
                .formParam("lat", 5.751826)
                .formParam("lon", 90.154929)
                .formParam("color", "RED")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(200)
                .extract().path("id");

        long place2 = given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "ТЦ Планета")
                .formParam("lat", 84.001826)
                .formParam("lon", 55.154929)
                .formParam("color", "GREEN")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(200)
                .extract().path("id");

        assertTrue(place2 > place1);
    }

    @Test
    @DisplayName("TC38: Проверка даты, времени и таймзоны created_at")
    void shouldMatchCreatedAtToLocalTimeAndTimezone() {
        String token = getToken();
        OffsetDateTime nowMoscow = OffsetDateTime.now(ZoneOffset.of("+03:00"));

        String response = given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "ТЦ Планета")
                .formParam("lat", 5.751826)
                .formParam("lon", 90.154929)
                .formParam("color", "GREEN")
                .log().all()
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(200)
                .extract().path("created_at");

        OffsetDateTime createdAt = OffsetDateTime.parse(response, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        //Проверка таймзоны
        ZoneOffset expectedOffset = ZoneOffset.of("+03:00");
        ZoneOffset actualOffset = createdAt.getOffset();
        String actualOffsetStr = actualOffset.toString().equals("Z") ? "+00:00" : actualOffset.toString();
        assertThat("Смещение по таймзоне должно быть +03:00, получено " + actualOffsetStr,
                actualOffsetStr, equalTo(expectedOffset.toString()));

        //Проверка даты
        assertThat("Дата создания не совпадает с текущей", createdAt.toLocalDate(), equalTo(nowMoscow.toLocalDate()));

        //Проверка времени (±60 секунд)
        long secondsDiff = ChronoUnit.SECONDS.between(nowMoscow.toLocalTime(), createdAt.toLocalTime());
        assertThat("Разница во времени превышает 60 секунд", Math.abs(secondsDiff), lessThanOrEqualTo(60L));
    }

    @Test
    @DisplayName("TC39: Токен истёк — ожидается 401 Unauthorized")
    void shouldRejectExpiredToken() throws InterruptedException {
        String token = getToken();
        Thread.sleep(2001); // токен действителен 2000 мс

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Просроченный токен")
                .formParam("lat", 55.7558)
                .formParam("lon", 37.6173)
                .formParam("color", "RED")
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("TC40: Отсутствие токена — ожидается 401 Unauthorized")
    void shouldTokenMissing() {

        given()
                .baseUri("https://regions-test.2gis.com")
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Отсутствие токена")
                .formParam("lat", 55.7558)
                .formParam("lon", 37.6173)
                .formParam("color", "RED")
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("TC41: Неверный регистр параметра title")
    void shouldRejectIncorrectTitleCase() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("Title", "Парк")
                .formParam("lat", 55.7558)
                .formParam("lon", 37.6173)
                .formParam("color", "RED")
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC42: Неверный регистр параметра lat")
    void shouldRejectIncorrectLatCase() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Парк")
                .formParam("Lat", 55.7558)
                .formParam("lon", 37.6173)
                .formParam("color", "RED")
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC43: Неверный регистр параметра lon")
    void shouldRejectIncorrectLonCase() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Парк")
                .formParam("lat", 55.7558)
                .formParam("Lon", 37.6173)
                .formParam("color", "RED")
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("TC44: Неверный регистр параметра color")
    void shouldRejectIncorrectColorCase() {
        String token = getToken();

        given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "Парк")
                .formParam("lat", 55.7558)
                .formParam("lon", 37.6173)
                .formParam("Color", "RED")
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(200)
                .body("color", equalTo(null));
    }

    @Test
    @DisplayName("TC45: Проверка структуры ошибки при 400")
    void shouldReturnStructuredError() {
        String token = getToken();

        Response response = given()
                .baseUri("https://regions-test.2gis.com")
                .header("Cookie", "token=" + token)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "")
                .formParam("lat", 55.7558)
                .formParam("lon", 37.6173)
                .when()
                .post("/v1/favorites")
                .then()
                .log().all()
                .statusCode(400)
                .extract().response();

        // Проверяем структуру ошибки
        assertThat(response.body().asString(), anyOf(
                containsString("error"),
                containsString("id"),
                containsString("message")
        ));
    }
}