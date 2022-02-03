package com.brtvsk.mediaservice;

import com.brtvsk.mediaservice.controller.AttachmentController;
import com.brtvsk.mediaservice.model.dto.AttachmentResponse;
import com.brtvsk.mediaservice.model.dto.ImmutableAttachmentResponse;
import com.brtvsk.mediaservice.service.AmazonClient;
import com.brtvsk.mediaservice.util.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URL;
import java.util.Optional;

import static com.brtvsk.mediaservice.TestUtils.TEST_DESCRIPTION;
import static com.brtvsk.mediaservice.TestUtils.TEST_FILENAME;
import static com.brtvsk.mediaservice.TestUtils.TEST_FILE_CONTENT;
import static com.brtvsk.mediaservice.TestUtils.TEST_MAX_SIZE;
import static com.brtvsk.mediaservice.TestUtils.TEST_OBJECT_ID;
import static com.brtvsk.mediaservice.TestUtils.TEST_SIZE_IN_KB;
import static com.brtvsk.mediaservice.TestUtils.TEST_URL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttachmentController.class)
@AutoConfigureMockMvc(addFilters = false)
@WebAppConfiguration
class AttachmentControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserMapper userMapper;

    @MockBean(name = "amazonClient")
    private AmazonClient amazonClient;

    private static final String BASE_PATH = "/api/v1/attachment";

    @WithMockUser(value = "user", roles = {"USER"})
    @Test
    void shouldGetMetadata() throws Exception {
        AttachmentResponse attachmentResponse = ImmutableAttachmentResponse.builder()
                .sizeInKb(TEST_SIZE_IN_KB)
                .description(Optional.of(TEST_DESCRIPTION))
                .filename(TEST_FILENAME)
                .build();

        when(amazonClient.getAttachmentMetadata(any(), any(), any()))
                .thenReturn(attachmentResponse);

        final String expectedResponse = objectMapper.writeValueAsString(attachmentResponse);

        mockMvc.perform(
                        MockMvcRequestBuilders.get(
                                        BASE_PATH
                                                + "/"
                                                + TEST_OBJECT_ID
                                                + "/metadata"
                                )
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(expectedResponse)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

        verify(amazonClient).getAttachmentMetadata(any(), any(), any());
    }

    @WithMockUser(value = "user", roles = {"USER"})
    @Test
    void shouldGenerateResourceUrl() throws Exception {
        URL expectedURL = new URL(TEST_URL);

        when(amazonClient.generateTemporaryLink(any(), any(), any()))
                .thenReturn(expectedURL);

        final String expectedResponse = objectMapper.writeValueAsString(expectedURL);

        mockMvc.perform(
                        MockMvcRequestBuilders.get(
                                        BASE_PATH
                                                + "/"
                                                + TEST_OBJECT_ID
                                                + "/url"
                                )
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(expectedResponse)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

        verify(amazonClient).generateTemporaryLink(any(), any(), any());
    }

    @WithMockUser(value = "user", roles = {"USER"})
    @Test
    void shouldDeleteFile() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(
                                BASE_PATH
                                        + "/"
                                        + TEST_OBJECT_ID
                        )
                )
                .andExpect(status().isOk());

        verify(amazonClient).deleteFileFromS3Bucket(any(), any(), any());
    }

    @WithMockUser(value = "user", roles = {"USER"})
    @Test
    void shouldUploadFile() throws Exception {
        AttachmentResponse attachmentResponse = ImmutableAttachmentResponse.builder()
                .sizeInKb(TEST_SIZE_IN_KB)
                .description(Optional.of(TEST_DESCRIPTION))
                .filename(TEST_FILENAME)
                .build();

        when(amazonClient.uploadFile(any(), any(), any(), any()))
                .thenReturn(attachmentResponse);

        when(amazonClient.maximumUserAttachmentSizeInBytes())
                .thenReturn(TEST_MAX_SIZE);

        final String expectedResponse = objectMapper.writeValueAsString(attachmentResponse);

        MockPart part = new MockPart("file",
                TEST_FILENAME,
                TEST_FILE_CONTENT.getBytes());

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart(BASE_PATH + "/" + TEST_OBJECT_ID)
                        .part(part)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

}
