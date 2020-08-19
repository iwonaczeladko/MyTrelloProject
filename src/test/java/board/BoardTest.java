package board;

import base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import java.util.List;
import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class BoardTest extends BaseTest {

    @Test
    @Order(1)
    public void createNewBoardWithoutDefaultLists() {

        Response response = given()
                .spec(reqspec)
                .queryParam("name", boardName)
                .queryParam("defaultLists", false)
                .when()
                .post(BASE_URL + "/" + BOARDS)
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo(boardName);

        boardId = json.getString("id");

        Response responseGetLists = given()
                .spec(reqspec)
                .when()
                .get(BASE_URL + "/" + BOARDS + "/" + boardId + "/" + LISTS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath jsonGetLists = responseGetLists.jsonPath();
        List<Object> idList = jsonGetLists.getList("id");
        Assertions.assertThat(idList).hasSize(0);
    }

    @Test
    @Order(2)
    public void createFirstList() {

        Response response = given()
                .spec(reqspec)
                .queryParam("name", listName)
                .queryParam("idBoard", boardId)
                .when()
                .post(BASE_URL + "/" + LISTS)
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo(listName).isNotEmpty();
        Assertions.assertThat(json.getString("idBoard")).isEqualTo(boardId);

        firstListId = json.getString("id");
    }

    @Test
    @Order(3)
    public void createSecondList() {

        Response response = given()
                .spec(reqspec)
                .queryParam("name", listName)
                .queryParam("idBoard", boardId)
                .when()
                .post(BASE_URL + "/" + LISTS)
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo(listName).isNotEmpty();
        Assertions.assertThat(json.getString("idBoard")).isEqualTo(boardId);

        secondListId = json.getString("id");

        Response responseGetLists = given()
                .spec(reqspec)
                .when()
                .get(BASE_URL + "/" + BOARDS + "/" + boardId + "/" + LISTS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath jsonGetLists = responseGetLists.jsonPath();
        List<Object> idList = jsonGetLists.getList("id");
        Assertions.assertThat(idList).hasSize(2);
    }

    @Test
    @Order(4)
    public void addCardOnFirstList() {

        Response response = given()
                .spec(reqspec)
                .queryParam("name", cardName)
                .queryParam("idList", firstListId)
                .when()
                .post(BASE_URL + "/" + CARDS)
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo(cardName).isNotEmpty();
        Assertions.assertThat(json.getString("idList")).isEqualTo(firstListId);

        cardId = json.getString("id");

        Response responseGetCards = given()
                .spec(reqspec)
                .when()
                .get(BASE_URL + "/" + LISTS + "/" + firstListId + "/" + CARDS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath jsonGetCards = responseGetCards.jsonPath();
        List<Object> nameCard = jsonGetCards.getList("name");
        Assertions.assertThat(nameCard)
                .hasSize(1)
                .contains(cardName);
    }

    @Test
    @Order(5)
    public void inviteMemberToBoardViaEmail() {
        Response response = given()
                .spec(reqspec)
                .queryParam("email", email)
                .when()
                .put(BASE_URL + "/" + BOARDS + "/" + boardId + "/members")
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        mainMemberId = json.getString("memberships.idMember[0]");
        addedMemberId = json.getString("id");

        Response responseGetMember = given()
                .spec(reqspec)
                .when()
                .get(BASE_URL + "/" + BOARDS + "/" + boardId +  "/members")
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath jsonGetMember = responseGetMember.jsonPath();
        List<Object> memberIdList = jsonGetMember.getList("id");
        Assertions.assertThat(memberIdList).hasSize(2);
    }
}