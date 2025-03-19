package com.spec2test.application.useCase.controller;


import com.spec2test.application.useCase.dto.UseCaseDTO;
import com.spec2test.application.useCase.dto.UseCaseReq;
import com.spec2test.application.useCase.service.UseCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usecase-service/v1/usecases")
public class UseCaseController {

    private final UseCaseService useCaseService;

    @PostMapping("/text")
    public UseCaseDTO processUseCaseText(@RequestBody UseCaseReq req) {
        return useCaseService.processUseCaseText(req);
    }

    @PostMapping("/upload")
    public UseCaseDTO processUseCaseFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "customPrompt", required = false) String customPrompt) {
        return useCaseService.processUseCaseFile(file, customPrompt);
    }
}
