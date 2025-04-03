package org.example.app.task.service;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;

import io.quarkus.test.junit.TestProfile;
import org.example.app.task.resource.KeycloakTokenProvider;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * E2E black-box test of the To-Do service only via its public REST resource.
 */
@QuarkusIntegrationTest
@TestMethodOrder(OrderAnnotation.class)
@TestProfile(IntegrationTestProfile.class)
@TestInstance(Lifecycle.PER_CLASS)
class TaskServiceIT {

    private Integer taskListId;

    private Integer taskItemId;

    private String token;

    @BeforeAll
    void getJwt() {
        token = KeycloakTokenProvider.getAccessTokenWithAdmin();
    }

    @Test
    @Order(1)
    void shouldAllowCreatingANewTaskList() {

        Response response = given().when().header("Authorization", token).body("{ \"title\": \"Shopping List\" }").contentType(ContentType.JSON)
                .post("/task/list");
        response.then().statusCode(201).header("Location", not(emptyString()));

        this.taskListId = Integer.parseInt(response.header("Location").replaceAll(".*?/task/list/", ""));
    }

    @Test
    @Order(2)
    void shouldAllowAddingATaskToATaskList() {

        Response response = given().when().header("Authorization", token).body("{ \"title\": \"Buy Milk\", \"taskListId\": " + this.taskListId + " }")
                .contentType(ContentType.JSON).post("/task/item");

        response.then().statusCode(201).header("Location", not(emptyString()));

        this.taskItemId = Integer.parseInt(response.header("Location").replaceAll(".*?/task/item/", ""));
    }

    @Test
    @Order(3)
    void shouldAllowRetrievingATaskListWithTaskItems() {

        given().when().header("Authorization", token).get("/task/list-with-items/{taskListId}", this.taskListId).then().statusCode(200)
                .body("list.title", Matchers.equalTo("Shopping List")).and().body("list.id", Matchers.equalTo(this.taskListId))
                .and().body("items[0].title", Matchers.equalTo("Buy Milk"));
    }

    @Test
    @Order(4)
    void shouldAllowDeletingATaskListCompletely() {

        given().when().header("Authorization", token).delete("/task/list/{taskListId}", this.taskListId).then().statusCode(204);
        given().when().header("Authorization", token).get("/task/list/{taskListId}", this.taskListId).then().statusCode(404);
        given().when().header("Authorization", token).get("/task/item/{itemId}", this.taskItemId).then().statusCode(404);

    }
}
