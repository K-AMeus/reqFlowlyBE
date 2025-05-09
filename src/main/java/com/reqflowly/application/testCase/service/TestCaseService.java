package com.reqflowly.application.testCase.service;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatCompletion;
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


    public TestCaseCreateResDto createTestCases(UUID projectId, UUID requirementId, UUID useCaseId, TestCaseCreateReqDto req) {
        log.info("Received request: {}", req);
        requirementRepository.findByProjectIdAndId(projectId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.REQUIREMENT_NOT_FOUND));

        String useCases = req.useCaseContent();
        String aiResponse = callOpenAiForTestCases(useCases);
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


    private String callOpenAiForTestCases(String useCases) {
        String prompt = this.TEST_CASE_PROMPT;
        prompt += useCases;
        prompt += "Now generate the test cases based on the above instructions";
        log.info("prompt: " +  prompt);

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(prompt)
                .model(ChatModel.GPT_4_TURBO)
                .build();

        ChatCompletion chatCompletion = openaiClient.chat().completions().create(params);

        return chatCompletion.choices().getFirst().message().content().orElse("");
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
