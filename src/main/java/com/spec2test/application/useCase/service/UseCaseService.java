package com.spec2test.application.useCase.service;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatCompletion;
import com.openai.models.ChatCompletionCreateParams;
import com.openai.models.ChatModel;
import com.spec2test.application.common.exception.ErrorCode;
import com.spec2test.application.common.exception.InvalidStateException;
import com.spec2test.application.requirement.repository.RequirementRepository;
import com.spec2test.application.useCase.dto.UseCaseCreateReqDto;
import com.spec2test.application.useCase.dto.UseCaseCreateResDto;
import com.spec2test.application.useCase.dto.UseCaseDto;
import com.spec2test.application.useCase.mapper.UseCaseMapper;
import com.spec2test.application.useCase.model.UseCase;
import com.spec2test.application.useCase.repository.UseCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UseCaseService {

    private final UseCaseRepository useCaseRepository;
    private final RequirementRepository requirementRepository;
    private final UseCaseMapper useCaseMapper;
    private final OpenAIClient openaiClient;

    @Value("${USE_CASE_PROMPT}")
    private String USE_CASE_PROMPT;
    @Value("${ANTLR_GRAMMAR}")
    private String ANTLR_GRAMMAR;


    public List<UseCaseCreateResDto> generateUseCases(UUID projectId, UUID requirementId, UseCaseCreateReqDto req) {
        log.info("Received request: {}", req);
        requirementRepository.findByProjectIdAndId(projectId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.REQUIREMENT_NOT_FOUND));

        String domainObjects = req.domainObject();
        List<String> attributes = req.attributes();
        String aiResponse = callOpenAiForDomainObjects(domainObjects, attributes);
        log.info("AI response: {}", aiResponse);

        UseCase useCase = new UseCase();
        useCase.setName(domainObjects);
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

    private String callOpenAiForDomainObjects(String domainObject, List<String> attributes) {
        String prompt = this.USE_CASE_PROMPT;
        prompt += "* **Domain Entity Name:** "+ domainObject;
        prompt += "* **Attributes:** " + attributes;
        prompt += this.ANTLR_GRAMMAR;

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(prompt)
                .model(ChatModel.GPT_4_TURBO)
                .build();

        ChatCompletion chatCompletion = openaiClient.chat().completions().create(params);

        return chatCompletion.choices().getFirst().message().content().orElse("");
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