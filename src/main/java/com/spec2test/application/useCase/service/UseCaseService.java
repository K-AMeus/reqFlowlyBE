package com.spec2test.application.useCase.service;


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


    //ToDo: use mapper
    public UseCaseDTO processUseCaseText(UseCaseReq req) {
        String text = req.getDescription();
        String gptResponse = callOpenAiForDomainObjects(text);
        List<String> domainObjects = parseDomainObjects(gptResponse);
        UseCaseDTO response = new UseCaseDTO();
        response.setDomainObjects(domainObjects);
        return response;

    }

    public UseCaseDTO processUseCaseFile(MultipartFile file) {
        try {
            String text = extractTextFromPDF(file.getInputStream());
            String gptResponse = callOpenAiForDomainObjects(text);
            List<String> domainObjects = parseDomainObjects(gptResponse);
            UseCaseDTO response = new UseCaseDTO();
            response.setDomainObjects(domainObjects);
            return response;
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
        String prompt = "Extract the key domain objects (nouns) and important actions (verbs) from the following text:\n\n" + text;

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(prompt)
                .model(ChatModel.GPT_3_5_TURBO) // Change later
                .build();

        ChatCompletion chatCompletion = openaiClient.chat().completions().create(params);
        return chatCompletion.choices().getFirst().message().content().orElse("");
    }

    private List<String> parseDomainObjects(String gptResponse) {
        return Arrays.asList(gptResponse.split("\\s*,\\s*"));
    }


}
