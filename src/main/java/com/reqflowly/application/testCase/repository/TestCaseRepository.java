package com.reqflowly.application.testCase.repository;

import com.reqflowly.application.testCase.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, UUID> {

    List<TestCase> findAllByUseCaseIdAndRequirementId(UUID useCaseId, UUID requirementId);

    Optional<TestCase> findByIdAndRequirementId(UUID id, UUID requirementId);
}
