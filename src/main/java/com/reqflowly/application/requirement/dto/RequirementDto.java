package com.reqflowly.application.requirement.dto;

import com.reqflowly.application.requirement.model.SourceType;

import java.time.Instant;
import java.util.UUID;

public record RequirementDto(
        UUID id,
        UUID projectId,
        String title,
        String description,
        SourceType sourceType,
        String sourceContent,
        String sourceFileUrl,
        Instant createdAt,
        Instant updatedAt
) {
}
