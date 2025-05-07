package com.spec2test.application.testCase.mapper;


import com.spec2test.application.testCase.dto.TestCaseCreateResDto;
import com.spec2test.application.testCase.dto.TestCaseDto;
import com.spec2test.application.testCase.model.TestCase;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TestCaseMapper {


    TestCaseCreateResDto toDto(TestCase testCase);

    void updateTestCase(@MappingTarget TestCase testCase, TestCaseDto req);
}
