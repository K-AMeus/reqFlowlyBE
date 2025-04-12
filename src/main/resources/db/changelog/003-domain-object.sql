--liquibase formatted sql
--changeset meus:003-create-domain-object-table

CREATE TABLE domain_object(
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    name varchar(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES project(id)
);