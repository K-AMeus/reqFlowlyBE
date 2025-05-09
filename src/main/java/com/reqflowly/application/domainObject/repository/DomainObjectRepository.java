package com.reqflowly.application.domainObject.repository;

import com.reqflowly.application.domainObject.model.DomainObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DomainObjectRepository extends JpaRepository<DomainObject, UUID> {

    Optional<DomainObject> findByProjectIdAndId(UUID projectId, UUID id);

    List<DomainObject> findAllByProjectIdAndRequirementId(UUID projectId, UUID requirementId);

    void deleteByProjectIdAndId(UUID projectId, UUID id);

    @Query("SELECT DISTINCT d.requirementId FROM DomainObject d where d.projectId = :projectId")
    List<UUID> findDistinctRequirementIdsByProjectId(@Param("projectId") UUID projectId);
}
