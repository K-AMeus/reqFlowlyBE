package com.reqflowly.application.testCase.controller;

import com.reqflowly.application.testCase.dto.TestCaseCreateReqDto;
import com.reqflowly.application.testCase.dto.TestCaseCreateResDto;
import com.reqflowly.application.testCase.dto.TestCaseDto;
import com.reqflowly.application.testCase.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test-case-service/v1/projects/{projectId}/requirements/{requirementId}/use-cases/{useCaseId}/test-cases")
public class TestCaseController {
    private final TestCaseService testCaseService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public TestCaseCreateResDto createTestCases(@PathVariable UUID projectId, @PathVariable UUID requirementId, @PathVariable UUID useCaseId, @RequestBody TestCaseCreateReqDto req) {
        return testCaseService.createTestCases(projectId, requirementId, useCaseId, req);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<TestCaseCreateResDto> getTestCases(@PathVariable UUID projectId, @PathVariable UUID requirementId, @PathVariable UUID useCaseId) {
        return testCaseService.getTestCases(projectId, requirementId, useCaseId);
    }

    @PutMapping("/{testCaseId}")
    @PreAuthorize("hasRole('USER')")
    public TestCaseCreateResDto updateTestCase(@PathVariable UUID projectId, @PathVariable UUID requirementId, @PathVariable UUID useCaseId, @PathVariable UUID testCaseId, @RequestBody TestCaseDto req) {
        return testCaseService.updateTestCase(projectId, requirementId, useCaseId, testCaseId, req);
    }

    @DeleteMapping("/{testCaseId}")
    @PreAuthorize("hasRole('USER')")
    public void deleteUseCase(@PathVariable UUID projectId, @PathVariable UUID requirementId, @PathVariable UUID useCaseId, @PathVariable UUID testCaseId) {
        testCaseService.deleteUseCase(projectId, requirementId, useCaseId, testCaseId);
    }


}
