-- liquibase formatted sql
-- changeset Andrey Antonov
-- comment: Created at 12:00 16.03.24.

SET search_path TO "user";

INSERT INTO gender(id, name)
VALUES (1, 'Мужчина'),
       (2, 'Женщина');

INSERT INTO "role" (name)
VALUES ( 'Сотрудник'),
       ( 'Администратор системы');

INSERT INTO "permission" (name, display_name)
VALUES ('PERMISSION_MANAGEMENT', 'Управление ролевой моделью'),
       ('PRODUCT_MANAGEMENT', 'Управление продутами'),
       ('ADMIN_MANAGEMENT', 'Полный доступ'),
       ('WORKING_NORM_MANAGEMENT', 'Управление рабочими нормами'),
       ('REPORT_MANAGEMENT', 'Управление отчетами');

INSERT INTO "permission_role" (permission_id, role_id)
VALUES (1, 2),
       (2, 2),
       (3, 2),
       (4, 2),
       (5, 2);