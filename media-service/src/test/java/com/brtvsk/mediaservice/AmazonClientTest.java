package com.brtvsk.mediaservice;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.brtvsk.mediaservice.model.dto.AttachmentResponse;
import com.brtvsk.mediaservice.model.dto.ImmutableAttachmentResponse;
import com.brtvsk.mediaservice.service.AmazonClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static com.brtvsk.mediaservice.TestUtils.TEST_BUCKET_NAME;
import static com.brtvsk.mediaservice.TestUtils.TEST_DESCRIPTION;
import static com.brtvsk.mediaservice.TestUtils.TEST_DURATION;
import static com.brtvsk.mediaservice.TestUtils.TEST_FILENAME;
import static com.brtvsk.mediaservice.TestUtils.TEST_FILE_CONTENT;
import static com.brtvsk.mediaservice.TestUtils.TEST_OBJECT_ID;
import static com.brtvsk.mediaservice.TestUtils.TEST_SIZE_IN_KB;
import static com.brtvsk.mediaservice.TestUtils.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AmazonClientTest {
    private final AmazonS3 amazonS3 = mock(AmazonS3.class);
    private final AmazonClient amazonClient = new AmazonClient(amazonS3);

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(amazonClient, "bucketName", TEST_BUCKET_NAME);
        ReflectionTestUtils.setField(amazonClient, "expirationDuration", TEST_DURATION);
    }

    @Test
    void shouldGenerateTemporaryLink() {
        amazonClient.generateTemporaryLink(TEST_FILENAME, TEST_OBJECT_ID, USER);
        verify(amazonS3).generatePresignedUrl(
                eq(TEST_BUCKET_NAME),
                any(String.class),
                any(Date.class)
        );
    }

    @Test
    void shouldUploadFile() throws Exception {
        AttachmentResponse expectedResult = ImmutableAttachmentResponse.builder()
                .sizeInKb(TEST_SIZE_IN_KB)
                .description(Optional.of(TEST_DESCRIPTION))
                .filename(TEST_FILENAME)
                .build();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setUserMetadata(Map.of("description", TEST_DESCRIPTION));
        objectMetadata.setContentLength(TEST_SIZE_IN_KB);

        PutObjectResult putObjectResult = new PutObjectResult();
        putObjectResult.setMetadata(objectMetadata);

        when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(putObjectResult);

        when(amazonS3.getObjectMetadata(eq(TEST_BUCKET_NAME), any(String.class)))
                .thenReturn(objectMetadata);

        MockMultipartFile file
                = new MockMultipartFile(
                TEST_FILENAME,
                TEST_FILENAME,
                MediaType.TEXT_PLAIN_VALUE,
                TEST_FILE_CONTENT.getBytes()
        );

        AttachmentResponse response = amazonClient.uploadFile(file, TEST_OBJECT_ID, Optional.of(TEST_DESCRIPTION), USER);

        assertThat(response.getDescription()).isEqualTo(expectedResult.getDescription());
        assertThat(response.getFilename()).isEqualTo(expectedResult.getFilename());
        assertThat(response.getSizeInKb()).isEqualTo(expectedResult.getSizeInKb());
    }

    @Test
    void shouldGetAttachmentMetadata() {
        AttachmentResponse expectedResult = ImmutableAttachmentResponse.builder()
                .sizeInKb(TEST_SIZE_IN_KB)
                .description(Optional.of(TEST_DESCRIPTION))
                .filename(TEST_FILENAME)
                .build();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setUserMetadata(Map.of("description", TEST_DESCRIPTION));
        objectMetadata.setContentLength(TEST_SIZE_IN_KB);

        when(amazonS3.getObjectMetadata(eq(TEST_BUCKET_NAME), any(String.class)))
                .thenReturn(objectMetadata);

        AttachmentResponse response = amazonClient.getAttachmentMetadata(TEST_FILENAME, TEST_OBJECT_ID, USER);

        assertThat(response.getDescription()).isEqualTo(expectedResult.getDescription());
        assertThat(response.getFilename()).isEqualTo(expectedResult.getFilename());
        assertThat(response.getSizeInKb()).isEqualTo(expectedResult.getSizeInKb());
    }

    @Test
    void shouldDeleteFileFromS3Bucket() {
        amazonClient.deleteFileFromS3Bucket(TEST_FILENAME, TEST_OBJECT_ID, USER);
        verify(amazonS3).deleteObject(eq(TEST_BUCKET_NAME), any(String.class));
    }

}
