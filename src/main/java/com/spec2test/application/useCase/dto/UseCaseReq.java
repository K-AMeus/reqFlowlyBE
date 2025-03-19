package com.spec2test.application.useCase.dto;

import lombok.Data;

@Data
public class UseCaseReq {
    private String description;

    // todo: remove
    private String customPrompt;
}
