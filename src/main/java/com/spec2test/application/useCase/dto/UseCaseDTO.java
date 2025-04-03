package com.spec2test.application.useCase.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class UseCaseDTO {
    private Map<String, List<String>> domainObjects;
    private Map<String, List<String>> suggestedDomainObjects;
}
