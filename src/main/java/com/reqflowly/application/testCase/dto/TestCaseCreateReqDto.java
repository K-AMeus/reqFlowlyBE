package com.reqflowly.application.testCase.dto;

public record TestCaseCreateReqDto(
        String useCaseName,
        String useCaseContent,
        String customPrompt
) {
}
