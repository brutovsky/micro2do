package com.brtvsk.todoservice;

import com.brtvsk.testsmanager.annotations.HighPriorityTest;
import com.brtvsk.testsmanager.annotations.Integration;
import com.brtvsk.testsmanager.annotations.LowPriorityTest;
import com.brtvsk.testsmanager.annotations.Slow;
import com.brtvsk.todoservice.model.dto.ImmutableTodoRequest;
import com.brtvsk.todoservice.model.dto.ImmutableUpdateTodoRequest;
import com.brtvsk.todoservice.model.dto.TodoRequest;
import com.brtvsk.todoservice.model.dto.TodoResponse;
import com.brtvsk.todoservice.model.dto.UpdateTodoRequest;
import com.brtvsk.todoservice.utils.RestMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MongoDBContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.brtvsk.todoservice.KeycloakUtils.ADMIN_PASSWORD;
import static com.brtvsk.todoservice.KeycloakUtils.ADMIN_USERNAME;
import static com.brtvsk.todoservice.KeycloakUtils.REALM;
import static com.brtvsk.todoservice.KeycloakUtils.TEST_USER_PASSWORD;
import static com.brtvsk.todoservice.KeycloakUtils.TEST_USER_USERNAME;
import static com.brtvsk.todoservice.KeycloakUtils.getAccessToken;
import static com.brtvsk.todoservice.KeycloakUtils.getAdminAccessToken;
import static com.brtvsk.todoservice.TodoTestUtils.IS_DONE;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_ATTACHMENTS_REQUEST;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_ATTACHMENTS_RESPONSE;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_DESCRIPTION;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_TAGS;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_TITLE;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Slow
@Integration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TodoControllerIT.TestcontainersContextInitializer.class)
class TodoControllerIT {

    private static final String BASE_PATH = "/api/v1/todo/";
    private static final MongoDBContainer MONGO_DB_CONTAINER;
    private static final KeycloakContainer KEYCLOAK_CONTAINER;
    private static final String AUTH_SERVER_URL;

    private static String userAccessToken;

    @Autowired
    private ObjectMapper objectMapper;

    static {
        MONGO_DB_CONTAINER = new MongoDBContainer("mongo:5.0");
        MONGO_DB_CONTAINER.start();

        KEYCLOAK_CONTAINER = new KeycloakContainer("jboss/keycloak:16.0.0")
                .withRealmImportFile("realm-export.json")
                .withAdminUsername(ADMIN_USERNAME)
                .withAdminPassword(ADMIN_PASSWORD);
        KEYCLOAK_CONTAINER.start();
        AUTH_SERVER_URL = KEYCLOAK_CONTAINER.getAuthServerUrl();
    }

    @LocalServerPort
    void savePort(final int port) {
        RestAssured.port = port;
        RestAssured.basePath = BASE_PATH;
    }

