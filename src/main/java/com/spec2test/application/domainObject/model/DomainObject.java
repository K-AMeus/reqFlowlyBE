package com.spec2test.application.domainObject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
public class DomainObject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;

    private UUID projectId;
    private UUID requirementId;
    private String name;
    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;

    public DomainObject() {
    }

    public DomainObject(UUID id, UUID projectId, UUID requirementId, String name, Instant createdAt, Instant updatedAt) {
        Id = id;
        this.projectId = projectId;
        this.requirementId = requirementId;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return Id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getRequirementId() {
        return requirementId;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public void setRequirementId(UUID requirementId) {
        this.requirementId = requirementId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}