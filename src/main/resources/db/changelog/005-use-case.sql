--liquibase formatted sql
--changeset meus:005-create-use-case-table

CREATE TABLE use_case(
    id UUID PRIMARY KEY,
    name VARCHAR NOT NULL,
    content TEXT NOT NULL,
    requirement_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (requirement_id) REFERENCES requirement(id)
);