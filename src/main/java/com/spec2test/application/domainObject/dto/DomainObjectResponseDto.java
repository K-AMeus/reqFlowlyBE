package com.spec2test.application.domainObject.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DomainObjectResponseDto(
        UUID id,
        String name,
        Instant createdAt,
        Instant updatedAt,
        List<DomainObjectAttributeCreateResponseDto> attributes
) {
}