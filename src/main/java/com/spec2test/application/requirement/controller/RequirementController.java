package com.spec2test.application.requirement.controller;

import com.spec2test.application.requirement.dto.RequirementCreateRequestDto;
import com.spec2test.application.requirement.dto.RequirementCreateResponseDto;
import com.spec2test.application.requirement.dto.RequirementDto;
import com.spec2test.application.requirement.service.RequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/requirement-service/v1/projects/{projectId}/requirements")
public class RequirementController {
    private final RequirementService requirementService;


    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public RequirementCreateResponseDto createRequirement(@PathVariable UUID projectId, @RequestBody RequirementCreateRequestDto req) {
        return requirementService.createRequirement(projectId, req);
    }

    @GetMapping("/{requirementId}")
    @PreAuthorize("hasRole('USER')")
    public RequirementDto getRequirement(@PathVariable UUID projectId, @PathVariable UUID requirementId) {
        return requirementService.getRequirement(projectId, requirementId);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<RequirementDto> getAllRequirements(@PathVariable UUID projectId, Pageable pageable) {
        return requirementService.getAllRequirements(projectId, pageable);
    }

    @PutMapping("/{requirementId}")
    @PreAuthorize("hasRole('USER')")
    public RequirementCreateResponseDto updateRequirement(@PathVariable UUID projectId, @PathVariable UUID requirementId, @RequestBody RequirementCreateRequestDto req) {
        return requirementService.updateRequirement(projectId, requirementId, req);
    }

    @DeleteMapping("/{requirementId}")
    @PreAuthorize("hasRole('USER')")
    public void deleteRequirement(@PathVariable UUID projectId, @PathVariable UUID requirementId) {
        requirementService.deleteRequirement(projectId, requirementId);
    }




}
