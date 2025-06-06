package com.reqflowly.application.useCase.dto;

import java.util.UUID;

public record UseCaseCreateResDto(
        UUID id,
        String name,
        String content
) {
}
