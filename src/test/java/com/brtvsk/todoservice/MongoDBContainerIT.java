package com.brtvsk.todoservice;

import com.brtvsk.testsmanager.annotations.Integration;
import com.brtvsk.testsmanager.annotations.Smoke;
import com.brtvsk.todoservice.repository.TodoRepository;
import com.brtvsk.todoservice.repository.TodoRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Integration
@Smoke
@Testcontainers
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
@AutoConfigureMockMvc
class MongoDBContainerIT {

    @Autowired
    private MongoTemplate mongoTemplate;
    private TodoRepository todoRepository;

    @Container
    private static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer("mongo:5.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        this.todoRepository = new TodoRepositoryImpl(mongoTemplate);
    }

    @Test
    void containerIsUp() {
        assertTrue(MONGO_DB_CONTAINER.isRunning());
    }

    @Test
    void repositoryIsNotNull() {
        assertThat(this.mongoTemplate).isNotNull();
        assertThat(this.todoRepository).isNotNull();
    }

}
