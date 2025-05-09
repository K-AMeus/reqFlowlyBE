package com.reqflowly.application.domainObject.dto;

import java.util.List;
import java.util.Map;

public record DomainObjectsWithAttributesResponseDto(
        Map<String, List<DomainObjectAttributeDto>> domainObjectsWithAttributes
) {
} 