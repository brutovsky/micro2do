package com.brtvsk.todoservice;

import com.brtvsk.todoservice.model.dto.ImmutableUpdateTodoRequest;
import com.brtvsk.todoservice.model.dto.ImmutableTodoRequest;
import com.brtvsk.todoservice.model.dto.UpdateTodoRequest;
import com.brtvsk.todoservice.model.dto.TodoRequest;
import com.brtvsk.todoservice.model.dto.TodoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MongoDBContainer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static com.brtvsk.todoservice.TodoTestUtils.IS_DONE;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_DESCRIPTION;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_TAGS;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_TITLE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TodoControllerIT.MongoDbInitializer.class)
class TodoControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    private static final MongoDBContainer MONGO_DB_CONTAINER;

    static {
        MONGO_DB_CONTAINER = new MongoDBContainer("mongo:5.0");
        MONGO_DB_CONTAINER.start();
    }

    private static final String BASE_PATH = "/api/v1/todo/";

    @LocalServerPort
    void savePort(final int port) {
        RestAssured.port = port;
        RestAssured.basePath = BASE_PATH;
    }

    @Test
    void shouldPostTodo() throws Exception {
        TodoRequest todoCreationRequest = ImmutableTodoRequest.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .tags(TEST_TAGS)
                .build();

        final String jsonRequest = objectMapper.writeValueAsString(todoCreationRequest);

        TodoResponse response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonRequest)
                .when()
                .post()
                .as(TodoResponse.class);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getCreationTime()).isNotNull();
        assertThat(response.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(response.getDescription()).contains(TEST_DESCRIPTION);
        assertThat(response.getDone()).isEqualTo(IS_DONE);
        assertThat(response.getTags()).containsAll(TEST_TAGS);
        assertThat(response.getCompletionTime()).isNotPresent();
    }

    @Test
    void shouldGetAllTodo() throws JsonProcessingException {
        TodoRequest todoCreationRequest = ImmutableTodoRequest.builder()
                .title(TEST_TITLE)
                .build();

        final String jsonRequest = objectMapper.writeValueAsString(todoCreationRequest);

        TodoResponse postResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonRequest)
                .when()
                .post()
                .as(TodoResponse.class);

        var getResponse = List.of(RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get()
                .as(TodoResponse[].class));

        assertThat(getResponse)
                .hasSizeGreaterThan(1)
                .anyMatch(dto -> dto.getId().equals(postResponse.getId()));
    }

    @Test
    void shouldGetTodo() throws JsonProcessingException {
        TodoRequest todoCreationRequest = ImmutableTodoRequest.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .tags(TEST_TAGS)
                .build();

        final String jsonRequest = objectMapper.writeValueAsString(todoCreationRequest);

        TodoResponse postResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonRequest)
                .when()
                .post()
                .as(TodoResponse.class);

        var getResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/" + postResponse.getId())
                .as(TodoResponse.class);

        assertThat(getResponse.getId()).isEqualTo(postResponse.getId());
        assertThat(getResponse.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(getResponse.getDescription()).contains(TEST_DESCRIPTION);
        assertThat(getResponse.getDone()).isEqualTo(IS_DONE);
        assertThat(getResponse.getTags()).containsAll(TEST_TAGS);
    }

    @Test
    void shouldUpdateTodo() throws JsonProcessingException {
        String changedTitle = "Changed title";
        String changedDescription = "Changed description";

        TodoRequest todoCreationRequest = ImmutableTodoRequest.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .build();

        UpdateTodoRequest todoUpdateRequest = ImmutableUpdateTodoRequest.builder()
                .title(changedTitle)
                .description(changedDescription)
                .build();

        final String jsonPostRequest = objectMapper.writeValueAsString(todoCreationRequest);
        final String jsonPatchRequest = objectMapper.writeValueAsString(todoUpdateRequest);

        TodoResponse postResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPostRequest)
                .when()
                .post()
                .as(TodoResponse.class);

        var updateResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPatchRequest)
                .when()
                .patch("/" + postResponse.getId())
                .as(TodoResponse.class);

        assertThat(updateResponse.getId()).isEqualTo(postResponse.getId());
        assertThat(updateResponse.getTitle()).isEqualTo(changedTitle);
        assertThat(updateResponse.getDescription()).contains(changedDescription);
        assertThat(updateResponse.getDone()).isEqualTo(postResponse.getDone());
        assertThat(updateResponse.getTags()).isEqualTo(postResponse.getTags());
        assertThat(updateResponse.getCreationTime()).isEqualTo(postResponse.getCreationTime());
        assertThat(updateResponse.getCompletionTime()).isEqualTo(postResponse.getCompletionTime());
    }

    @Test
    void shouldReplaceTodo() throws JsonProcessingException {
        String changedTitle = "Changed title";
        String changedDescription = "Changed description";
        Date changedCreationTime = Date.from(Instant.now());
        Date changedCompletionTime = Date.from(changedCreationTime.toInstant().plus(1, ChronoUnit.HOURS));

        TodoRequest todoCreationRequest = ImmutableTodoRequest.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .build();

        TodoRequest todoReplaceRequest = ImmutableTodoRequest.builder()
                .title(changedTitle)
                .description(changedDescription)
                .done(Boolean.TRUE)
                .creationTime(changedCreationTime)
                .completionTime(changedCompletionTime)
                .build();

        final String jsonPostRequest = objectMapper.writeValueAsString(todoCreationRequest);
        final String jsonPutRequest = objectMapper.writeValueAsString(todoReplaceRequest);

        TodoResponse postResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPostRequest)
                .when()
                .post()
                .as(TodoResponse.class);

        var updateResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPutRequest)
                .when()
                .put("/" + postResponse.getId())
                .as(TodoResponse.class);

        assertThat(updateResponse.getId()).isEqualTo(postResponse.getId());
        assertThat(updateResponse.getTitle()).isEqualTo(changedTitle);
        assertThat(updateResponse.getDescription()).contains(changedDescription);
        assertThat(updateResponse.getDone()).isEqualTo(Boolean.TRUE);
        assertThat(updateResponse.getCreationTime()).isEqualTo(changedCreationTime);
        assertThat(updateResponse.getCompletionTime()).contains(changedCompletionTime);
    }

    @Test
    void shouldDeleteTodo() throws JsonProcessingException {
        TodoRequest todoCreationRequest = ImmutableTodoRequest.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .build();

        final String jsonPostRequest = objectMapper.writeValueAsString(todoCreationRequest);

        TodoResponse postResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPostRequest)
                .when()
                .post()
                .as(TodoResponse.class);

        Response response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPostRequest)
                .when()
                .delete("/" + postResponse.getId())
                .thenReturn();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPostRequest)
                .when()
                .get("/" + postResponse.getId())
                .thenReturn();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    static class MongoDbInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    "spring.data.mongodb.uri=" + MONGO_DB_CONTAINER.getReplicaSetUrl()
            );
            values.applyTo(configurableApplicationContext);
        }
    }
}
