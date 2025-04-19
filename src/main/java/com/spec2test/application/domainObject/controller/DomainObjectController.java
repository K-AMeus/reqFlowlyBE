package com.spec2test.application.domainObject.controller;

import com.spec2test.application.domainObject.dto.DomainObjectCreateRequestDto;
import com.spec2test.application.domainObject.dto.DomainObjectCreateResponseDto;
import com.spec2test.application.domainObject.dto.DomainObjectDto;
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
    public DomainObjectCreateResponseDto createDomainObject(@PathVariable UUID projectId, @RequestBody DomainObjectCreateRequestDto req) {
        return domainObjectService.createDomainObject(projectId, req);
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
