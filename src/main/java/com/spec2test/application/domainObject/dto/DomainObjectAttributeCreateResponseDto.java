package com.spec2test.application.domainObject.dto;

import java.time.Instant;
import java.util.UUID;

public record DomainObjectAttributeCreateResponseDto(
        UUID id,
        UUID domainObjectId,
        String name,
        Instant createdAt,
        Instant updatedAt
) {
}
