package com.spec2test.application.domainObject.controller;


import com.spec2test.application.domainObject.dto.DomainObjectGenerationDto;
import com.spec2test.application.domainObject.dto.DomainObjectGenerationReq;
import com.spec2test.application.domainObject.service.DomainObjectGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/domain-object-service/v1/generation")
public class DomainObjectGenerationController {

    private final DomainObjectGenerationService domainObjectGenerationService;

    @PostMapping("/text")
    @PreAuthorize("hasRole('USER')")
    public DomainObjectGenerationDto processUseCaseText(@RequestBody DomainObjectGenerationReq req) {
        return domainObjectGenerationService.processUseCaseText(req);
    }

    @PostMapping("/pdf")
    @PreAuthorize("hasRole('USER')")
    public DomainObjectGenerationDto processUseCaseFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "customPrompt", required = false) String customPrompt) {
        return domainObjectGenerationService.processUseCaseFile(file, customPrompt);
    }
}
