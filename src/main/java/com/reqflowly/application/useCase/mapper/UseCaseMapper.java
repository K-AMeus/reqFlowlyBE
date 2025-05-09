package com.reqflowly.application.useCase.mapper;

import com.reqflowly.application.useCase.dto.UseCaseCreateResDto;
import com.reqflowly.application.useCase.dto.UseCaseDto;
import com.reqflowly.application.useCase.model.UseCase;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface UseCaseMapper {

    UseCaseCreateResDto toDto(UseCase useCase);

    void updateUseCase(@MappingTarget UseCase useCase, UseCaseDto req);

}
