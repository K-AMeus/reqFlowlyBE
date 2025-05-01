package com.spec2test.application.domainObject.controller;

import com.spec2test.application.domainObject.dto.*;
import com.spec2test.application.domainObject.service.DomainObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/domain-object-service/v1/projects/{projectId}")
public class DomainObjectController {

    private final DomainObjectService domainObjectService;

    @PostMapping("/requirements/{requirementId}/domain-objects")
    @PreAuthorize("hasRole('USER')")
    public DomainObjectsCreateResponseDto createDomainObjectsWithAttributes(@PathVariable UUID projectId, @PathVariable UUID requirementId, @RequestBody DomainObjectsCreateRequestDto req) {
        return domainObjectService.createDomainObjects(projectId, requirementId, req);
    }

    @GetMapping("/{domainObjectId}")
    @PreAuthorize("hasRole('USER')")
    public DomainObjectDto getDomainObject(@PathVariable UUID projectId, @PathVariable UUID domainObjectId) {
        return domainObjectService.getDomainObject(projectId, domainObjectId);
    }

    // @GetMapping
    // @PreAuthorize("hasRole('USER')")
    // public Page<DomainObjectDto> getAllDomainObjects(@PathVariable UUID projectId, UUID requirementId, Pageable pageable) {
    //     return domainObjectService.getAllDomainObjects(projectId, requirementId, pageable);
    // }
    
    @GetMapping("/domain-objects")
    public DomainObjectsWithAttributesResponseDto getAllDomainObjectsWithAttributes(@PathVariable UUID projectId) {
        return domainObjectService.getAllDomainObjectsWithAttributes(projectId);
    }

    @PutMapping("/requirements/{requirementId}/domain-objects/{domainObjectId}")
    @PreAuthorize("hasRole('USER')")
    public DomainObjectCreateResponseDto updateDomainObject(@PathVariable UUID projectId, @PathVariable UUID requirementId, @PathVariable UUID domainObjectId, @RequestBody DomainObjectCreateRequestDto req) {
        return domainObjectService.updateDomainObject(projectId, domainObjectId, req);
    }

    @DeleteMapping("/requirements/{requirementId}/domain-objects/{domainObjectId}")
    @PreAuthorize("hasRole('USER')")
    public void deleteDomainObject(@PathVariable UUID projectId, @PathVariable UUID requirementId, @PathVariable UUID domainObjectId) {
        domainObjectService.deleteDomainObject(projectId, domainObjectId);
    }
}
