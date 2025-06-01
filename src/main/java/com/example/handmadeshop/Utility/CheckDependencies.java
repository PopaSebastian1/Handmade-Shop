package com.example.handmadeshop.Utility;

public class CheckDependencies {
    public static void main(String[] args) {
        System.out.println("🔍 Checking CloudWatch Log4j2 Dependencies...");

        // Verifică dacă clasele CloudWatch sunt disponibile
        try {
            Class.forName("com.kdgregory.logging.log4j2.CloudWatchAppender");
            System.out.println("✅ CloudWatchAppender class found");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ CloudWatchAppender class NOT found");
        }

        try {
            Class.forName("com.kdgregory.logging.log4j2.CloudWatchLogsAppender");
            System.out.println("✅ CloudWatchLogsAppender class found");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ CloudWatchLogsAppender class NOT found");
        }

        try {
            Class.forName("com.kdgregory.logging.logwriters.CloudWatchLogsLogWriter");
            System.out.println("✅ CloudWatchLogsLogWriter class found");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ CloudWatchLogsLogWriter class NOT found");
        }

        // Verifică AWS SDK
        try {
            Class.forName("software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient");
            System.out.println("✅ AWS CloudWatchLogsClient class found");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ AWS CloudWatchLogsClient class NOT found");
        }

        // Listează toate clasele kdgregory disponibile
        System.out.println("\n📦 Checking classpath for kdgregory packages...");
        String classpath = System.getProperty("java.class.path");
        String[] paths = classpath.split(System.getProperty("path.separator"));

        for (String path : paths) {
            if (path.contains("kdgregory") || path.contains("log4j2-aws")) {
                System.out.println("📍 Found: " + path);
            }
        }

        System.out.println("\n✅ Dependency check completed!");
    }
}