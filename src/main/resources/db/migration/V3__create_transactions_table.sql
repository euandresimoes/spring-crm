CREATE TABLE organization.transactions (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    user_id UUID NOT NULL,

    description VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    type VARCHAR(20) NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_transactions_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization.organizations(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_transactions_organization_id
    ON organization.transactions (organization_id);

CREATE INDEX idx_transactions_user_id
    ON organization.transactions (user_id);

CREATE INDEX idx_transactions_type
    ON organization.transactions (type);
