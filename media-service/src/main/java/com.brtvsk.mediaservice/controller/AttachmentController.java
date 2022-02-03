package com.brtvsk.mediaservice.controller;

import com.brtvsk.mediaservice.model.dto.AttachmentResponse;
import com.brtvsk.mediaservice.security.model.AuthUser;
import com.brtvsk.mediaservice.util.UserMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.brtvsk.mediaservice.service.AmazonClient;

import java.net.URL;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attachment")
public class AttachmentController {
    private final AmazonClient amazonClient;
    private final UserMapper userMapper;

    public AttachmentController(final AmazonClient amazonClient, final UserMapper userMapper) {
        this.amazonClient = amazonClient;
        this.userMapper = userMapper;
    }

    @GetMapping("/{associatedObjectId}/url")
    public URL generateResourceUrl(@PathVariable final UUID associatedObjectId, final String filename, final Authentication auth) {
        AuthUser user = userMapper.fromAuthentication(auth);
        return this.amazonClient.generateTemporaryLink(filename, associatedObjectId, user);
    }

    @GetMapping("/{associatedObjectId}/metadata")
    public AttachmentResponse getMetadata(@PathVariable final UUID associatedObjectId, final String filename, final Authentication auth) {
        AuthUser user = userMapper.fromAuthentication(auth);
        return this.amazonClient.getAttachmentMetadata(filename, associatedObjectId, user);
    }

    @PostMapping("/{associatedObjectId}")
    @PreAuthorize("#file.size <= @amazonClient.maximumUserAttachmentSizeInBytes() or hasRole('PREMIUM')")
    public AttachmentResponse uploadFile(@PathVariable final UUID associatedObjectId, @RequestPart("file") MultipartFile file, final Optional<String> description, final Authentication auth) throws Exception {
        AuthUser user = userMapper.fromAuthentication(auth);
        return this.amazonClient.uploadFile(file, associatedObjectId, description, user);
    }

    @DeleteMapping("/{associatedObjectId}")
    public void deleteFile(@PathVariable final UUID associatedObjectId, final String filename, final Authentication auth) {
        AuthUser user = userMapper.fromAuthentication(auth);
        this.amazonClient.deleteFileFromS3Bucket(filename, associatedObjectId, user);
    }
}
