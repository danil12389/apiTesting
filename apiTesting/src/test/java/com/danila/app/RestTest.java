package com.danila.app;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.Matchers.hasSize;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Test;

import java.util.ArrayList;

public class RestTest {

    private final String baseUrl = "https://jsonplaceholder.typicode.com";

    private final RequestSpecification requestSpec = given().baseUri(baseUrl);

    @Test
    public void getFirstPost() {
        requestSpec.given()
               .baseUri(baseUrl)
               .when().get("/posts/1")
               .then().statusCode(200)
               .body("userId", equalTo(1))
               .body("id", equalTo(1))
               .body("title", equalTo("sunt aut facere repellat provident occaecati excepturi optio reprehenderit"))
               .body("body", equalTo("quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"));
    }

    @Test
    public void getNonExistentPost() {
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
}
