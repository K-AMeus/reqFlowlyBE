--liquibase formatted sql
--changeset meus:004-create-domain-object-attribute-table

CREATE TABLE domain_object_attribute(
    id UUID PRIMARY KEY,
    domain_object_id UUID NOT NULL,
    name varchar(255) NOT NULL,
    data_type varchar(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (domain_object_id) REFERENCES domain_object(id)
);