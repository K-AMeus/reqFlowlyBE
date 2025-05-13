package com.reqflowly.application.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Configuration
public class VertexAIConfig {

    @Value("${VERTEX_SERVICE_ACCOUNT}")
    private String vertexServiceAccountJson;

    @Bean(destroyMethod = "close")
    public VertexAI vertexAI() throws Exception {
        log.info("Inside VertexAi bean");
        try (InputStream stream =
                     new ByteArrayInputStream(vertexServiceAccountJson.getBytes(StandardCharsets.UTF_8))) {
            log.info("input stream donezo");

            GoogleCredentials creds = GoogleCredentials.fromStream(stream)
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
            log.info("creds donezo");

            String projectId = "flowspec-9ce0c";
            String location = "us-central1";
            return new VertexAI.Builder()
                    .setProjectId(projectId)
                    .setLocation(location)
                    .setCredentials(creds)
                    .build();
        }
    }


    // testing models:
    // gemini-2.0-flash-001
    // gemini-2.5-pro-preview-05-06
    // gemini-2.5-flash-preview-04-17
    @Bean
    public GenerativeModel geminiTextModel(VertexAI vertexAI) {
        return new GenerativeModel("gemini-2.5-flash-preview-04-17", vertexAI);
    }

    @Bean
    public GenerationConfig defaultGenConfig() {
        return GenerationConfig.newBuilder()
                .setTemperature(0.8F)
                .setTopP(0.95F)
                .setMaxOutputTokens(8_192)
                .build();
    }
}