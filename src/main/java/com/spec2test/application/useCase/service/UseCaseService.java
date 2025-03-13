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
                "from the following text. Always no matter what return valid json and so that both domain objects and actions exist (they can be empty). Return the result as valid JSON only (do not include any markdown formatting or triple backticks) in the following format:\n\n" +
                "{\n" +
                "  \"domainObjects\": [\"object1\", \"object2\"],\n" +
                "  \"actions\": [\"action1\", \"action2\"]\n" +
                "}\n\n" +
                "Text:\n" + text;

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

            List<String> domainObjects = objectMapper.convertValue(
                    root.get("domainObjects"), new TypeReference<>() {
                    });

            List<String> actions = objectMapper.convertValue(
                    root.get("actions"), new TypeReference<>() {
                    });

            UseCaseDTO dto = new UseCaseDTO();
            dto.setDomainObjects(domainObjects);
            dto.setActions(actions);

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