    @BeforeAll
    public static void initTestKeycloakUser() throws IOException {
        String adminAccessToken = getAdminAccessToken(AUTH_SERVER_URL, ADMIN_USERNAME, ADMIN_PASSWORD);

        File file = new ClassPathResource("create-user.json").getFile();
        String createUserRequest = new String(Files.readAllBytes(file.toPath()));

        given()
                .auth().oauth2(adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createUserRequest)
                .when()
                .post(AUTH_SERVER_URL + "/admin/realms/micro2do-realm/users")
                .then()
                .assertThat().statusCode(HttpStatus.CREATED.value());

        String userId = given()
                .auth().oauth2(adminAccessToken)
                .body(createUserRequest)
                .when().get(AUTH_SERVER_URL + "/admin/realms/micro2do-realm/users/?username=user")
                .thenReturn()
                .jsonPath()
                .getString("id[0]");

        String updateUserRolesUrl = AUTH_SERVER_URL
                + "/admin/realms/micro2do-realm/users/"
                + userId
                + "/role-mappings/realm";
        file = new ClassPathResource("update-user-role-mapping.json").getFile();
        String updateUserRolesRequest = new String(Files.readAllBytes(file.toPath()));

        given()
                .auth().oauth2(adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(updateUserRolesRequest)
                .when().post(updateUserRolesUrl)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        userAccessToken = getAccessToken(AUTH_SERVER_URL, TEST_USER_USERNAME, TEST_USER_PASSWORD);
    }

    @HighPriorityTest
    void shouldPostTodo() throws Exception {
        TodoRequest todoCreationRequest = ImmutableTodoRequest.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .tags(TEST_TAGS)
                .build();

        final String jsonRequest = objectMapper.writeValueAsString(todoCreationRequest);

        var response = given()
                .auth().oauth2(userAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonRequest)
                .when()
                .post()
                .thenReturn()
                .as(TodoResponse.class);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getCreationTime()).isNotNull();
        assertThat(response.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(response.getDescription()).contains(TEST_DESCRIPTION);
        assertThat(response.getDone()).isEqualTo(IS_DONE);
        assertThat(response.getTags()).containsAll(TEST_TAGS);
        assertThat(response.getCompletionTime()).isNotPresent();
    }

    @HighPriorityTest
    void shouldGetAllTodo() throws JsonProcessingException {
        TodoRequest todoCreationRequest = ImmutableTodoRequest.builder()
                .title(TEST_TITLE)
                .build();

        final String jsonRequest = objectMapper.writeValueAsString(todoCreationRequest);

        var postResponse = given()
                .auth().oauth2(userAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonRequest)
                .when()
                .post()
                .as(TodoResponse.class);

        var getResponse = List.of(
                given()
                        .auth().oauth2(userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when()
                        .get()
                        .as(TodoResponse[].class)
        );

        assertThat(getResponse)
                .hasSizeGreaterThan(1)
                .anyMatch(dto -> dto.getId().equals(postResponse.getId()));
    }

    @HighPriorityTest
    void shouldGetTodo() throws JsonProcessingException {
        TodoRequest todoCreationRequest = ImmutableTodoRequest.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .tags(TEST_TAGS)
                .build();

        final String jsonRequest = objectMapper.writeValueAsString(todoCreationRequest);

        var postResponse = given()
                .auth().oauth2(userAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonRequest)
                .when()
                .post()
                .as(TodoResponse.class);

        var getResponse = given()
                .auth().oauth2(userAccessToken)
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

    @HighPriorityTest
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
                .attachments(TEST_ATTACHMENTS_REQUEST)
                .build();

        final String jsonPostRequest = objectMapper.writeValueAsString(todoCreationRequest);
        final String jsonPatchRequest = objectMapper.writeValueAsString(todoUpdateRequest);

        var postResponse = given()
                .auth().oauth2(userAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPostRequest)
                .when()
                .post()
                .as(TodoResponse.class);

        var updateResponse = given()
                .auth().oauth2(userAccessToken)
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
        assertThat(updateResponse.getAttachments()).containsAll(TEST_ATTACHMENTS_RESPONSE);
    }

    @HighPriorityTest
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

        var postResponse = given()
                .auth().oauth2(userAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPostRequest)
                .when()
                .post()
                .as(TodoResponse.class);

        var updateResponse = given()
                .auth().oauth2(userAccessToken)
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

    @HighPriorityTest
    void shouldDeleteTodo() throws JsonProcessingException {
        TodoRequest todoCreationRequest = ImmutableTodoRequest.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .build();

        final String jsonPostRequest = objectMapper.writeValueAsString(todoCreationRequest);

        var postResponse = given()
                .auth().oauth2(userAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPostRequest)
                .when()
                .post()
                .as(TodoResponse.class);

        var response = given()
                .auth().oauth2(userAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPostRequest)
                .when()
                .delete("/" + postResponse.getId())
                .thenReturn();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        response = given()
                .auth().oauth2(userAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonPostRequest)
                .when()
                .get("/" + postResponse.getId())
                .thenReturn();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @LowPriorityTest
    void shouldThrowTodoNotFoundException() {
        UUID id = UUID.randomUUID();

        var response = given()
                .auth().oauth2(userAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/" + id)
                .thenReturn();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

        var restMessage = response.as(RestMessage.class);

        assertThat(restMessage.getMessages()).isNotEmpty();
        assertThat(restMessage.getMessages().get(0)).contains(id.toString());
    }

    static class TestcontainersContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            String mongoDbProperty = "spring.data.mongodb.uri="
                    + MONGO_DB_CONTAINER.getReplicaSetUrl();
            String jwkSetUriPropery = "spring.security.oauth2.resourceserver.jwt.jwk-set-uri="
                    + AUTH_SERVER_URL
                    + "/realms/"
                    + REALM
                    + "/protocol/openid-connect/certs";
            TestPropertyValues values = TestPropertyValues.of(
                    mongoDbProperty,
                    jwkSetUriPropery
            );
            values.applyTo(configurableApplicationContext);
        }
    }
}
