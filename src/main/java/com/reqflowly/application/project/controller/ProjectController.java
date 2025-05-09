package com.reqflowly.application.project.controller;

import com.reqflowly.application.project.dto.ProjectCreateRequestDto;
import com.reqflowly.application.project.dto.ProjectCreateResponseDto;
import com.reqflowly.application.project.dto.ProjectDto;
import com.reqflowly.application.project.dto.ProjectSort;
import com.reqflowly.application.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project-service/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ProjectCreateResponseDto createProject(@AuthenticationPrincipal String userId, @RequestBody ProjectCreateRequestDto dto) {
        return projectService.createProject(userId, dto);
    }

    @GetMapping("/{projectId}")
    @PreAuthorize("hasRole('USER')")
    public ProjectDto getProject(@AuthenticationPrincipal String userId, @PathVariable UUID projectId) {
        return projectService.getProject(userId, projectId);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<ProjectDto> getAllProjects(@AuthenticationPrincipal String userId, Pageable pageable,
            @RequestParam(value = "orderBy", defaultValue = "updatedAt")
            String orderByParam,
            @RequestParam(value = "direction", defaultValue = "DESC")
            String dirParam) {

        ProjectSort orderBy = ProjectSort.from(orderByParam);
        Sort.Direction direction = Sort.Direction.fromString(dirParam);

        return projectService.getAllProjects(userId, pageable, orderBy, direction);
    }

    @PutMapping("/{projectId}")
    @PreAuthorize("hasRole('USER')")
    public ProjectCreateResponseDto updateProject(@AuthenticationPrincipal String userId, @PathVariable UUID projectId, @RequestBody ProjectCreateRequestDto dto) {
        return projectService.updateProject(userId, projectId, dto);
    }

    @DeleteMapping("/{projectId}")
    @PreAuthorize("hasRole('USER')")
    public void deleteProject(@AuthenticationPrincipal String userId, @PathVariable UUID projectId) {
        projectService.deleteProject(userId, projectId);
    }

}
