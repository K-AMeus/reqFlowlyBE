--liquibase formatted sql
--changeset meus:001-create-project-table

CREATE TABLE project(
    id UUID PRIMARY KEY,
    user_id varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);



