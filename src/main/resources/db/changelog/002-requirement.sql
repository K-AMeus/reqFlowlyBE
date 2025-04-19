--liquibase formatted sql
--changeset meus:002-create-requirement-table

CREATE TABLE requirement(
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    title varchar(255),
    description TEXT,
    source_type varchar(255),
    source_content TEXT,
    source_file_url varchar(255),
    FOREIGN KEY (project_id) REFERENCES project(id)
);

--changeset meus:add-createdAt-and-updatedAt-to-requirement
ALTER TABLE requirement ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE requirement ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;