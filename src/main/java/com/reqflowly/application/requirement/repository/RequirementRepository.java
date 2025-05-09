package com.reqflowly.application.requirement.repository;

import com.reqflowly.application.requirement.model.Requirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, UUID> {

    Optional<Requirement> findByProjectIdAndId(UUID projectId, UUID id);

    Page<Requirement> findAllByProjectIdOrderByUpdatedAtDesc(UUID projectId, Pageable pageable);

    void deleteByProjectIdAndId(UUID projectId, UUID id);

    Page<Requirement> findAllByProjectIdAndIdInOrderByUpdatedAtDesc(UUID projectId, List<UUID> requirementIds, Pageable pageable);

}
