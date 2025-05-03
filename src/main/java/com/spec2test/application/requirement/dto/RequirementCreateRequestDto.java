package com.spec2test.application.requirement.dto;

public record RequirementCreateRequestDto(
        String title,
        String description,
        String sourceType,
        String sourceContent,
        String sourceFileUrl
) {
}
