package com.spec2test.application.useCase.mapper;

import com.spec2test.application.useCase.dto.UseCaseCreateResDto;
import com.spec2test.application.useCase.model.UseCase;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface UseCaseMapper {

    UseCaseCreateResDto toDto(UseCase useCase);
}
