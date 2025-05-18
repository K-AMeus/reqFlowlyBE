package com.reqflowly.application.useCase.service;

import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.cloud.vertexai.generativeai.ResponseStream;
import com.reqflowly.application.common.exception.ErrorCode;
import com.reqflowly.application.common.exception.InvalidStateException;
import com.reqflowly.application.requirement.repository.RequirementRepository;
import com.reqflowly.application.useCase.dto.UseCaseCreateReqDto;
import com.reqflowly.application.useCase.dto.UseCaseCreateResDto;
import com.reqflowly.application.useCase.dto.UseCaseDto;
import com.reqflowly.application.useCase.mapper.UseCaseMapper;
import com.reqflowly.application.useCase.model.UseCase;
import com.reqflowly.application.useCase.repository.UseCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UseCaseService {

    private final UseCaseRepository useCaseRepository;
    private final RequirementRepository requirementRepository;
    private final UseCaseMapper useCaseMapper;
    private final GenerativeModel geminiTextModel;

    @Value("${USE_CASE_PROMPT}")
    private String USE_CASE_PROMPT;
    @Value("${ANTLR_GRAMMAR}")
    private String ANTLR_GRAMMAR;

    private static final String ADDITIONAL_HEADER = "Additional custom instructions:";
    private static final String ADDITIONAL_FOOTER = "End of custom instructions";
    private static final String ENTITY_LABEL = "* **Domain Entity Name:** ";
    private static final String ATTR_LABEL = "* **Attributes:** ";

    private static final Pattern LEADING_CODE_FENCE =
            Pattern.compile("^```[\\p{Alnum}-]*[\\r\\n]+");

    private static String stripMarkdownFence(String text) {
        if (text == null) return null;

        String body = LEADING_CODE_FENCE.matcher(text).replaceFirst("");
        body = body.replaceFirst("[\\r\\n]+```\\s*$", "");
        return body.trim();
    }


    public List<UseCaseCreateResDto> generateUseCases(UUID projectId, UUID requirementId, UseCaseCreateReqDto req) {
        log.info("Received request: {}", req);
        requirementRepository.findByProjectIdAndId(projectId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.REQUIREMENT_NOT_FOUND));

        String aiResponse = callAiForUseCases(req.domainObject(), req.attributes(), req.customPrompt());
        log.info("AI response: {}", aiResponse);

        UseCase useCase = new UseCase();
        useCase.setName(req.domainObject());
        useCase.setContent(aiResponse);
        useCase.setRequirementId(requirementId);
        useCase.setCreatedAt(Instant.now());
        useCase.setUpdatedAt(Instant.now());
        
        UseCase savedUseCase = useCaseRepository.save(useCase);
        log.info("Saved use case with name: {}", savedUseCase.getName());
        
        // Todo: change: Return as a list for API consistency
        List<UseCaseCreateResDto> result = new ArrayList<>();
        result.add(new UseCaseCreateResDto(savedUseCase.getId(), savedUseCase.getName(), savedUseCase.getContent()));
        return result;
    }

    private String callAiForUseCases(String domainObject, List<String> attributes, @Nullable String customPrompt) {


        String prompt = buildPrompt(domainObject, attributes, customPrompt);
        log.info("Use case prompt:\n{}", prompt);

        ResponseStream<GenerateContentResponse> stream;
        try {
            stream = geminiTextModel.generateContentStream(prompt);
        } catch (IOException e) {
            throw new RuntimeException("Error streaming use cases from AI", e);
        }

        StringBuilder sb = new StringBuilder();
        for (GenerateContentResponse chunk : stream) {
            String text = ResponseHandler.getText(chunk);
            sb.append(text);
        }
        return stripMarkdownFence(sb.toString());
    }

    private String buildPrompt(String domainObject,
                               List<String> attributes,
                               @Nullable String customPrompt) {

        StringBuilder sb = new StringBuilder(USE_CASE_PROMPT).append('\n');

        Optional.ofNullable(customPrompt)
                .filter(s -> !s.isBlank())
                .ifPresent(cp -> sb.append('\n')
                        .append(ADDITIONAL_HEADER).append('\n')
                        .append(cp.trim()).append('\n')
                        .append(ADDITIONAL_FOOTER).append("\n\n"));

        sb.append(ENTITY_LABEL).append(domainObject).append('\n')
                .append(ATTR_LABEL).append(attributes).append("\n\n")
                .append(ANTLR_GRAMMAR);

        return sb.toString();
    }


    public List<UseCaseCreateResDto> getUseCases(UUID projectId, UUID requirementId) {
        requirementRepository.findByProjectIdAndId(projectId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.REQUIREMENT_NOT_FOUND));

        List<UseCase> useCases = useCaseRepository.findAllByRequirementId(requirementId);
        return useCases.stream()
                .map(useCaseMapper::toDto)
                .collect(Collectors.toList());
    }


    public UseCaseCreateResDto updateUseCase(UUID projectId, UUID requirementId, UUID useCaseId, UseCaseDto req) {
        requirementRepository.findByProjectIdAndId(projectId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.REQUIREMENT_NOT_FOUND));

        UseCase useCase = useCaseRepository.findByIdAndRequirementId(useCaseId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.USE_CASE_NOT_FOUND));

        useCaseMapper.updateUseCase(useCase, req);
        useCase = useCaseRepository.save(useCase);
        return useCaseMapper.toDto(useCase);
    }


    public void deleteUseCase(UUID projectId, UUID requirementId, UUID useCaseId) {
        requirementRepository.findByProjectIdAndId(projectId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.REQUIREMENT_NOT_FOUND));

        UseCase useCase = useCaseRepository.findByIdAndRequirementId(useCaseId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.USE_CASE_NOT_FOUND));

        useCaseRepository.delete(useCase);
    }
}