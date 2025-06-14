package com.example.handmadeshop.Utility;

import com.example.handmadeshop.services.SecretsManagerService;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class CloudWatchLogHandler extends Handler {

    private static final String LOG_GROUP = SecretsManagerService.get("logGroup");
    private static final String LOG_STREAM = SecretsManagerService.get("logStream");

    private final CloudWatchLogsClient logsClient;
    private final AtomicReference<String> sequenceToken = new AtomicReference<>();

    public CloudWatchLogHandler() {
        this.logsClient = CloudWatchLogsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try {
            createLogGroupAndStreamIfNotExists();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createLogGroupAndStreamIfNotExists() {
        try {
            logsClient.createLogGroup(CreateLogGroupRequest.builder().logGroupName(LOG_GROUP).build());
        } catch (ResourceAlreadyExistsException ignored) {
        }

        try {
            logsClient.createLogStream(CreateLogStreamRequest.builder()
                    .logGroupName(LOG_GROUP)
                    .logStreamName(LOG_STREAM)
                    .build());
        } catch (ResourceAlreadyExistsException ignored) {

        }

        DescribeLogStreamsResponse describe = logsClient.describeLogStreams(r -> r
                .logGroupName(LOG_GROUP)
                .logStreamNamePrefix(LOG_STREAM));
        if (!describe.logStreams().isEmpty()) {
            String token = describe.logStreams().get(0).uploadSequenceToken();
            sequenceToken.set(token);
        }
    }

    @Override
    public void publish(LogRecord record) {
        try {
            String message = getFormatter().format(record);

            InputLogEvent event = InputLogEvent.builder()
                    .message(message)
                    .timestamp(Instant.now().toEpochMilli())
                    .build();

            PutLogEventsRequest.Builder requestBuilder = PutLogEventsRequest.builder()
                    .logGroupName(LOG_GROUP)
                    .logStreamName(LOG_STREAM)
                    .logEvents(List.of(event));

            String token = sequenceToken.get();
            if (token != null) {
                requestBuilder.sequenceToken(token);
            }

            PutLogEventsResponse response = logsClient.putLogEvents(requestBuilder.build());
            sequenceToken.set(response.nextSequenceToken());

        } catch (Exception e) {
            System.err.println("Failed to send log to CloudWatch: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {
        logsClient.close();
    }
}
