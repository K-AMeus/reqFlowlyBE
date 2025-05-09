package com.reqflowly.application.project.dto;

import java.time.Instant;
import java.util.UUID;

public record ProjectDto(
        UUID id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}
