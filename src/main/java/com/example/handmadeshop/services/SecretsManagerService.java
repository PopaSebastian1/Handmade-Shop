package com.example.handmadeshop.services;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class SecretsManagerService {

    private static final Logger logger = LogManager.getLogger(SecretsManagerService.class);
    private static Map<String, String> secrets;

    static {
        try {
            logger.info("Initializing AWS SecretsManagerClient...");
            SecretsManagerClient client = SecretsManagerClient.builder()
                    .region(Region.US_EAST_1)
                    .build();

            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId("Handmade")
                    .build();

            GetSecretValueResponse response = client.getSecretValue(request);
            String jsonString = response.secretString();

            ObjectMapper mapper = new ObjectMapper();
            secrets = mapper.readValue(jsonString, Map.class);

            logger.info("✅ Secrets loaded successfully. Keys: {}", secrets.keySet());
        } catch (Exception e) {
            logger.error("❌ Failed to load secrets from AWS Secrets Manager", e);
            throw new RuntimeException("Could not load secrets from AWS Secrets Manager", e);
        }
    }

    public static String get(String key) {
        return secrets.get(key);
    }
}
