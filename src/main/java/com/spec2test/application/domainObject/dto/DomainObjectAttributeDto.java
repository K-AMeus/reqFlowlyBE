package com.spec2test.application.domainObject.dto;

import java.time.Instant;
import java.util.UUID;

public record DomainObjectAttributeDto(
        UUID id,
        UUID domainObjectId,
        String name,
        String dataType,
        Instant createdAt,
        Instant updatedAt
) {
}
