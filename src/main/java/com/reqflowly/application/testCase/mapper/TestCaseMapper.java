package com.reqflowly.application.testCase.mapper;


import com.reqflowly.application.testCase.dto.TestCaseCreateResDto;
import com.reqflowly.application.testCase.dto.TestCaseDto;
import com.reqflowly.application.testCase.model.TestCase;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TestCaseMapper {


    TestCaseCreateResDto toDto(TestCase testCase);

    void updateTestCase(@MappingTarget TestCase testCase, TestCaseDto req);
}
