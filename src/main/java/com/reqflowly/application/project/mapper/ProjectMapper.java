package com.reqflowly.application.project.mapper;


import com.reqflowly.application.project.dto.ProjectCreateRequestDto;
import com.reqflowly.application.project.dto.ProjectCreateResponseDto;
import com.reqflowly.application.project.dto.ProjectDto;
import com.reqflowly.application.project.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectCreateResponseDto toResponseDto(Project project);

    ProjectDto toDto(Project project);

    Project toEntity(ProjectCreateRequestDto dto);

    void updateProject(@MappingTarget Project project, ProjectCreateRequestDto req);
}
