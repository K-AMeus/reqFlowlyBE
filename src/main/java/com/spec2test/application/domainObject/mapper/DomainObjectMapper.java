package com.spec2test.application.domainObject.mapper;

import com.spec2test.application.domainObject.dto.DomainObjectCreateRequestDto;
import com.spec2test.application.domainObject.dto.DomainObjectCreateResponseDto;
import com.spec2test.application.domainObject.dto.DomainObjectDto;
import com.spec2test.application.domainObject.dto.DomainObjectRequirementIds;
import com.spec2test.application.domainObject.model.DomainObject;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper(componentModel = "Spring")
public interface DomainObjectMapper {

    DomainObjectCreateResponseDto toCreateResponse(DomainObject domainObject);

    DomainObject toEntity(DomainObjectCreateRequestDto req);

    DomainObjectDto toDto(DomainObject domainObject);

    void updateDomainObject(@MappingTarget DomainObject domainObject, DomainObjectCreateRequestDto dto);

    DomainObjectRequirementIds toDto(UUID requirementId);

}
