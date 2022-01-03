package com.danila.app;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.Assertions;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class RestTest {

    private final RequestSpecification requestSpec = given().baseUri("https://jsonplaceholder.typicode.com");

    private final String requestBody =  "{\n" +
            "  \"title\": \"foo\",\n" +
            "  \"body\": \"bar\",\n" +
            "  \"userId\": \"1\" \n}";

    private final String requestBodyBrokenId =  "{\n" +
            "  \"title\": \"foo\",\n" +
            "  \"body\": \"bar\",\n" +
            "  \"userId\": \"1\",\n" +
            "  \"Id\": \"-1\" \n}";

    @Test
    public void getFirstPost() {
        requestSpec.given()
               .when().get("/posts/1")
               .then().statusCode(200)
               .body("userId", equalTo(1))
               .body("id", equalTo(1))
               .body("title", equalTo("sunt aut facere repellat provident occaecati excepturi optio reprehenderit"))
               .body("body", equalTo("quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"));
    }

    @Test
    public void getNonExistentPost() {
        //притворимся, что у нас каждый раз возвращается рандомное количество постов, а не 100 :)
        Response response =
                 requestSpec.
                 given().
                 when().
                 get("/posts").
                 then().
                 extract().
                 response();

        int sizeOfList = response.body().path("list.size()");

        requestSpec.given()
                 .when().get("/posts/"+sizeOfList + 1 +"")
                 .then().statusCode(404);
    }

    @Test
    public void getPostBasedOnBrokenUri() {
        given()
                .baseUri("https://jsonplaceholder.typicode.com/posts/11%43")
                .when().get()
                .then()
                .statusCode(404);
    }

    @Test
    public void addPost() {
        Response response = requestSpec.given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/posts")
                .then()
                .extract().response();

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("foo", response.jsonPath().getString("title"));
        Assertions.assertEquals("bar", response.jsonPath().getString("body"));
        Assertions.assertEquals("1", response.jsonPath().getString("userId"));
        Assertions.assertEquals("101", response.jsonPath().getString("id"));
    }

    @Test
    public void addPostWithBrokenId() {
        Response response = requestSpec.given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBodyBrokenId)
                .when()
                .post("/posts")
                .then()
                .extract().response();

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("foo", response.jsonPath().getString("title"));
        Assertions.assertEquals("bar", response.jsonPath().getString("body"));
        Assertions.assertEquals("1", response.jsonPath().getString("userId"));
        Assertions.assertEquals("101", response.jsonPath().getString("id"));
    }
}
