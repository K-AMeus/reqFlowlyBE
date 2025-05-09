package com.reqflowly.application.domainObject.dto;

import java.util.List;
import java.util.Map;

public record DomainObjectsCreateRequestDto(
        Map<String, List<DomainObjectAttributeCreateRequestDto>> domainObjectsWithAttributes
) {
}
