package com.bankingsystem.bankingsystem.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class EnvConfig {

    private static final AtomicBoolean LOADED = new AtomicBoolean(false);
    
    static {
        loadEnv();
    }
    
    @PostConstruct
    public void initEnv() {
        loadEnv();
    }
    
    private static void loadEnv() {
        if (!LOADED.compareAndSet(false, true)) {
            return;
        }

        Path envPath = Path.of(".env");
        if (!Files.exists(envPath)) {
            return;
        }

        try {
            for (String line : Files.readAllLines(envPath)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                int separatorIndex = trimmed.indexOf('=');
                if (separatorIndex <= 0) {
                    continue;
                }

                String key = trimmed.substring(0, separatorIndex).trim();
                String value = trimmed.substring(separatorIndex + 1).trim();
                if ((value.startsWith("\"") && value.endsWith("\""))
                        || (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }

                System.setProperty(key, value);
                System.out.println("Loaded env key: " + key);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load .env file: " + e.getMessage());
        }
    }
}
