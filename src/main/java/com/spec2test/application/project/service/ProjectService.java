package com.spec2test.application.project.service;

import com.spec2test.application.common.exception.ErrorCode;
import com.spec2test.application.common.exception.InvalidStateException;
import com.spec2test.application.project.dto.*;
import com.spec2test.application.project.mapper.ProjectMapper;
import com.spec2test.application.project.model.Project;
import com.spec2test.application.project.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<ProjectDto> getAllProjects(String userId, Pageable pageable) {
        return projectRepository.findAllByUserIdOrderByUpdatedAtDesc(userId, pageable)
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
