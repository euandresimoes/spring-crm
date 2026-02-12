CREATE SCHEMA IF NOT EXISTS organization;

CREATE TABLE organization.organizations (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(30) NOT NULL
);

CREATE TABLE organization.clients (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    user_id UUID NOT NULL,

    name VARCHAR(255) NOT NULL,
    description TEXT,
    email VARCHAR(255),
    cpf_cnpj VARCHAR(14),
    phone VARCHAR(50),

    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_clients_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization.organizations(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_clients_organization_id
    ON organization.clients (organization_id);

CREATE INDEX idx_clients_status
    ON organization.clients (status);
