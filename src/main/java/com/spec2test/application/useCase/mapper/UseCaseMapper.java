package com.spec2test.application.useCase.mapper;

import com.spec2test.application.useCase.dto.UseCaseCreateResDto;
import com.spec2test.application.useCase.dto.UseCaseDto;
import com.spec2test.application.useCase.model.UseCase;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface UseCaseMapper {

    UseCaseCreateResDto toDto(UseCase useCase);

    void updateUseCase(@MappingTarget UseCase useCase, UseCaseDto req);

}
