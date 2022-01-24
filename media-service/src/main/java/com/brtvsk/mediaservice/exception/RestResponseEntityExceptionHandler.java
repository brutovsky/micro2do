package com.brtvsk.mediaservice.exception;

import com.amazonaws.AmazonServiceException;
import com.brtvsk.mediaservice.util.RestMessage;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    // 413 MultipartException - file size too big
    @ExceptionHandler({MultipartException.class, FileSizeLimitExceededException.class, java.lang.IllegalStateException.class})
    public ResponseEntity<Object> handleSizeExceededException(final WebRequest request, final MultipartException ex) {
        return handleExceptionInternal(ex, new RestMessage(ex.getMessage()), new HttpHeaders(), HttpStatus.PAYLOAD_TOO_LARGE, request);
    }

    @ExceptionHandler(AmazonServiceException.class)
    public ResponseEntity<Object> handleAmazonS3Exception(final WebRequest request, final AmazonServiceException ex) {
        return handleExceptionInternal(ex, new RestMessage(ex.getMessage()), new HttpHeaders(), Objects.requireNonNull(HttpStatus.resolve(ex.getStatusCode())), request);
    }


}
