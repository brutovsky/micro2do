package com.brtvsk.mediaservice.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.brtvsk.mediaservice.model.dto.AttachmentResponse;
import com.brtvsk.mediaservice.model.dto.ImmutableAttachmentResponse;
import com.brtvsk.mediaservice.security.model.AuthUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class AmazonClient {
    private AmazonS3 s3client;
    @Value("${s3.bucketName}")
    private String bucketName;
    @Value("${s3.accessKeyId}")
    private String accessKeyId;
    @Value("${s3.secretKey}")
    private String secretKey;
    @Value("${s3.region}")
    private String region;
    @Value("${s3.presignedUrl.expirationDuration}")
    private Duration expirationDuration;
    @Value("${s3.user.max-request-size}")
    private DataSize maximumUserAttachmentSize;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials
                = new BasicAWSCredentials(this.accessKeyId, this.secretKey);
        this.s3client = AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public URL generateTemporaryLink(final String filename, final UUID associatedObjectId, final AuthUser user) {
        String resourceKey = generateResourceKey(filename, associatedObjectId, user);
        Date expirationTime = Date.from(Instant.now().plus(expirationDuration.toMinutes(), ChronoUnit.MINUTES));
        return s3client.generatePresignedUrl(bucketName, resourceKey, expirationTime);
    }

    public AttachmentResponse uploadFile(final MultipartFile multipartFile, final UUID associatedObjectId, final Optional<String> description, final AuthUser user)
            throws Exception {
        File file = convertMultiPartToFile(multipartFile);
        String resourceKey = generateResourceKey(multipartFile.getOriginalFilename(), associatedObjectId, user);
        uploadFileTos3bucket(resourceKey, description, file);
        file.delete();
        ObjectMetadata metadata = s3client.getObjectMetadata(bucketName, resourceKey);
        return generateAttachmentResponse(metadata, multipartFile.getOriginalFilename());
    }

    public AttachmentResponse getAttachmentMetadata(final String filename, final UUID associatedObjectId, final AuthUser user) {
        String resourceKey = generateResourceKey(filename, associatedObjectId, user);
        ObjectMetadata metadata = s3client.getObjectMetadata(bucketName, resourceKey);
        return generateAttachmentResponse(metadata, filename);
    }

    public void deleteFileFromS3Bucket(final String filename, final UUID associatedObjectId, final AuthUser user) {
        String resourceKey = generateResourceKey(filename, associatedObjectId, user);
        s3client.deleteObject(bucketName, resourceKey);
    }

    private PutObjectResult uploadFileTos3bucket(final String fileName, final Optional<String> description, final File file) {
        PutObjectRequest request = new PutObjectRequest(bucketName, fileName, file);
        ObjectMetadata metadata = new ObjectMetadata();
        description.ifPresent(s -> metadata.addUserMetadata("description", s));
        request.setMetadata(metadata);
        return s3client.putObject(request);
    }

    private File convertMultiPartToFile(MultipartFile file)
            throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private String generateResourceKey(final String userDefinedFilename, final UUID associatedObjectId, final AuthUser user) {
        var list = List.of(
                user.getId().toString(),
                associatedObjectId.toString(),
                userDefinedFilename
        );
        var fileName = String.join("/", list);
        return fileName.replace(" ", "_");
    }

    private AttachmentResponse generateAttachmentResponse(final ObjectMetadata objectMetadata, final String filename) {
        long sizeInKb = objectMetadata.getContentLength();
        String description = objectMetadata.getUserMetaDataOf("description");
        return ImmutableAttachmentResponse.builder()
                .sizeInKb(sizeInKb)
                .description(Optional.ofNullable(description))
                .filename(filename)
                .build();
    }

    public long maximumUserAttachmentSizeInBytes() {
        return maximumUserAttachmentSize.toBytes();
    }
}
