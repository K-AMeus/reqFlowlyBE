package com.spec2test.application.useCase.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatCompletion;
import com.openai.models.ChatCompletionCreateParams;
import com.openai.models.ChatModel;
import com.spec2test.application.useCase.dto.UseCaseDTO;
import com.spec2test.application.useCase.dto.UseCaseReq;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UseCaseService {

    private final OpenAIClient openaiClient;

    private static final String DEFAULT_DOMAIN_EXTRACTION_PROMPT = """
            From the functional requirements below, identify the domain entities (a.k.a. domain objects) for an information system.
            Include both explicitly mentioned entities\s
            Include implicitly required ones. For example, if "doctors write examination notes," you may infer the existence of an Examination entity, or for example from some strong verbs-actions such as Register and Withdraw, you may infer the existence of a Registration and a Withdrawal entities.
            Avoid treating implementation details (e.g., UI, buttons, delivery channels) as entities.
            Group related data or actions under meaningful domain concepts (e.g., Examination includes notes and findings).
            Clearly distinguish between entities, attributes, and associations.
            """;

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

    public UseCaseDTO processUseCaseText(UseCaseReq req) {
        String text = req.getDescription();
        String gptResponse = callOpenAiForDomainObjects(text, req.getCustomPrompt());
        return parseGptResponse(gptResponse);
    }

    public UseCaseDTO processUseCaseFile(MultipartFile file, String customPrompt) {
        try {
            String text = extractTextFromPDF(file.getInputStream());
            String gptResponse = callOpenAiForDomainObjects(text, customPrompt);
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

    private String callOpenAiForDomainObjects(String text, String customPrompt) {
        String prompt;

        if (customPrompt != null && !customPrompt.isBlank()) {
            prompt = DEFAULT_DOMAIN_EXTRACTION_PROMPT +
                    "\n\nAdditional instructions: " + customPrompt;
        } else {
            prompt = DEFAULT_DOMAIN_EXTRACTION_PROMPT;
        }

        prompt += JSON_FORMAT_INSTRUCTION + "Text:\n" + text;

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(prompt)
                .model(ChatModel.GPT_4_TURBO)
                .build();

        ChatCompletion chatCompletion = openaiClient.chat().completions().create(params);

        return chatCompletion.choices().getFirst().message().content().orElse("");
    }

    private UseCaseDTO parseGptResponse(String gptResponse) {
        String cleanedResponse = cleanGptResponse(gptResponse);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(cleanedResponse);

            Map<String, List<String>> domainObjects = objectMapper.convertValue(
                    root.get("domainObjects"),
                    new TypeReference<>() {
                    }
            );

            Map<String, List<String>> suggestedDomainObjects = objectMapper.convertValue(
                    root.get("suggestedDomainObjects"),
                    new TypeReference<>() {
                    }
            );
            UseCaseDTO dto = new UseCaseDTO();
            dto.setDomainObjects(domainObjects);
            dto.setSuggestedDomainObjects(suggestedDomainObjects);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse GPT response: " + cleanedResponse, e);
        }
    }

    private String cleanGptResponse(String response) {
        if (response == null) {
            return "";
        }

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