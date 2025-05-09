package com.reqflowly.application.requirement.mapper;


import com.reqflowly.application.requirement.dto.RequirementCreateRequestDto;
import com.reqflowly.application.requirement.dto.RequirementCreateResponseDto;
import com.reqflowly.application.requirement.dto.RequirementDto;
import com.reqflowly.application.requirement.model.Requirement;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface RequirementMapper {

    RequirementDto toDto(Requirement requirement);

    Requirement toEntity(RequirementCreateRequestDto req);

    RequirementCreateResponseDto toResponseDto(Requirement requirement);

    void updateRequirement(@MappingTarget Requirement requirement, RequirementCreateRequestDto dto);

}
