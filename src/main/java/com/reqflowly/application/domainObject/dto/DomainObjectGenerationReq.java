package com.reqflowly.application.domainObject.dto;

import lombok.Data;

@Data
public class DomainObjectGenerationReq {
    private String description;
    private String customPrompt;
}
