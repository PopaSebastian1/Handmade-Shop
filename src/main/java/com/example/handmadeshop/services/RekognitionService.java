package com.example.handmadeshop.services;

import jakarta.ejb.Stateless;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Stateless
public class RekognitionService {

    private final RekognitionClient client = RekognitionClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

    public byte[] downloadImage(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        try (InputStream in = url.openStream()) {
            return in.readAllBytes();
        }
    }

    public List<ModerationLabel> detectModerationLabels(byte[] imageBytes, float minConfidence) {
        Image awsImage = Image.builder()
                .bytes(SdkBytes.fromByteArray(imageBytes))
                .build();

        DetectModerationLabelsRequest req = DetectModerationLabelsRequest.builder()
                .image(awsImage)
                .minConfidence(minConfidence)
                .build();

        DetectModerationLabelsResponse resp = client.detectModerationLabels(req);
        return resp.moderationLabels();
    }
    public Path downloadImageToTempFile(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        Path tempFile = Files.createTempFile("rek-", ".img");
        try (InputStream in = url.openStream()) {
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFile;
    }
}