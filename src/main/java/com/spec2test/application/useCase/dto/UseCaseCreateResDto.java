package com.spec2test.application.useCase.dto;

import java.util.UUID;

public record UseCaseCreateResDto(
        UUID id,
        String name,
        String content
) {
}
