package com.spec2test.application.project.dto;

import java.time.Instant;
import java.util.UUID;

public record ProjectCreateResponseDto(
        UUID id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}
