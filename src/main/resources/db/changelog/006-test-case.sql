--liquibase formatted sql
--changeset meus:006-create-test-case-table

CREATE TABLE test_case(
    id UUID PRIMARY KEY,
    name varchar(255),
    content TEXT,
    requirement_id UUID NOT NULL,
    use_case_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (requirement_id) REFERENCES requirement(id),
    FOREIGN KEY (use_case_id) REFERENCES use_case(id)
);