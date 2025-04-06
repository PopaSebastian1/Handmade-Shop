package com.example.handmadeshop.service;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.*;

import java.nio.ByteBuffer;
import java.util.Base64;

public class KmsEncryptionService {

    private final String keyId = "arn:aws:kms:us-east-1:816130369761:key/05c6999d-2582-4957-a9aa-98c42b0465bd"; // înlocuiește cu cheia ta!
    private final KmsClient kmsClient;

    public KmsEncryptionService() {
        this.kmsClient = KmsClient.builder()
                .region(Region.US_EAST_1) // adaptează dacă e altă regiune
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
