package com.spec2test.application.useCase.dto;

import java.util.List;

public record UseCaseCreateReqDto(
        String domainObject,
        List<String> attributes
) {
}
