package com.brtvsk.todoservice;

import com.brtvsk.todoservice.controller.TodoController;
import com.brtvsk.todoservice.model.TodoDto;
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

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    private TodoDto todoCreationRequest;
    private TodoDto expectedTodoResponse;

    private final String testId = "fd324dsfdsfsdf";
    private final Date testCreationTime = Date.from(Instant.now());
    private final String testTitle = "Title 1";
    private final String testDescription = "Description 1";
    private final Set<String> testTags = Set.of("Tag 1", "Tag 2", "Tag 3");


    @Test
    void shouldCreateTodo() throws Exception {
        expectedTodoResponse = new TodoDto();
        expectedTodoResponse.setTitle(testTitle);
        expectedTodoResponse.setDescription(testDescription);
        expectedTodoResponse.setTags(testTags);
        expectedTodoResponse.setId(testId);
        expectedTodoResponse.setCreationTime(testCreationTime);

        Mockito.when(todoService.create(any()))
                .thenReturn(expectedTodoResponse);

        todoCreationRequest = new TodoDto();
        todoCreationRequest.setTitle(testTitle);
        todoCreationRequest.setDescription(testDescription);
        todoCreationRequest.setTags(testTags);

        final String jsonRequest = objectMapper.writeValueAsString(todoCreationRequest);
        final String expectedResponse = objectMapper.writeValueAsString(expectedTodoResponse);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/todos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));

        Mockito.verify(todoService).create(any());
        Mockito.verify(todoService).create(todoCreationRequest);
    }

}
