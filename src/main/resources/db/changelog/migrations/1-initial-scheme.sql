-- liquibase formatted sql
-- changeset Andrey Antonov
-- comment: Created at 12:00 16.03.24.

CREATE SCHEMA IF NOT EXISTS "user";
SET search_path TO "user";

CREATE TABLE "user"
(
    id                 BIGSERIAL PRIMARY KEY,
    username           VARCHAR(50) NOT NULL UNIQUE,
    email              VARCHAR(50) NOT NULL UNIQUE,
    last_name          VARCHAR(50) NOT NULL,
    first_name         VARCHAR(50) NOT NULL,
    patronymic         VARCHAR(50),
    birthday           DATE,
    active             BOOLEAN,
    date_work_from     DATE,
    date_work_to       DATE,
    date_probation_end DATE,
    position_name      VARCHAR(50),
    city               VARCHAR(50),
    address            VARCHAR(100),
    gender_id          BIGINT,
    role_id            BIGINT      NOT NULL DEFAULT 1,
    profile_picture_id BIGINT
);

CREATE TABLE gender
(
    id   BIGINT PRIMARY KEY,
    name VARCHAR(10) UNIQUE NOT NULL
);

ALTER TABLE "user"
    ADD FOREIGN KEY (gender_id) REFERENCES "gender" (id);

CREATE TABLE "profile_picture"
(
    id         BIGSERIAL PRIMARY KEY,
    active     BOOLEAN   NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    user_id    BIGINT REFERENCES "user" (id)
);

ALTER TABLE "user"
    ADD FOREIGN KEY (profile_picture_id) REFERENCES "profile_picture" (id);


CREATE TABLE "role"
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) UNIQUE NOT NULL
);

ALTER TABLE "user"
    ADD FOREIGN KEY (role_id) REFERENCES "role" (id);

CREATE TABLE "permission"
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(200) UNIQUE NOT NULL,
    display_name VARCHAR(400) UNIQUE NOT NULL
);

CREATE TABLE "permission_role"
(
    id            BIGSERIAL PRIMARY KEY,
    permission_id BIGINT NOT NULL REFERENCES "permission" (id),
    role_id       BIGINT NOT NULL REFERENCES "role" (id)
);
