package com.reqflowly.application.testCase.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String content;
    private UUID requirementId;
    private UUID useCaseId;
    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;

    public TestCase() {
    }

    public TestCase(UUID id, String name, String content, UUID requirementId, UUID useCaseId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.requirementId = requirementId;
        this.useCaseId = useCaseId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public UUID getRequirementId() {
        return requirementId;
    }

    public UUID getUseCaseId() {
        return useCaseId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRequirementId(UUID requirementId) {
        this.requirementId = requirementId;
    }

    public void setUseCaseId(UUID useCaseId) {
        this.useCaseId = useCaseId;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
