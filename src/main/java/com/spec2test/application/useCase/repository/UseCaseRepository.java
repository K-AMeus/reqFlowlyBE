package com.spec2test.application.useCase.repository;

import com.spec2test.application.useCase.model.UseCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UseCaseRepository extends JpaRepository<UseCase, UUID> {

}
