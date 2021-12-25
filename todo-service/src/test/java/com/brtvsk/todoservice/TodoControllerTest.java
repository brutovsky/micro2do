package com.brtvsk.todoservice;

import com.brtvsk.todoservice.controller.TodoController;
import com.brtvsk.todoservice.model.dto.*;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.brtvsk.todoservice.TodoTestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    @Test
    void shouldCreateTodo() throws Exception {
        ResponseTodoDto expectedTodoResponse = ImmutableResponseTodoDto
                .builder()
                .id(TEST_ID)
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .done(IS_DONE)
                .tags(TEST_TAGS)
                .creationTime(TEST_CREATION_TIME)
                .build();

        RequestTodoDto todoCreationRequest = ImmutableRequestTodoDto.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .tags(TEST_TAGS)
                .build();

        Mockito.when(todoService.create(any()))
                .thenReturn(expectedTodoResponse);

        final String jsonRequest = objectMapper.writeValueAsString(todoCreationRequest);
        final String expectedResponse = objectMapper.writeValueAsString(expectedTodoResponse);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/todo")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));

        Mockito.verify(todoService).create(todoCreationRequest);
    }

    @Test
    void shouldReplaceTodo() throws Exception {
        ResponseTodoDto expectedTodoResponse = ImmutableResponseTodoDto
                .builder()
                .id(TEST_ID)
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .done(IS_DONE)
                .tags(TEST_TAGS)
                .creationTime(TEST_CREATION_TIME)
                .build();

        RequestTodoDto todoCreationRequest = ImmutableRequestTodoDto.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .done(IS_DONE)
                .tags(TEST_TAGS)
                .build();

        Mockito.when(todoService.replace(any(UUID.class), any(RequestTodoDto.class)))
                .thenReturn(expectedTodoResponse);

        final String jsonRequest = objectMapper.writeValueAsString(todoCreationRequest);
        final String expectedResponse = objectMapper.writeValueAsString(expectedTodoResponse);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/todo/" + TEST_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));

        Mockito.verify(todoService).replace(TEST_ID, todoCreationRequest);
    }

    @Test
    void shouldUpdateTodo() throws Exception {
        ResponseTodoDto expectedTodoResponse = ImmutableResponseTodoDto
                .builder()
                .id(TEST_ID)
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .done(IS_DONE)
                .tags(TEST_TAGS)
                .creationTime(TEST_CREATION_TIME)
                .completionTime(TEST_COMPLETION_TIME)
                .build();

        OptionalRequestTodoDto optionalTodoCreationRequest = ImmutableOptionalRequestTodoDto.builder()
                .description(TEST_DESCRIPTION)
                .done(IS_DONE)
                .completionTime(TEST_COMPLETION_TIME)
                .build();

        Mockito.when(todoService.update(any(UUID.class), any(OptionalRequestTodoDto.class)))
                .thenReturn(expectedTodoResponse);

        final String jsonRequest = objectMapper.writeValueAsString(optionalTodoCreationRequest);
        final String expectedResponse = objectMapper.writeValueAsString(expectedTodoResponse);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/todo/" + TEST_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));

        Mockito.verify(todoService).update(TEST_ID, optionalTodoCreationRequest);
    }

    @Test
    void shouldFindTodo() throws Exception {
        ResponseTodoDto expectedTodoResponse = ImmutableResponseTodoDto
                .builder()
                .id(TEST_ID)
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .done(IS_DONE)
                .tags(TEST_TAGS)
                .creationTime(TEST_CREATION_TIME)
                .build();

        Mockito.when(todoService.findById(TEST_ID))
                .thenReturn(Optional.of(expectedTodoResponse));

        final String expectedResponse = objectMapper.writeValueAsString(expectedTodoResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/todo/" + TEST_ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    void shouldFindAll() throws Exception {
        List<? extends ResponseTodoDto> expectedList = List.of(
                ImmutableResponseTodoDto.builder()
                        .id(TEST_ID)
                        .title(TEST_TITLE)
                        .done(IS_DONE)
                        .creationTime(TEST_CREATION_TIME)
                        .build(),
                ImmutableResponseTodoDto.builder()
                        .id(TEST_ID)
                        .title(TEST_TITLE)
                        .done(IS_DONE)
                        .creationTime(TEST_CREATION_TIME)
                        .build()
        );

        Mockito.<List<? extends ResponseTodoDto>>when(todoService.findAll()).thenReturn(expectedList);

        final String expectedResponse = objectMapper.writeValueAsString(expectedList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/todo"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    void shouldDeleteTodo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/todo/" + TEST_ID))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(todoService, times(1)).delete(TEST_ID);
    }

}
