package com.spec2test.application.project.repository;

import com.spec2test.application.project.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Optional<Project> findByUserIdAndId(String userId, UUID id);
    Page<Project> findAllByUserId(String userId, Pageable pageable);
    void deleteByUserIdAndId(String userId, UUID id);
}
