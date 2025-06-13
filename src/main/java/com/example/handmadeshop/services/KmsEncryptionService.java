package com.example.handmadeshop.services;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Base64;
import java.util.logging.Logger;
import java.util.logging.Level;

@ApplicationScoped
public class KmsEncryptionService {

    private static final Logger LOGGER = Logger.getLogger(KmsEncryptionService.class.getName());

    private final String keyId = "arn:aws:kms:us-east-1:816130369761:alias/handmade-key";
    private final KmsClient kmsClient;

    public KmsEncryptionService() {
        try {
            this.kmsClient = KmsClient.builder()
                    .region(Region.US_EAST_1)
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
            LOGGER.info("KMS Client initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize KMS Client", e);
            throw new RuntimeException("Failed to initialize KMS service", e);
        }
    }

    public String encrypt(String plaintext) {
        if (plaintext == null || plaintext.trim().isEmpty()) {
            throw new IllegalArgumentException("Plaintext cannot be null or empty");
        }

        try {
            LOGGER.log(Level.INFO, "Encrypting data with KMS key: {0}", keyId);

            EncryptRequest request = EncryptRequest.builder()
                    .keyId(keyId)
                    .plaintext(SdkBytes.fromUtf8String(plaintext))
                    .build();

            EncryptResponse response = kmsClient.encrypt(request);
            String encryptedData = Base64.getEncoder().encodeToString(response.ciphertextBlob().asByteArray());

            LOGGER.info("Data encrypted successfully");
            return encryptedData;

        } catch (KmsException e) {
            LOGGER.log(Level.SEVERE, "KMS encryption failed: {0}", e.getMessage());
            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during encryption", e);
            throw new RuntimeException("Encryption failed due to unexpected error", e);
        }
    }

    public String decrypt(String ciphertext) {
        if (ciphertext == null || ciphertext.trim().isEmpty()) {
            throw new IllegalArgumentException("Ciphertext cannot be null or empty");
        }

        try {
            LOGGER.info("Decrypting data with KMS");

            byte[] decodedBytes = Base64.getDecoder().decode(ciphertext);

            DecryptRequest request = DecryptRequest.builder()
                    .ciphertextBlob(SdkBytes.fromByteArray(decodedBytes))
                    .build();

            DecryptResponse response = kmsClient.decrypt(request);
            String decryptedData = response.plaintext().asUtf8String();

            LOGGER.info("Data decrypted successfully");
            return decryptedData;

        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Invalid Base64 ciphertext: {0}", e.getMessage());
            throw new RuntimeException("Invalid ciphertext format", e);
        } catch (KmsException e) {
            LOGGER.log(Level.SEVERE, "KMS decryption failed: {0}", e.getMessage());
            throw new RuntimeException("Decryption failed: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during decryption", e);
            throw new RuntimeException("Decryption failed due to unexpected error", e);
        }
    }
}