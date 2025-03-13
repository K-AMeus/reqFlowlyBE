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
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UseCaseService {

    private final OpenAIClient openaiClient;

    public UseCaseDTO processUseCaseText(UseCaseReq req) {
        String text = req.getDescription();
        String gptResponse = callOpenAiForDomainObjects(text);
        return parseGptResponse(gptResponse);
    }

    public UseCaseDTO processUseCaseFile(MultipartFile file) {
        try {
            String text = extractTextFromPDF(file.getInputStream());
            String gptResponse = callOpenAiForDomainObjects(text);
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



    private String callOpenAiForDomainObjects(String text) {
        String prompt = "Extract the key domain objects (nouns) and important actions (verbs) " +
                "from the following text. Return the results as valid JSON in the format:\n\n" +
                "{\n" +
                "  \"domainObjects\": [\"object1\", \"object2\"],\n" +
                "  \"actions\": [\"action1\", \"action2\"]\n" +
                "}\n\n" +
                "Text:\n" + text;

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(prompt)
                // use whichever model you want
                .model(ChatModel.GPT_3_5_TURBO)
                .build();

        ChatCompletion chatCompletion = openaiClient.chat().completions().create(params);
        return chatCompletion.choices().getFirst().message().content().orElse("");
    }


    private UseCaseDTO parseGptResponse(String gptResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Attempt to read JSON from the GPT response
            JsonNode root = objectMapper.readTree(gptResponse);

            List<String> domainObjects = objectMapper.convertValue(
                    root.get("domainObjects"), new TypeReference<List<String>>() {});

            List<String> actions = objectMapper.convertValue(
                    root.get("actions"), new TypeReference<List<String>>() {});

            UseCaseDTO dto = new UseCaseDTO();
            dto.setDomainObjects(domainObjects);
            dto.setActions(actions);

            return dto;
        } catch (Exception e) {
            // If parsing fails, handle gracefully, or fallback
            // You might throw a custom exception or log the error.
            throw new RuntimeException("Failed to parse GPT response: " + gptResponse, e);
        }
    }



}
