package com.reqflowly.application.requirement.controller;

import com.reqflowly.application.domainObject.service.DomainObjectService;
import com.reqflowly.application.requirement.dto.RequirementCreateRequestDto;
import com.reqflowly.application.requirement.dto.RequirementCreateResponseDto;
import com.reqflowly.application.requirement.dto.RequirementDto;
import com.reqflowly.application.requirement.service.RequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/requirement-service/v1/projects/{projectId}/requirements")
public class RequirementController {
    private final RequirementService requirementService;
    private final DomainObjectService domainObjectService;


    @PostMapping("/text")
    @PreAuthorize("hasRole('USER')")
    public RequirementCreateResponseDto createTextRequirement(@PathVariable UUID projectId, @RequestBody RequirementCreateRequestDto req) {
        return requirementService.createTextRequirement(projectId, req);
    }

    @PostMapping("/pdf")
    @PreAuthorize("hasRole('USER')")
    public RequirementCreateResponseDto createPdfRequirement(@PathVariable UUID projectId, @RequestParam("file") MultipartFile file, @RequestPart("metadata") RequirementCreateRequestDto req) {
        return requirementService.createPdfRequirement(file, projectId, req);
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

    @GetMapping("/used")
    @PreAuthorize("hasRole('USER')")
    public Page<RequirementDto> getUsedRequirements(@PathVariable UUID projectId, Pageable pageable) {
        List<UUID> requirementIds = domainObjectService.getDistinctRequirementIdsForProject(projectId);
        return requirementService.findRequirementsByIdsAndProject(projectId, requirementIds, pageable);
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
