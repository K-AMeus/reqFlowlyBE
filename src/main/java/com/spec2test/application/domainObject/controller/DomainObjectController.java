package com.spec2test.application.domainObject.controller;

import com.spec2test.application.domainObject.dto.*;
import com.spec2test.application.domainObject.service.DomainObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/domain-object-service/v1/projects/{projectId}/requirements/{requirementId}/domain-objects")
public class DomainObjectController {

    private final DomainObjectService domainObjectService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public DomainObjectsCreateResponseDto createDomainObjectsWithAttributes(@PathVariable UUID projectId, @PathVariable UUID requirementId, @RequestBody DomainObjectsCreateRequestDto req) {
        return domainObjectService.createDomainObjects(projectId, requirementId, req);
    }

    @GetMapping("/{domainObjectId}")
    @PreAuthorize("hasRole('USER')")
    public DomainObjectDto getDomainObject(@PathVariable UUID projectId, @PathVariable UUID domainObjectId) {
        return domainObjectService.getDomainObject(projectId, domainObjectId);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public DomainObjectsWithAttributesResponseDto getDomainObjectsWithAttributesByRequirement(@PathVariable UUID projectId, @PathVariable UUID requirementId) {
        return domainObjectService.getAllDomainObjectsWithAttributes(projectId, requirementId);
    }

    @PutMapping("/{domainObjectId}")
    @PreAuthorize("hasRole('USER')")
    public DomainObjectCreateResponseDto updateDomainObject(@PathVariable UUID projectId, @PathVariable UUID requirementId, @PathVariable UUID domainObjectId, @RequestBody DomainObjectCreateRequestDto req) {
        return domainObjectService.updateDomainObject(projectId, domainObjectId, req);
    }

    @DeleteMapping("/{domainObjectId}")
    @PreAuthorize("hasRole('USER')")
    public void deleteDomainObject(@PathVariable UUID projectId, @PathVariable UUID requirementId, @PathVariable UUID domainObjectId) {
        domainObjectService.deleteDomainObject(projectId, domainObjectId);
    }
}
