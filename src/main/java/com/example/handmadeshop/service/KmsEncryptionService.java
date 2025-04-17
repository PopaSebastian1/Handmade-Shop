package com.example.handmadeshop.service;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.*;

import java.util.Base64;

public class KmsEncryptionService {

    private final String keyId = "arn:aws:kms:us-east-1:067784255460:key/90ae80ba-4646-4893-a13a-f4f95a80643f";
    private final KmsClient kmsClient;

    public KmsEncryptionService() {
        this.kmsClient = KmsClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    public String encrypt(String plaintext) {
        EncryptRequest request = EncryptRequest.builder()
                .keyId(keyId)
                .plaintext(SdkBytes.fromUtf8String(plaintext))
                .build();

        EncryptResponse response = kmsClient.encrypt(request);
        return Base64.getEncoder().encodeToString(response.ciphertextBlob().asByteArray());
    }

    public String decrypt(String ciphertext) {
        byte[] decodedBytes = Base64.getDecoder().decode(ciphertext);

        DecryptRequest request = DecryptRequest.builder()
                .ciphertextBlob(SdkBytes.fromByteArray(decodedBytes))
                .build();

        DecryptResponse response = kmsClient.decrypt(request);
        return response.plaintext().asUtf8String();
    }
}
