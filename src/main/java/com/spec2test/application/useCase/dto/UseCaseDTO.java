package com.spec2test.application.useCase.dto;

import lombok.Data;
import java.util.List;

@Data
public class UseCaseDTO {
    private List<String> domainObjects;
    private List<String> suggestedDomainObjects;
    private List<String> actions;
    private List<String> suggestedActions;
}
