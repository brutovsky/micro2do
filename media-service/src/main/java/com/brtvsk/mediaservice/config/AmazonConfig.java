package com.brtvsk.mediaservice.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {
    @Value("${s3.accessKeyId}")
    private String accessKeyId;
    @Value("${s3.secretKey}")
    private String secretKey;
    @Value("${s3.region}")
    private String region;

    @Bean
    public AmazonS3 s3client() {
        AWSCredentials credentials
                = new BasicAWSCredentials(this.accessKeyId, this.secretKey);
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(this.region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}
