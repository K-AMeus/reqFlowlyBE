package com.reqflowly.application.domainObject.dto;

import java.time.Instant;
import java.util.UUID;

public record DomainObjectAttributeDto(
        UUID id,
        UUID domainObjectId,
        String name,
        Instant createdAt,
        Instant updatedAt
) {
}
