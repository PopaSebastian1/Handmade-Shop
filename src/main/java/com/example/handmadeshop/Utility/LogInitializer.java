package com.example.handmadeshop.Utility;


import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.logging.LogManager;
import java.util.logging.Logger;

@Singleton
@Startup
public class LogInitializer {
    @PostConstruct
    public void init() {
        CloudWatchLogHandler handler = new CloudWatchLogHandler();
        handler.setFormatter(new java.util.logging.SimpleFormatter());
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.addHandler(handler);
    }
}