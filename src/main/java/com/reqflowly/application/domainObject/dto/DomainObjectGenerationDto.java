package com.reqflowly.application.domainObject.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DomainObjectGenerationDto {
    private Map<String, List<String>> domainObjects;
    private Map<String, List<String>> suggestedDomainObjects;
}
