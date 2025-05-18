package com.reqflowly.application.domainObject.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.cloud.vertexai.generativeai.ResponseStream;
import com.reqflowly.application.domainObject.dto.DomainObjectGenerationDto;
import com.reqflowly.application.domainObject.dto.DomainObjectGenerationReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DomainObjectGenerationService {

    private final GenerativeModel geminiTextModel;
    private final GenerationConfig defaultGenConfig;

    @Value("${DOMAIN_EXTRACTION_PROMPT}")
    private String DOMAIN_EXTRACTION_PROMPT;

    private static final String ADDITIONAL_HEADER = "Additional custom instructions:";
    private static final String ADDITIONAL_FOOTER = "End of custom instructions";

    private static final String JSON_FORMAT_INSTRUCTION = """
            
            Return valid JSON only (do not include any markdown formatting) in the following format:
            
            {
              "domainObjects": {
                "domain object 1": ["attribute1", "attribute2", "attribute3"],
                "domain object 2": ["attribute1", "attribute2"],
                "domain object n": ["attribute1", "attribute2", "attribute3", "attribute4"]
              },
              "suggestedDomainObjects": {
                "domain object 1": ["attribute1", "attribute2", "attribute3"],
                "domain object 2": ["attribute1", "attribute2"],
                "domain object n": ["attribute1", "attribute2", "attribute3", "attribute4"]
              }
            }
            
            """;

    public DomainObjectGenerationDto processUseCaseText(DomainObjectGenerationReq req) {
        String gptResponse = callAiForDomainObjects(req.getDescription(), req.getCustomPrompt());
        log.info(gptResponse);
        return parseGptResponse(gptResponse);
    }

    public DomainObjectGenerationDto processUseCaseFile(MultipartFile file, String customPrompt) {
        try {
            String text = extractTextFromPDF(file.getInputStream());
            String gptResponse = callAiForDomainObjects(text, customPrompt);
            return parseGptResponse(gptResponse);
        } catch (Exception e) {
            throw new RuntimeException("Error processing PDF file", e);
        }
    }

    private String extractTextFromPDF(InputStream inputStream) throws Exception {
        byte[] pdfBytes = inputStream.readAllBytes();
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String callAiForDomainObjects(String text, String customPrompt) {
        String prompt = buildPrompt(text, customPrompt);
        log.info("Domain extraction prompt:\n{}", prompt);

        StringBuilder sb = new StringBuilder();
        try {
            ResponseStream<GenerateContentResponse> stream =
                    geminiTextModel
                            .withGenerationConfig(defaultGenConfig)
                            .generateContentStream(prompt);

            for (GenerateContentResponse chunk : stream) {
                sb.append(ResponseHandler.getText(chunk));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error streaming domain objects from AI", e);
        }

        return sb.toString();
    }

    private String buildPrompt(String text, @Nullable String customPrompt) {
        StringBuilder sb = new StringBuilder(DOMAIN_EXTRACTION_PROMPT).append('\n');

        Optional.ofNullable(customPrompt)
                .filter(s -> !s.isBlank())
                .ifPresent(cp -> sb.append('\n')
                        .append(ADDITIONAL_HEADER).append('\n')
                        .append(cp.trim()).append('\n')
                        .append(ADDITIONAL_FOOTER).append("\n\n"));

        sb.append(JSON_FORMAT_INSTRUCTION)
                .append("Text:\n")
                .append(text);

        return sb.toString();
    }

    private DomainObjectGenerationDto parseGptResponse(String gptResponse) {
        String cleaned = cleanGptResponse(gptResponse);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(cleaned);

            Map<String, List<String>> objects    = mapper.convertValue(root.get("domainObjects"),
                    new TypeReference<>() {});
            Map<String, List<String>> suggestions = mapper.convertValue(root.get("suggestedDomainObjects"),
                    new TypeReference<>() {});


            DomainObjectGenerationDto dto = new DomainObjectGenerationDto();
            dto.setDomainObjects(objects);
            dto.setSuggestedDomainObjects(suggestions);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse GPT response: " + cleaned, e);
        }
    }

    private String cleanGptResponse(String response) {
        if (response == null) return "";

        String cleaned = response.trim();

        if (cleaned.startsWith("```") && cleaned.endsWith("```")) {
            cleaned = cleaned.substring(3, cleaned.length() - 3).trim();
            int firstNewline = cleaned.indexOf('\n');
            if (firstNewline != -1) {
                String firstLine = cleaned.substring(0, firstNewline).trim();
                if (!firstLine.startsWith("{") && !firstLine.startsWith("[")) {
                    cleaned = cleaned.substring(firstNewline).trim();
                }
            }
        }

        return cleaned;
    }
}