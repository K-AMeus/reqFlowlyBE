package com.spec2test.application.project.dto;

import java.util.UUID;

public record ProjectCreateResponseDto(
        UUID id,
        String name,
        String description
) {
}
