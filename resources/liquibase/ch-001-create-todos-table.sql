-- liquibiase formatted sql

-- changeset shantanu:ch-001-create-todos-table

CREATE TABLE todos (
todo_id  CHAR(36) NOT NULL PRIMARY KEY,
content  VARCHAR(250) NOT NULL,
complete BOOLEAN NOT NULL DEFAULT false,
created  DATETIME NOT NULL DEFAULT NOW(),
updated  DATETIME NOT NULL DEFAULT NOW()
);

-- rollback DROP TABLE todos;
