-- Staff accounts are separate from subscriber and mobile-app records.
CREATE TABLE staff_role (
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(30)  NOT NULL,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT uq_staff_role_code UNIQUE (code),
    CONSTRAINT ck_staff_role_code CHECK (code ~ '^[A-Z_]{3,30}$')
);
INSERT INTO staff_role (code, name) VALUES
    ('ADMIN', 'Administrator'), ('OPERATOR', 'Operator'), ('VIEWER', 'Viewer')
ON CONFLICT (code) DO NOTHING;

CREATE TABLE staff_user (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username           VARCHAR(50)  NOT NULL,
    password_hash      VARCHAR(100) NOT NULL,
    full_name          VARCHAR(150) NOT NULL,
    email              VARCHAR(150),
    enabled            BOOLEAN      NOT NULL DEFAULT TRUE,
    failed_login_count SMALLINT     NOT NULL DEFAULT 0 CHECK (failed_login_count >= 0),
    locked_until       TIMESTAMPTZ,
    last_login_at      TIMESTAMPTZ,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_staff_user_username UNIQUE (username),
    CONSTRAINT ck_staff_user_username CHECK (username = lower(btrim(username)) AND username ~ '^[a-z0-9._-]{3,50}$')
);
CREATE UNIQUE INDEX uq_staff_user_email ON staff_user (lower(email)) WHERE email IS NOT NULL;

CREATE TABLE staff_user_role (
    user_id BIGINT NOT NULL REFERENCES staff_user (id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES staff_role (id) ON DELETE RESTRICT,
    PRIMARY KEY (user_id, role_id)
);
CREATE INDEX idx_staff_user_role_role ON staff_user_role (role_id);
