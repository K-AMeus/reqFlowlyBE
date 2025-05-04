package com.spec2test.application.useCase.repository;

import com.spec2test.application.useCase.model.UseCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UseCaseRepository extends JpaRepository<UseCase, UUID> {


    List<UseCase> findAllByRequirementId(@Param("requirementId") UUID requirementId);

    Optional<UseCase> findByIdAndRequirementId(UUID id, UUID requirementId);

}
