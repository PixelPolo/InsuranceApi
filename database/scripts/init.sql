-- Module for gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Client
CREATE TABLE
    public.client (
        "client_id" UUID DEFAULT gen_random_uuid () PRIMARY KEY,
        "phone" VARCHAR(16),
        "email" VARCHAR(128) UNIQUE,
        "name" VARCHAR(64),
        -- Soft deletion for archives
        "is_deleted" BOOLEAN DEFAULT FALSE,
        "deletion_date" DATE
    );

-- Person is a specialization of a Client
CREATE TABLE
    public.person (
        "client_id" UUID PRIMARY KEY,
        "birthdate" DATE,
        -- The generalization primary key is the primary key of a specialization as a foreign key
        CONSTRAINT fk_person_client_id FOREIGN KEY ("client_id") REFERENCES public.client ("client_id")
        -- If the generalization is deleted, also its specialization
        ON DELETE CASCADE
        -- If the generalisation primary key is updated, also the specialization one
        ON UPDATE CASCADE
    );

-- Company is a specialization of a Client
CREATE TABLE
    public.company (
        "client_id" UUID PRIMARY KEY,
        "company_identifier" VARCHAR(32) UNIQUE,
        -- The generalization primary key is the primary key of a specialization as a foreign key
        CONSTRAINT fk_company_client_id FOREIGN KEY ("client_id") REFERENCES public.client ("client_id")
        -- If the generalization is deleted, also its specialization
        ON DELETE CASCADE
        -- If the generalisation primary key is updated, also the specialization one
        ON UPDATE CASCADE
    );

CREATE TABLE
    public.contract (
        "contract_id" UUID DEFAULT gen_random_uuid () PRIMARY KEY,
        "client_id" UUID NOT NULL,
        "start_date" TIMESTAMP NOT NULL, -- The backend will handle the default as current ISO 8601
        "end_date" TIMESTAMP,
        "update_date" TIMESTAMP NOT NULL,
        "cost_amount" NUMERIC(16, 4) NOT NULL, -- 16 digits, 4 decimals
        -- Many contract could be sign by one client
        CONSTRAINT fk_contract_client_id FOREIGN KEY ("client_id") REFERENCES public.client ("client_id")
        -- As we have a soft deletion on Client, we restrict the deletion
        ON DELETE RESTRICT ON UPDATE CASCADE
    );