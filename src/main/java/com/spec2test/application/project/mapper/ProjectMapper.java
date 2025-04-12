package com.spec2test.application.project.mapper;


import com.spec2test.application.project.dto.*;
import com.spec2test.application.project.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectCreateResponseDto toResponseDto(Project project);

    ProjectDto toDto(Project project);

    Project toEntity(ProjectCreateRequestDto dto);

    void updateProject(@MappingTarget Project project, ProjectCreateRequestDto req);
}
