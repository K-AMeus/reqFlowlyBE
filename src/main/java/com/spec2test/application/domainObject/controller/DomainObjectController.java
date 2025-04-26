package com.spec2test.application.domainObject.controller;

import com.spec2test.application.domainObject.dto.*;
import com.spec2test.application.domainObject.service.DomainObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/domain-object-service/v1/projects/{projectId}/domain-objects")
public class DomainObjectController {

    private final DomainObjectService domainObjectService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public DomainObjectsCreateResponseDto createDomainObjects(@PathVariable UUID projectId, @RequestBody DomainObjectsCreateRequestDto req) {
        return domainObjectService.createDomainObjects(projectId, req);
    }

    @GetMapping("/{domainObjectId}")
    @PreAuthorize("hasRole('USER')")
    public DomainObjectDto getDomainObject(@PathVariable UUID projectId, @PathVariable UUID domainObjectId) {
        return domainObjectService.getDomainObject(projectId, domainObjectId);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<DomainObjectDto> getAllDomainObjects(@PathVariable UUID projectId, Pageable pageable) {
        return domainObjectService.getAllDomainObjects(projectId, pageable);
    }

    @PutMapping("/{domainObjectId}")
    @PreAuthorize("hasRole('USER')")
    public DomainObjectCreateResponseDto updateDomainObject(@PathVariable UUID projectId, @PathVariable UUID domainObjectId, @RequestBody DomainObjectCreateRequestDto req) {
        return domainObjectService.updateDomainObject(projectId, domainObjectId, req);
    }

    @DeleteMapping("/{domainObjectId}")
    @PreAuthorize("hasRole('USER')")
    public void deleteDomainObject(@PathVariable UUID projectId, @PathVariable UUID domainObjectId) {
        domainObjectService.deleteDomainObject(projectId, domainObjectId);
    }
}
