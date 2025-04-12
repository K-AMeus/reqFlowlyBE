package com.spec2test.application.project.dto;

import java.util.UUID;

public record ProjectDto(
        UUID id,
        String name,
        String description
) {
}
