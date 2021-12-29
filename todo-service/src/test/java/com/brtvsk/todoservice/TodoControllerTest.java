package com.brtvsk.todoservice;

import com.brtvsk.todoservice.controller.TodoController;
import com.brtvsk.todoservice.i18n.Translator;
import com.brtvsk.todoservice.model.dto.ImmutableOptionalRequestTodoDto;
import com.brtvsk.todoservice.model.dto.ImmutableRequestTodoDto;
import com.brtvsk.todoservice.model.dto.ImmutableResponseTodoDto;
import com.brtvsk.todoservice.model.dto.OptionalRequestTodoDto;
import com.brtvsk.todoservice.model.dto.RequestTodoDto;
import com.brtvsk.todoservice.model.dto.ResponseTodoDto;
import com.brtvsk.todoservice.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
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

import static com.brtvsk.todoservice.TodoTestUtils.IS_DONE;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_COMPLETION_TIME;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_CREATION_TIME;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_DESCRIPTION;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_ID;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_TAGS;
import static com.brtvsk.todoservice.TodoTestUtils.TEST_TITLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private Translator translator;

    @MockBean
    private TodoService todoService;

    private static final String BASE_PATH = "/api/v1/todo/";

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

        when(todoService.create(any()))
                .thenReturn(expectedTodoResponse);

        final String jsonRequest = objectMapper.writeValueAsString(todoCreationRequest);
        final String expectedResponse = objectMapper.writeValueAsString(expectedTodoResponse);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(BASE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));

        verify(todoService).create(todoCreationRequest);
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

        when(todoService.replace(any(UUID.class), any(RequestTodoDto.class)))
                .thenReturn(expectedTodoResponse);

        final String jsonRequest = objectMapper.writeValueAsString(todoCreationRequest);
        final String expectedResponse = objectMapper.writeValueAsString(expectedTodoResponse);

        mockMvc.perform(
                        MockMvcRequestBuilders.put(BASE_PATH + TEST_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));

        verify(todoService).replace(TEST_ID, todoCreationRequest);
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

        when(todoService.update(any(UUID.class), any(OptionalRequestTodoDto.class)))
                .thenReturn(expectedTodoResponse);

        final String jsonRequest = objectMapper.writeValueAsString(optionalTodoCreationRequest);
        final String expectedResponse = objectMapper.writeValueAsString(expectedTodoResponse);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch(BASE_PATH + TEST_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));

        verify(todoService).update(TEST_ID, optionalTodoCreationRequest);
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

        when(todoService.findById(TEST_ID))
                .thenReturn(Optional.of(expectedTodoResponse));

        final String expectedResponse = objectMapper.writeValueAsString(expectedTodoResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + TEST_ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));

        verify(todoService).findById(TEST_ID);
    }

    @Test
    void shouldFindAll() throws Exception {
        List<ResponseTodoDto> expectedList = List.of(
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

        when(todoService.findAll()).thenReturn(expectedList);

        final String expectedResponse = objectMapper.writeValueAsString(expectedList);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));

        verify(todoService).findAll();
    }

    @Test
    void shouldDeleteTodo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + TEST_ID))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(todoService, times(1)).delete(TEST_ID);
    }

}
