package com.spec2test.application.domainObject.dto;

import java.util.List;

public record DomainObjectsCreateResponseDto(
        List<DomainObjectResponseDto> domainObjects
) {
}
