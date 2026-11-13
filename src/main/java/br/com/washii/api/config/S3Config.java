package br.com.washii.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${supabase.project-ref}")
    private String projectRef;

    @Value("${supabase.public-key}")
    private String publicKey;

    @Value("${supabase.secret-key}")
    private String secretKey;

    @Value("${supabase.storage.s3-endpoint}")
    private String s3Endpoint;

    @Value("${supabase.storage.region}")
    private String region;

    @Value("${supabase.storage.access-key:}")
    private String s3AccessKey;

    @Value("${supabase.storage.secret-key:}")
    private String s3SecretKey;

    @Bean
    public S3Client s3Client() {
        AwsCredentials credentials;

        if (s3AccessKey != null && !s3AccessKey.isBlank() && s3SecretKey != null && !s3SecretKey.isBlank()) {
            credentials = AwsBasicCredentials.create(s3AccessKey, s3SecretKey);
        } else {
            credentials = AwsSessionCredentials.create(projectRef, publicKey, secretKey);
        }

        return S3Client.builder()
                .endpointOverride(URI.create(s3Endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .forcePathStyle(true)
                .build();
    }
}
