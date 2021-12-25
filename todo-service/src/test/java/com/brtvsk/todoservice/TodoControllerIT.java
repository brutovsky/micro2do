package com.brtvsk.todoservice;

import com.brtvsk.todoservice.model.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MongoDBContainer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static com.brtvsk.todoservice.TodoTestUtils.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TodoControllerIT.MongoDbInitializer.class)
public class TodoControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    private static final MongoDBContainer MONGO_DB_CONTAINER;

    static {
        MONGO_DB_CONTAINER = new MongoDBContainer("mongo:4.4.2");
        MONGO_DB_CONTAINER.start();
    }

    private static final String BASE_PATH = "/api/todo/";

    @LocalServerPort
    void savePort(final int port) {
        RestAssured.port = port;
        RestAssured.basePath = BASE_PATH;
    }

    @Test
    public void shouldPostTodo() throws Exception {
        RequestTodoDto todoCreationRequest = ImmutableRequestTodoDto.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .tags(TEST_TAGS)
                .build();

        final String jsonRequest = objectMapper.writeValueAsString(todoCreationRequest);

        ResponseTodoDto response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonRequest)
                .when()
                .post()
                .as(ResponseTodoDto.class);

        Assertions.assertThat(response.getId()).isNotNull();
        Assertions.assertThat(response.getCreationTime()).isNotNull();
        Assertions.assertThat(response.getTitle()).isEqualTo(TEST_TITLE);
        Assertions.assertThat(response.getDescription()).contains(TEST_DESCRIPTION);
        Assertions.assertThat(response.getDone()).isEqualTo(IS_DONE);
        Assertions.assertThat(response.getTags()).contains(TEST_TAGS);
        Assertions.assertThat(response.getCompletionTime()).isNotPresent();
    }

    @Test
    public void shouldGetAllTodo() throws JsonProcessingException {
        RequestTodoDto todoCreationRequest = ImmutableRequestTodoDto.builder()
                .title(TEST_TITLE)
                .build();

        final String jsonRequest = objectMapper.writeValueAsString(todoCreationRequest);

        ResponseTodoDto postResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonRequest)
                .when()
                .post()
                .as(ResponseTodoDto.class);

        var getResponse = List.of(RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get()
                .as(ResponseTodoDto[].class));

        Assertions.assertThat(getResponse).hasSizeGreaterThan(1);
        Assertions.assertThat(getResponse).anyMatch(dto -> dto.getId().equals(postResponse.getId()));
    }

    @Test
    public void shouldGetTodo() throws JsonProcessingException {
        RequestTodoDto todoCreationRequest = ImmutableRequestTodoDto.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .tags(TEST_TAGS)
                .build();

        final String jsonRequest = objectMapper.writeValueAsString(todoCreationRequest);

        ResponseTodoDto postResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonRequest)
                .when()
                .post()
                .as(ResponseTodoDto.class);

        var getResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/" + postResponse.getId())
                .as(ResponseTodoDto.class);

        Assertions.assertThat(getResponse.getId()).isEqualTo(postResponse.getId());
        Assertions.assertThat(getResponse.getTitle()).isEqualTo(TEST_TITLE);
        Assertions.assertThat(getResponse.getDescription()).contains(TEST_DESCRIPTION);
        Assertions.assertThat(getResponse.getDone()).isEqualTo(IS_DONE);
        Assertions.assertThat(getResponse.getTags()).contains(TEST_TAGS);
    }

    @Test
    public void shouldUpdateTodo() throws JsonProcessingException {
        String changedTitle = "Changed title";
        String changedDescription = "Changed description";

        RequestTodoDto todoCreationRequest = ImmutableRequestTodoDto.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .build();

        OptionalRequestTodoDto todoUpdateRequest = ImmutableOptionalRequestTodoDto.builder()
                .title(changedTitle)
                .description(changedDescription)
                .build();

        final String jsonPostRequest = objectMapper.writeValueAsString(todoCreationRequest);
        final String jsonPatchRequest = objectMapper.writeValueAsString(todoUpdateRequest);

        ResponseTodoDto postResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPostRequest)
                .when()
                .post()
                .as(ResponseTodoDto.class);

        var updateResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPatchRequest)
                .when()
                .patch("/" + postResponse.getId())
                .as(ResponseTodoDto.class);

        Assertions.assertThat(updateResponse.getId()).isEqualTo(postResponse.getId());
        Assertions.assertThat(updateResponse.getTitle()).isEqualTo(changedTitle);
        Assertions.assertThat(updateResponse.getDescription()).contains(changedDescription);
        Assertions.assertThat(updateResponse.getDone()).isEqualTo(postResponse.getDone());
        Assertions.assertThat(updateResponse.getTags()).isEqualTo(postResponse.getTags());
        Assertions.assertThat(updateResponse.getCreationTime()).isEqualTo(postResponse.getCreationTime());
        Assertions.assertThat(updateResponse.getCompletionTime()).isEqualTo(postResponse.getCompletionTime());
    }

    @Test
    public void shouldReplaceTodo() throws JsonProcessingException {
        String changedTitle = "Changed title";
        String changedDescription = "Changed description";
        Date changedCreationTime = Date.from(Instant.now());
        Date changedCompletionTime = Date.from(changedCreationTime.toInstant().plus(1, ChronoUnit.HOURS));

        RequestTodoDto todoCreationRequest = ImmutableRequestTodoDto.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .build();

        RequestTodoDto todoReplaceRequest = ImmutableRequestTodoDto.builder()
                .title(changedTitle)
                .description(changedDescription)
                .done(Boolean.TRUE)
                .creationTime(changedCreationTime)
                .completionTime(changedCompletionTime)
                .build();

        final String jsonPostRequest = objectMapper.writeValueAsString(todoCreationRequest);
        final String jsonPutRequest = objectMapper.writeValueAsString(todoReplaceRequest);

        ResponseTodoDto postResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPostRequest)
                .when()
                .post()
                .as(ResponseTodoDto.class);

        var updateResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPutRequest)
                .when()
                .put("/" + postResponse.getId())
                .as(ResponseTodoDto.class);

        Assertions.assertThat(updateResponse.getId()).isEqualTo(postResponse.getId());
        Assertions.assertThat(updateResponse.getTitle()).isEqualTo(changedTitle);
        Assertions.assertThat(updateResponse.getDescription()).contains(changedDescription);
        Assertions.assertThat(updateResponse.getDone()).isEqualTo(Boolean.TRUE);
        Assertions.assertThat(updateResponse.getCreationTime()).isEqualTo(changedCreationTime);
        Assertions.assertThat(updateResponse.getCompletionTime()).contains(changedCompletionTime);
    }

    @Test
    public void shouldDeleteTodo() throws JsonProcessingException {
        RequestTodoDto todoCreationRequest = ImmutableRequestTodoDto.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .build();

        final String jsonPostRequest = objectMapper.writeValueAsString(todoCreationRequest);

        ResponseTodoDto postResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPostRequest)
                .when()
                .post()
                .as(ResponseTodoDto.class);

        Response response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPostRequest)
                .when()
                .delete("/" + postResponse.getId())
                .thenReturn();

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPostRequest)
                .when()
                .get("/" + postResponse.getId())
                .thenReturn();

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    public static class MongoDbInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    "spring.data.mongodb.uri=" + MONGO_DB_CONTAINER.getReplicaSetUrl()
            );
            values.applyTo(configurableApplicationContext);
        }
    }
}
