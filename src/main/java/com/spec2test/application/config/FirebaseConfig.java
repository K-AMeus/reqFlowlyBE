package com.spec2test.application.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Lazy
    @Bean
    public FirebaseApp firebaseApp(@Value("${FIREBASE_SERVICE_ACCOUNT}") String firebaseServiceAccountJson) throws Exception {
        try (InputStream stream = new ByteArrayInputStream(firebaseServiceAccountJson.getBytes(StandardCharsets.UTF_8))) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(stream))
                    .build();
            return FirebaseApp.initializeApp(options);
        }
    }

    @Lazy
    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}
