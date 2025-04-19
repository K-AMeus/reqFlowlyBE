package com.spec2test.application.requirement.mapper;


import com.spec2test.application.requirement.dto.RequirementCreateRequestDto;
import com.spec2test.application.requirement.dto.RequirementCreateResponseDto;
import com.spec2test.application.requirement.dto.RequirementDto;
import com.spec2test.application.requirement.model.Requirement;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface RequirementMapper {

    RequirementDto toDto(Requirement requirement);

    Requirement toEntity(RequirementCreateRequestDto req);

    RequirementCreateResponseDto toResponseDto(Requirement requirement);

    void updateRequirement(@MappingTarget Requirement requirement, RequirementCreateRequestDto dto);

}
