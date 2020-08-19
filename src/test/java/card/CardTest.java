package card;

import base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import java.util.List;
import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class CardTest extends BaseTest {

    @Test
    @Order(1)
    public void moveCardFromFirstListToSecond() {

        Response response = given()
                .spec(reqspec)
                .queryParam("idList", secondListId)
                .when()
                .put(BASE_URL + "/" + CARDS + "/" + cardId)
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("idList")).isEqualTo(secondListId);

        Response responseGetCardsFirstList = given()
                .spec(reqspec)
                .when()
                .get(BASE_URL + "/" + LISTS + "/" + firstListId + "/" + CARDS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath jsonGetCardsFirstList = responseGetCardsFirstList.jsonPath();
        List<Object> idCardFirstList = jsonGetCardsFirstList.getList("id");
        Assertions.assertThat(idCardFirstList).hasSize(0);

        Response responseGetCardsSecondList = given()
                .spec(reqspec)
                .when()
                .get(BASE_URL + "/" + LISTS + "/" + secondListId + "/" + CARDS)
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath jsonGetCardsSecondList = responseGetCardsSecondList.jsonPath();
        List<Object> idCardSecondList = jsonGetCardsSecondList.getList("id");
        Assertions.assertThat(idCardSecondList).hasSize(1);
    }

    @Test
    @Order(2)
    public void addAttachmentToCard() {
        Response response = given()
                .spec(reqspec)
                .queryParam("url", attachmentUrl)
                .when()
                .post(BASE_URL + "/" + CARDS + "/" + cardId + "/" + ATTACHMENTS)
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("url")).isEqualTo(attachmentUrl);

        attachmentId = json.getString("id");
    }

    @Test
    @Order(3)
    public void addChecklistToCard() {
        Response response = given()
                .spec(reqspec)
                .queryParam("idCard", cardId)
                .queryParam("name", checklistName)
                .when()
                .post(BASE_URL + "/" + CHECKLISTS)
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("idCard")).isEqualTo(cardId);
        Assertions.assertThat(json.getString("name")).isEqualTo(checklistName);

        checklistId = json.getString("id");

        Response responseGetChecklist = given()
                .spec(reqspec)
                .when()
                .get(BASE_URL + "/" + CARDS + "/" + cardId + "/" + CHECKLISTS)
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath jsonGetChecklist = responseGetChecklist.jsonPath();
        List<Object> listOfChecklist = jsonGetChecklist.getList("id");
        Assertions.assertThat(listOfChecklist).hasSize(1);
    }

    @Test
    @Order(4)
    public void addCheckitemsToChecklist() {
        Response response = given()
                .spec(reqspec)
                .queryParam("name", checkitemName)
                .when()
                .post(BASE_URL + "/" + CHECKLISTS + "/" + checklistId + "/" + CHECKITEMS)
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo(checkitemName);

        checkitemId = json.getString("id");

        Response responseGetCheckitemList = given()
                .spec(reqspec)
                .when()
                .get(BASE_URL + "/" + CHECKLISTS + "/" + checklistId + "/" + CHECKITEMS)
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath jsonGetCheckitemList = responseGetCheckitemList.jsonPath();
        List<Object> checkitemList = jsonGetCheckitemList.getList("id");
        Assertions.assertThat(checkitemList).hasSize(1);
    }

    @Test
    @Order(5)
    public void addCommentOnCard() {
        Response response = given()
                .spec(reqspec)
                .queryParam("text", textComment)
                .when()
                .post(BASE_URL + "/" + CARDS + "/" + cardId + "/" + ACTIONS + "/" + COMMENTS)
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("data.text")).isEqualTo(textComment);
        Assertions.assertThat(json.getString("data.card.id")).isEqualTo(cardId);
        Assertions.assertThat(json.getString("data.list.id")).isEqualTo(secondListId);
        Assertions.assertThat(json.getString("data.board.id")).isEqualTo(boardId);

        commentId = json.getString("id");
    }

    @Test
    @Order(6)
    public void createNewLabelOnCard() {
        Response response = given()
                .spec(reqspec)
                .queryParam("name", "target")
                .queryParam("color", "pink")
                .when()
                .post(BASE_URL + "/" + CARDS + "/" + cardId + "/" + LABELS)
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("target");
        Assertions.assertThat(json.getString("color")).isEqualTo("pink");

        labelId = json.getString("id");
    }

    @Test
    @Order(7)
    public void addMemberOnCard() {
        given()
                .spec(reqspec)
                .queryParam("value", mainMemberId)
                .when()
                .post(BASE_URL + "/" + CARDS + "/" + cardId + "/" + IDMEMBERS)
                .then()
                .spec(respspec)
                .extract()
                .response();

        Response response = given()
                .spec(reqspec)
                .when()
                .get(BASE_URL + "/" + CARDS + "/" + cardId + "/" + MEMBERS)
                .then()
                .spec(respspec)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        List<Object> idList = json.getList("id");
        Assertions.assertThat(idList).hasSize(1).contains(mainMemberId);
    }

    @Test
    @Order(8)
    public void delateBoard() {
        given()
                .spec(reqspec)
                .when()
                .delete(BASE_URL + "/" + BOARDS + "/" + boardId)
                .then()
                .spec(respspec);
    }
}
