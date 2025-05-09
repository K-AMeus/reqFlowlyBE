package com.reqflowly.application.domainObject.mapper;

import com.reqflowly.application.domainObject.model.DomainObject;
import com.reqflowly.application.domainObject.dto.DomainObjectCreateRequestDto;
import com.reqflowly.application.domainObject.dto.DomainObjectCreateResponseDto;
import com.reqflowly.application.domainObject.dto.DomainObjectDto;
import com.reqflowly.application.domainObject.dto.DomainObjectRequirementIds;
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
