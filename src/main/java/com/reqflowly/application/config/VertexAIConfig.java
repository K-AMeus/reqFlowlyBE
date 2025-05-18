package com.reqflowly.application.config;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.Transport;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.PredictionServiceClient;
import com.google.cloud.vertexai.api.PredictionServiceSettings;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.threeten.bp.Duration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Configuration
public class VertexAIConfig {


    @Bean(destroyMethod = "close")
    public VertexAI vertexAI(
            @Value("${VERTEXAI_SERVICE_ACCOUNT}") String serviceAccountJson,
            @Value("${VERTEXAI_PROJECT_ID}") String projectId,
            @Value("${VERTEXAI_LOCATION}") String location
    ) throws IOException {
        String raw = serviceAccountJson.replace("\\n", "\n");
        GoogleCredentials creds;
        try (InputStream stream = new ByteArrayInputStream(raw.getBytes(StandardCharsets.UTF_8))) {
            creds = GoogleCredentials.fromStream(stream)
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        }

        PredictionServiceSettings.Builder settingsBuilder =
                PredictionServiceSettings.newHttpJsonBuilder();

        settingsBuilder.generateContentSettings()
                .setRetrySettings(
                        settingsBuilder.generateContentSettings()
                                .getRetrySettings()
                                .toBuilder()
                                .setTotalTimeout(Duration.ofMinutes(3))
                                .build()
                );

        settingsBuilder.streamGenerateContentSettings()
                .setRetrySettings(
                        settingsBuilder.streamGenerateContentSettings()
                                .getRetrySettings()
                                .toBuilder()
                                .setTotalTimeout(Duration.ofMinutes(3))
                                .build()
                );

        PredictionServiceSettings psSettings = settingsBuilder
                .setCredentialsProvider(FixedCredentialsProvider.create(creds))
                .build();
        PredictionServiceClient psClient = PredictionServiceClient.create(psSettings);

        return new VertexAI.Builder()
                .setProjectId(projectId)
                .setLocation(location)
                .setTransport(Transport.REST)
                .setPredictionClientSupplier(() -> psClient)
                .setCredentials(creds)
                .build();
    }

    // testing models:
    // gemini-2.0-flash-001
    // gemini-2.5-pro-preview-05-06
    // gemini-2.5-flash-preview-04-17
    @Bean
    public GenerativeModel geminiTextModel(VertexAI vertexAI, GenerationConfig defaultGenConfig) {
        return new GenerativeModel("gemini-2.5-flash-preview-04-17", vertexAI)
                .withGenerationConfig(defaultGenConfig);
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