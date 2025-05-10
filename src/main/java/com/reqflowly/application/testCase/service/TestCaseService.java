package com.reqflowly.application.testCase.service;

import org.springframework.lang.Nullable;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatCompletionCreateParams;
import com.openai.models.ChatModel;
import com.reqflowly.application.common.exception.ErrorCode;
import com.reqflowly.application.common.exception.InvalidStateException;
import com.reqflowly.application.requirement.repository.RequirementRepository;
import com.reqflowly.application.testCase.dto.TestCaseCreateReqDto;
import com.reqflowly.application.testCase.dto.TestCaseCreateResDto;
import com.reqflowly.application.testCase.dto.TestCaseDto;
import com.reqflowly.application.testCase.mapper.TestCaseMapper;
import com.reqflowly.application.testCase.model.TestCase;
import com.reqflowly.application.testCase.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestCaseService {
    private final TestCaseRepository testCaseRepository;
    private final RequirementRepository requirementRepository;
    private final TestCaseMapper testCaseMapper;
    private final OpenAIClient openaiClient;

    @Value("${TEST_CASE_PROMPT}")
    private String TEST_CASE_PROMPT;

    private static final String ADDITIONAL_HEADER = "Additional custom instructions:";
    private static final String ADDITIONAL_FOOTER = "End of custom instructions";
    private static final String USE_CASE_HEADER = "**Input use-case specifications:**";


    public TestCaseCreateResDto createTestCases(UUID projectId, UUID requirementId, UUID useCaseId, TestCaseCreateReqDto req) {
        log.info("Received request: {}", req);
        requirementRepository.findByProjectIdAndId(projectId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.REQUIREMENT_NOT_FOUND));

        String useCases = req.useCaseContent();
        String aiResponse = callOpenAiForTestCases(useCases, req.customPrompt());
        log.info("AI response: {}", aiResponse);

        TestCase testCase = new TestCase();
        testCase.setName("test cases for use case " + req.useCaseName());
        testCase.setContent(aiResponse);
        testCase.setUseCaseId(useCaseId);
        testCase.setRequirementId(requirementId);

        TestCase savedTestCase = testCaseRepository.save(testCase);
        log.info("Saved test case with name: {}", savedTestCase.getName());

        return new TestCaseCreateResDto(savedTestCase.getId(), savedTestCase.getName(), savedTestCase.getContent());
    }

    private String callOpenAiForTestCases(String useCases, String customPrompt) {
        String prompt = buildPrompt(useCases, customPrompt);

        log.debug("OpenAI prompt:\n{}", prompt);

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(prompt)
                .model(ChatModel.GPT_4_TURBO)
                .build();

        return openaiClient.chat()
                .completions()
                .create(params)
                .choices()
                .getFirst()
                .message()
                .content()
                .orElse("");
    }

    private String buildPrompt(String useCases, @Nullable String customPrompt) {
        StringBuilder sb = new StringBuilder(TEST_CASE_PROMPT).append('\n');

        Optional.ofNullable(customPrompt)
                .filter(s -> !s.isBlank())
                .ifPresent(cp -> sb.append('\n')
                        .append(ADDITIONAL_HEADER).append('\n')
                        .append(cp.trim()).append('\n')
                        .append(ADDITIONAL_FOOTER).append("\n\n"));

        sb.append(USE_CASE_HEADER).append('\n')
                .append(useCases.trim()).append("\n\n")
                .append("Now generate the test cases based on the above instructions.");

        return sb.toString();
    }

    public List<TestCaseCreateResDto> getTestCases(UUID projectId, UUID requirementId, UUID useCaseId) {
        requirementRepository.findByProjectIdAndId(projectId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.REQUIREMENT_NOT_FOUND));

        List<TestCase> testCases = testCaseRepository.findAllByUseCaseIdAndRequirementId(useCaseId, requirementId);

        return testCases.stream()
                .map(testCaseMapper::toDto)
                .collect(Collectors.toList());
    }

    public TestCaseCreateResDto updateTestCase(UUID projectId, UUID requirementId, UUID useCaseId, UUID testCaseId, TestCaseDto req) {
        requirementRepository.findByProjectIdAndId(projectId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.REQUIREMENT_NOT_FOUND));

        TestCase testCase = testCaseRepository.findByIdAndRequirementId(testCaseId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.TEST_CASE_NOT_FOUND));

        testCaseMapper.updateTestCase(testCase, req);
        testCase = testCaseRepository.save(testCase);
        return testCaseMapper.toDto(testCase);
    }



    public void deleteUseCase(UUID projectId, UUID requirementId, UUID useCaseId, UUID testCaseId) {
        requirementRepository.findByProjectIdAndId(projectId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.REQUIREMENT_NOT_FOUND));

        TestCase testCase = testCaseRepository.findByIdAndRequirementId(testCaseId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.TEST_CASE_NOT_FOUND));

        testCaseRepository.delete(testCase);


    }


}
