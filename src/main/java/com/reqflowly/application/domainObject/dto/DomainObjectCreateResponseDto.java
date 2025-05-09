package com.reqflowly.application.domainObject.dto;

import java.time.Instant;
import java.util.UUID;

public record DomainObjectCreateResponseDto(
        UUID id,
        String name,
        Instant createdAt,
        Instant updatedAt
) {
}
