CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS tb_user(
    id                      UUID DEFAULT gen_random_uuid(),
    cpf                     VARCHAR(11) UNIQUE NOT NULL,
    full_name               VARCHAR(256) NOT NULL,
    birth_date              DATE NOT NULL,
    phone                   VARCHAR(16),
    email                   VARCHAR(64),
    created_at              TIMESTAMP WITH TIME ZONE,
    updated_at              TIMESTAMP WITH TIME ZONE,
    deleted_at              TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id)
);