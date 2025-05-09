package com.reqflowly.application.testCase.dto;

import java.util.UUID;

public record TestCaseCreateResDto(
        UUID id,
        String name,
        String content

) {
}
