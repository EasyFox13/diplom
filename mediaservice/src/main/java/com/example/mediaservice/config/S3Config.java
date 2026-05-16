package com.example.mediaservice.config;


import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;

import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
@Configuration
public class S3Config {

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.endpoint}")
    private String endpoint;

    // Добавляем получение имени бакета из пропертис здесь тоже!
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Bean
    public S3Client s3Client() {
        S3Client client = S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .forcePathStyle(true) // Это исправит ошибку 301
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();

        try {
            // Проверяем, существует ли бакет, прежде чем создавать
            // Или просто вызываем создание (MinIO проигнорирует, если он есть)
            client.createBucket(b -> b.bucket(bucketName));
            System.out.println(">>> S3 INFO: Бакет [" + bucketName + "] готов к работе.");
        } catch (Exception e) {
            System.out.println(">>> S3 INFO: Бакет уже существует или доступен.");
        }

        return client;
    }
}