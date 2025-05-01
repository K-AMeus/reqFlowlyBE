package com.spec2test.application.domainObject.repository;

import com.spec2test.application.domainObject.model.DomainObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DomainObjectRepository extends JpaRepository<DomainObject, UUID> {

    Optional<DomainObject> findByProjectIdAndId(UUID projectId, UUID id);

    Page<DomainObject> findAllByProjectIdAndRequirementId(UUID projectId, UUID requirementId, Pageable pageable);

    List<DomainObject> findAllByProjectId(UUID projectId);

    void deleteByProjectIdAndId(UUID projectId, UUID id);
}
