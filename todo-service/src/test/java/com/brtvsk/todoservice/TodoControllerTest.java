package com.brtvsk.todoservice;

import com.brtvsk.todoservice.controller.TodoController;
import com.brtvsk.todoservice.model.dto.ImmutableRequestTodoDto;
import com.brtvsk.todoservice.model.dto.ImmutableResponseTodoDto;
import com.brtvsk.todoservice.model.dto.RequestTodoDto;
import com.brtvsk.todoservice.model.dto.ResponseTodoDto;
import com.brtvsk.todoservice.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    private RequestTodoDto todoCreationRequest;
    private ResponseTodoDto expectedTodoResponse;

    private final UUID testId = UUID.randomUUID();
    private final Date testCreationTime = Date.from(Instant.now());
    private final String testTitle = "Title 1";
    private final String testDescription = "Description 1";
    private final Boolean isDone = Boolean.FALSE;
    private final Set<String> testTags = Set.of("Tag 1", "Tag 2", "Tag 3");


    @Test
    void shouldCreateTodo() throws Exception {
        expectedTodoResponse = ImmutableResponseTodoDto
                .builder()
                .id(testId)
                .title(testTitle)
                .description(testDescription)
                .done(isDone)
                .tags(testTags)
                .creationTime(testCreationTime)
                .build();

        Mockito.when(todoService.create(any()))
                .thenReturn(expectedTodoResponse);

        todoCreationRequest = ImmutableRequestTodoDto.builder()
                .title(testTitle)
                .description(testDescription)
                .tags(testTags)
                .build();

        final String jsonRequest = objectMapper.writeValueAsString(todoCreationRequest);
        final String expectedResponse = objectMapper.writeValueAsString(expectedTodoResponse);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/todo")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));

        Mockito.verify(todoService).create(any());
        Mockito.verify(todoService).create(todoCreationRequest);
    }

}
