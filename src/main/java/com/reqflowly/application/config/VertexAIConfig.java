package com.reqflowly.application.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class VertexAIConfig {

    @Value("${google.cloud.project-id}")
    private String projectId;

    @Value("${google.cloud.location}")
    private String location;

    @Value("${VERTEX_SERVICE_ACCOUNT}")
    private String vertexServiceAccountJson;



    @Bean(destroyMethod = "close")
    public VertexAI vertexAI() throws Exception {
        try (InputStream stream =
                     new ByteArrayInputStream(vertexServiceAccountJson.getBytes(StandardCharsets.UTF_8))) {

            GoogleCredentials creds = GoogleCredentials.fromStream(stream)
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

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
