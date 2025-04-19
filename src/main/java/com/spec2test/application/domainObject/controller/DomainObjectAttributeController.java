package com.spec2test.application.domainObject.controller;

import com.spec2test.application.domainObject.dto.DomainObjectAttributeCreateRequestDto;
import com.spec2test.application.domainObject.dto.DomainObjectAttributeCreateResponseDto;
import com.spec2test.application.domainObject.dto.DomainObjectAttributeDto;
import com.spec2test.application.domainObject.service.DomainObjectAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/domain-object-service/v1/projects/{projectId}/domain-objects/{domainObjectId}/attributes")
public class DomainObjectAttributeController {

    private final DomainObjectAttributeService domainObjectAttributeService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public DomainObjectAttributeCreateResponseDto createAttribute(@PathVariable UUID projectId, @PathVariable UUID domainObjectId, @RequestBody DomainObjectAttributeCreateRequestDto req) {
        return domainObjectAttributeService.createAttribute(projectId, domainObjectId, req);
    }

    @GetMapping("{attributeId}")
    @PreAuthorize("hasRole('USER')")
    public DomainObjectAttributeDto getAttribute(@PathVariable UUID projectId, @PathVariable UUID domainObjectId, @PathVariable UUID attributeId) {
        return domainObjectAttributeService.getAttribute(projectId, domainObjectId, attributeId);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<DomainObjectAttributeDto> getAllAttributes(@PathVariable UUID projectId, @PathVariable UUID domainObjectId, Pageable pageable) {
        return domainObjectAttributeService.getAllAttributes(projectId, domainObjectId, pageable);
    }

    @PutMapping("/{attributeId}")
    @PreAuthorize("hasRole('USER')")
    public DomainObjectAttributeCreateResponseDto updateAttribute(@PathVariable UUID projectId, @PathVariable UUID domainObjectId, @PathVariable UUID attributeId, @RequestBody DomainObjectAttributeCreateRequestDto req) {
        return domainObjectAttributeService.updateAttribute(projectId, domainObjectId, attributeId, req);
    }

    @DeleteMapping("/{attributeId}")
    @PreAuthorize("hasRole('USER')")
    public void deleteAttribute(@PathVariable UUID projectId, @PathVariable UUID domainObjectId, @PathVariable UUID attributeId) {
        domainObjectAttributeService.deleteAttribute(projectId, domainObjectId, attributeId);
    }
}
