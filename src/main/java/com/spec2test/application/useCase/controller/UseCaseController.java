package com.spec2test.application.useCase.controller;


import com.spec2test.application.useCase.dto.UseCaseCreateReqDto;
import com.spec2test.application.useCase.dto.UseCaseCreateResDto;
import com.spec2test.application.useCase.service.UseCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/use-case-service/v1/projects/{projectId}/requirements/{requirementId}/use-cases")
@RequiredArgsConstructor
public class UseCaseController {
    private final UseCaseService useCaseService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public List<UseCaseCreateResDto> generateUseCases(@PathVariable UUID projectId, @PathVariable UUID requirementId, @RequestBody UseCaseCreateReqDto req) {
        return useCaseService.generateUseCases(projectId, requirementId, req);
    }
}
