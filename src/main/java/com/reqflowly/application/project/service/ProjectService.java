package com.reqflowly.application.project.service;

import com.reqflowly.application.common.exception.ErrorCode;
import com.reqflowly.application.common.exception.InvalidStateException;
import com.reqflowly.application.project.dto.ProjectCreateRequestDto;
import com.reqflowly.application.project.dto.ProjectCreateResponseDto;
import com.reqflowly.application.project.dto.ProjectDto;
import com.reqflowly.application.project.dto.ProjectSort;
import com.reqflowly.application.project.mapper.ProjectMapper;
import com.reqflowly.application.project.model.Project;
import com.reqflowly.application.project.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectCreateResponseDto createProject(String userId, ProjectCreateRequestDto dto) {
        Project newProject = projectMapper.toEntity(dto);
        newProject.setUserId(userId);
        return projectMapper.toResponseDto(projectRepository.save(newProject));
    }

    public ProjectDto getProject(String userId, UUID projectId) {
        return projectMapper.toDto(projectRepository.findByUserIdAndId(userId, projectId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.PROJECT_NOT_FOUND)));
    }

    public Page<ProjectDto> getAllProjects(String userId, Pageable pageable, ProjectSort sort, Sort.Direction dir) {
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort.toSort(dir));

        return projectRepository.findAllByUserId(userId, sorted)
                .map(projectMapper::toDto);
    }

    @Transactional
    public ProjectCreateResponseDto updateProject(String userId, UUID projectId, ProjectCreateRequestDto req) {
        Project project = projectRepository.findByUserIdAndId(userId, projectId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.PROJECT_NOT_FOUND));

        projectMapper.updateProject(project, req);
        project = projectRepository.save(project);
        return projectMapper.toResponseDto(project);
    }

    @Transactional
    public void deleteProject(String userId, UUID projectId) {
        projectRepository.findByUserIdAndId(userId, projectId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.PROJECT_NOT_FOUND));

        projectRepository.deleteByUserIdAndId(userId, projectId);
    }
}
