-- Same as V9999__sample-data.sql
-- Populate the database with consistent sample data for testing
--
-- Clean existing data
DELETE FROM contract;

DELETE FROM person;

DELETE FROM company;

DELETE FROM client;

-- Ensure pgcrypto extension is available
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Insert sample clients
INSERT INTO
    public.client (client_id, phone, email, name)
VALUES
    (
        gen_random_uuid (),
        '+41791234567',
        'alice@example.com',
        'Alice Dupont'
    ),
    (
        gen_random_uuid (),
        '+41761239876',
        'bob@example.com',
        'Bob Martin'
    ),
    (
        gen_random_uuid (),
        '+41442223344',
        'entreprise@example.com',
        'Entreprise SA'
    );

-- Link Alice and Bob as persons
INSERT INTO
    public.person (client_id, birthdate)
SELECT
    c."client_id",
    DATE '1990-05-15'
FROM
    public.client c
WHERE
    c."email" = 'alice@example.com';

INSERT INTO
    public.person (client_id, birthdate)
SELECT
    c."client_id",
    DATE '1985-09-10'
FROM
    public.client c
WHERE
    c."email" = 'bob@example.com';

-- Link Entreprise SA as a company
INSERT INTO
    public.company (client_id, company_identifier)
SELECT
    c."client_id",
    'CH-123.456.789'
FROM
    public.client c
WHERE
    c."email" = 'entreprise@example.com';

-- Insert contracts for each client
INSERT INTO
    public.contract (client_id, start_date, update_date, cost_amount)
SELECT
    c."client_id",
    TIMESTAMP '2024-01-15T12:00:00',
    TIMESTAMP '2024-01-15T12:00:00',
    400.00
FROM
    public.client c
WHERE
    c."email" = 'alice@example.com';

INSERT INTO
    public.contract (
        client_id,
        start_date,
        end_date,
        update_date,
        cost_amount
    )
SELECT
    c."client_id",
    TIMESTAMP '2023-12-01T12:00:00',
    TIMESTAMP '2025-12-01T12:00:00',
    TIMESTAMP '2025-12-01T12:00:00',
    300.00
FROM
    public.client c
WHERE
    c."email" = 'bob@example.com';

INSERT INTO
    public.contract (client_id, start_date, update_date, cost_amount)
SELECT
    c."client_id",
    TIMESTAMP '2024-03-05T12:00:00',
    TIMESTAMP '2024-03-05T12:00:00',
    1000.00
FROM
    public.client c
WHERE
    c."email" = 'entreprise@example.com';