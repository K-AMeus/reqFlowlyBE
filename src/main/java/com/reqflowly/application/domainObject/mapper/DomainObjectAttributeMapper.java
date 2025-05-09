package com.reqflowly.application.domainObject.mapper;


import com.reqflowly.application.domainObject.dto.DomainObjectAttributeCreateRequestDto;
import com.reqflowly.application.domainObject.dto.DomainObjectAttributeCreateResponseDto;
import com.reqflowly.application.domainObject.dto.DomainObjectAttributeDto;
import com.reqflowly.application.domainObject.model.DomainObjectAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface DomainObjectAttributeMapper {

    DomainObjectAttributeCreateResponseDto toCreateResponse(DomainObjectAttribute domainObjectAttribute);

    DomainObjectAttribute toEntity(DomainObjectAttributeCreateRequestDto req);

    DomainObjectAttributeDto toDto(DomainObjectAttribute domainObjectAttribute);

    void updateDomainObjectAttribute(@MappingTarget DomainObjectAttribute domainObjectAttribute, DomainObjectAttributeCreateRequestDto dto);
}
