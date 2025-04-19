package com.spec2test.application.domainObject.mapper;


import com.spec2test.application.domainObject.dto.*;
import com.spec2test.application.domainObject.model.DomainObjectAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface DomainObjectAttributeMapper {

    DomainObjectAttributeCreateResponseDto toCreateResponse(DomainObjectAttribute domainObjectAttribute);

    DomainObjectAttribute toEntity(DomainObjectAttributeCreateRequestDto req);

    DomainObjectAttributeDto toDto(DomainObjectAttribute domainObjectAttribute);

    void updateDomainObjectAttribute(@MappingTarget DomainObjectAttribute domainObjectAttribute, DomainObjectAttributeCreateRequestDto dto);
}
