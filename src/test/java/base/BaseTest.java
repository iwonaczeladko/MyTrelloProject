package base;

import com.github.javafaker.Faker;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    protected String BASE_URL = "https://api.trello.com/1";
    protected String BOARDS = "boards";
    protected String LISTS = "lists";
    protected String CARDS = "cards";
    protected String ATTACHMENTS = "attachments";
    protected String CHECKLISTS = "checklists";
    protected String CHECKITEMS = "checkItems";
    protected String ACTIONS = "actions";
    protected String COMMENTS = "comments";
    protected String LABELS = "labels";
    protected String MEMBERS = "members";
    protected String IDMEMBERS = "idMembers";
    protected String ORGANIZATIONS = "organizations";
    protected static String KEY = "KEY";
    protected static String TOKEN = "TOKEN";
    protected static String boardId;
    protected static String firstListId;
    protected static String secondListId;
    protected static String cardId;
    protected static String attachmentId;
    protected static String checklistId;
    protected static String checkitemId;
    protected static String commentId;
    protected static String labelId;
    protected static String mainMemberId;
    protected static String addedMemberId;
    protected static String firstOrganizationId;
    protected static String secondOrganizationId;
    protected static String firstOrganizationName;
    protected String boardName;
    protected String listName;
    protected String cardName;
    protected String attachmentUrl;
    protected String checklistName;
    protected String checkitemName;
    protected String textComment;
    protected String email;
    protected static Faker faker;
    protected static RequestSpecBuilder reqbuilder;
    protected static RequestSpecification reqspec;
    protected static ResponseSpecBuilder respbuilder;
    protected static ResponseSpecification respspec;

    @BeforeAll
    public static void beforeAll() {
        reqbuilder = new RequestSpecBuilder();
        reqbuilder.setContentType(ContentType.JSON);
        reqbuilder.addQueryParam("key", KEY);
        reqbuilder.addQueryParam("token", TOKEN);
        reqspec = reqbuilder.build();

        respbuilder = new ResponseSpecBuilder();
        respbuilder.expectStatusCode(200);
        respbuilder.expectContentType(ContentType.JSON);
        respspec = respbuilder.build();
        faker = new Faker();

        reqbuilder.addFilter(new AllureRestAssured());
    }

    @BeforeEach
    public void beforeEach() {
        boardName = faker.harryPotter().location();
        listName = faker.harryPotter().character();
        cardName = faker.harryPotter().quote();
        attachmentUrl = faker.internet().url();
        attachmentUrl = "http://" + attachmentUrl;
        checklistName = faker.book().author();
        checkitemName = faker.book().title();
        textComment = faker.harryPotter().spell();
        email = faker.internet().emailAddress();
    }
}
