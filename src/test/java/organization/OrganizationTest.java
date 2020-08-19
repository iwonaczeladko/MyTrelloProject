package organization;

import base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class OrganizationTest extends BaseTest {

    public static Stream<Arguments> createOrganizationWithValidData() {
        return Stream.of(
                Arguments.of("This is displayName", "This is desc", "run_1", "http://icz.pl"),
                Arguments.of("This is displayName", "This is desc", "run_1", "https://icz.pl"),
                Arguments.of("This is displayName", "This is desc", "run", "https://icz.pl"),
                Arguments.of("This is displayName", "This is desc", "ru_", "https://icz.pl"),
                Arguments.of("This is displayName", "This is desc", "r_1", "https://icz.pl")
        );
    }

    public static Stream<Arguments> createOrganizationWithInvalidData() {
        return Stream.of(
                Arguments.of("This is displayName", "This is desc", "ru", "http://icz.pl"),
                Arguments.of("This is displayName", "This is desc", "run _1", "http://icz.pl"),
                Arguments.of("This is displayName", "This is desc", "RUN_1", "http://icz.pl"),
                Arguments.of("This is displayName", "This is desc", "Random_1", "http://icz.pl"),
                Arguments.of("This is displayName", "This is desc", "run_1!@-", "http://icz.pl"),
                Arguments.of("This is displayName", "This is desc", firstOrganizationName, "https://icz.pl")
        );
    }

    public static Stream<Arguments> updateOrganizationWithValidData() {
        return Stream.of(
                Arguments.of("T", "This is desc", "run_1", "http://icz.pl"),
                Arguments.of("This is displayName", "This is desc", "run_1", "http://icz.pl"),
                Arguments.of("This is displayName", "This is desc", "run_1", "https://icz.pl"),
                Arguments.of("This is displayName", "This is desc", "run_1", "icz.pl")
        );
    }

    public static Stream<Arguments> updateOrganizationWithInvalidData() {
        return Stream.of(
                Arguments.of("", "This is desc", "run_1", "http://icz.pl"),
                Arguments.of(" This is displayName", "This is desc", "run_1", "http://icz.pl"),
                Arguments.of("This is displayName ", "This is desc", "run_1", "http://icz.pl"),
                Arguments.of("This is displayName", "This is desc", "ru", "http://icz.pl"),
                Arguments.of("This is displayName", "This is desc", "run", "http://icz.pl"),
                Arguments.of("This is displayName", "This is desc", "run1", "http://icz.pl"),
                Arguments.of("This is displayName", "This is desc", "run_", "http://icz.pl"),
                Arguments.of("This is displayName", "This is desc", "ruN_1", "http://icz.pl"),
                Arguments.of("This is displayName", "This is desc", "RUN_1", "http://icz.pl"),
                Arguments.of("This is displayName", "This is desc", firstOrganizationName, "http://icz.pl")
        );
    }

    @Test
    @Order(1)
    public void addOrganizationWithoutDisplayName() {

        given()
                .spec(reqspec)
                .queryParam("displayName", "")
                .when()
                .post(BASE_URL + "/" + ORGANIZATIONS)
                .then()
                .statusCode(400);
    }

    @Test
    @Order(2)
    public void createFirstOrganization() {

        Response response = given()
                .spec(reqspec)
                .queryParam("displayName", "Name for Organization")
                .queryParam("name", "test_30")
                .when()
                .post(BASE_URL + "/" + ORGANIZATIONS)
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
//        System.out.println(json.getString("name"));

        Assertions.assertThat(json.getString("displayName")).isEqualTo("Name for Organization");
        Assertions.assertThat(json.getString("name"))
                .isEqualTo("test_30")
                .isLowerCase()
                .hasSizeGreaterThan(2);

        firstOrganizationId = json.getString("id");
        firstOrganizationName = json.getString("name");

        given()
                .spec(reqspec)
                .when()
                .get(BASE_URL + "/" + ORGANIZATIONS + "/" + firstOrganizationId)
                .then()
                .statusCode(200);
    }

    @Test
    @Order(3)
    public void createSecondOrganization() {
        Response response = given()
                .spec(reqspec)
                .queryParam("displayName", "Second Organization")
                .when()
                .post(BASE_URL + "/" + ORGANIZATIONS)
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        secondOrganizationId = json.getString("id");
    }

    @DisplayName("update organization with valid data")
    @ParameterizedTest(name = "displayName: {0}, desc: {1}, name: {2}, website: {3}")
    @MethodSource("updateOrganizationWithValidData")
    @Order(4)
    public void updateSecondOrganizationWithValidData(String displayName, String desc, String name, String website) {
        Response response1 = given()
                .spec(reqspec)
                .queryParam("displayName", displayName)
                .queryParam("desc", desc)
                .queryParam("name", name)
                .queryParam("website", website)
                .when()
                .put(BASE_URL + "/" + ORGANIZATIONS + "/" + secondOrganizationId)
                .then()
                .spec(respspec)
                .extract()
                .response();

        boolean websiteStartWithHttp = false;

        JsonPath json = response1.jsonPath();
        if (json.getString("website").startsWith("http://")) {
            websiteStartWithHttp = true;
        } else if (json.getString("website").startsWith("https://")) {
            websiteStartWithHttp = true;
        }

        Assertions.assertThat(json.getString("displayName")).isEqualTo(displayName);
        Assertions.assertThat(json.getString("desc")).isEqualTo(desc);
        Assertions.assertThat(json.getString("name")).isEqualTo(name);
        Assertions.assertThat(websiteStartWithHttp).isEqualTo(true);
    }

    @DisplayName("update organization with invalid data")
    @ParameterizedTest(name = "displayName: {0}, desc: {1}, name: {2}, website: {3}")
    @MethodSource("updateOrganizationWithInvalidData")
    @Order(5)
    public void updateSecondOrganizationWithInvalidData(String displayName, String desc, String name, String website) {
        given()
                .spec(reqspec)
                .queryParam("displayName", displayName)
                .queryParam("desc", desc)
                .queryParam("name", name)
                .queryParam("website", website)
                .when()
                .put(BASE_URL + "/" + ORGANIZATIONS + "/" + secondOrganizationId)
                .then()
                .statusCode(400);
    }

    @Test
    @Order(6)
    public void deleteSecondOrganization() {
        given()
                .spec(reqspec)
                .when()
                .delete(BASE_URL + "/" + ORGANIZATIONS + "/" + secondOrganizationId)
                .then()
                .statusCode(200);
    }

/*
    @DisplayName("Create organization with valid data")
    @ParameterizedTest (name = "displayName: {0}, desc: {1}, name: {2}, website: {3}")
    @MethodSource("createOrganizationWithValidData")
    @Order(7)
    public void createSecondOrganizationsWithValidData(String displayName, String desc, String name, String website) {

        Response response = given()
                .spec(reqspec)
                .queryParam("displayName", displayName)
                .queryParam("desc", desc)
                .queryParam("name", name)
                .queryParam("website", website)
                .when()
                .post(BASE_URL + "/" + ORGANIZATIONS)
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("displayName")).isEqualTo(displayName);
        Assertions.assertThat(json.getString("desc")).isEqualTo(desc);
        Assertions.assertThat(json.getString("name")).isEqualTo(name);
        Assertions.assertThat(json.getString("website")).isEqualTo(website);

        secondOrganizationId = json.getString("id");

        given()
                .spec(reqspec)
                .when()
                .get(BASE_URL + "/" + ORGANIZATIONS + "/" + secondOrganizationId)
                .then()
                .statusCode(200);

        given()
                .spec(reqspec)
                .when()
                .delete(BASE_URL + "/" + ORGANIZATIONS + "/" + secondOrganizationId)
                .then()
                .statusCode(200);
    }
*/

/*    @DisplayName("Create organization with invalid data")
    @ParameterizedTest (name = "displayName: {0}, desc: {1}, name: {2}, website: {3}")
    @MethodSource("createOrganizationWithInvalidData")
    @Order(8)
    public void createSecondOrganizationsWithInvalidData(String displayName, String desc, String name, String website) {

        given()
                .spec(reqspec)
                .queryParam("displayName", displayName)
                .queryParam("desc", desc)
                .queryParam("name", name)
                .queryParam("website", website)
                .when()
                .post(BASE_URL + "/" + ORGANIZATIONS)
                .then()
                .statusCode(400);
    }*/

    @Test
    @Order(9)
    public void deleteFirstOrganization() {
        given()
                .spec(reqspec)
                .when()
                .delete(BASE_URL + "/" + ORGANIZATIONS + "/" + firstOrganizationId)
                .then()
                .statusCode(200);
    }
}
