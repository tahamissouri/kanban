-- V1__init.sql
CREATE TABLE users (
                       id         BIGSERIAL    PRIMARY KEY,
                       user_name  VARCHAR(255) NOT NULL UNIQUE,
                       email      VARCHAR(255) NOT NULL UNIQUE,
                       password   VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP    NOT NULL
);

CREATE TABLE board (
                       id         BIGSERIAL PRIMARY KEY,
                       name       VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP    NOT NULL,
                       owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);


CREATE TABLE columnn (
                         id       BIGSERIAL PRIMARY KEY,
                         name     VARCHAR(255) NOT NULL,
                         position INTEGER      NOT NULL,
                         board_id BIGINT       NOT NULL,
                         CONSTRAINT fk_column_board
                             FOREIGN KEY (board_id) REFERENCES board(id)
                                 ON DELETE CASCADE
);

CREATE TABLE card (
                      id          BIGSERIAL PRIMARY KEY,
                      title       VARCHAR(255) NOT NULL,
                      description TEXT,
                      position    INTEGER,
                      column_id   BIGINT NOT NULL,
                      CONSTRAINT fk_card_column
                          FOREIGN KEY (column_id) REFERENCES columnn(id)
                              ON DELETE CASCADE
);
CREATE TABLE board_members (
                               id_board  BIGINT NOT NULL REFERENCES board(id)  ON DELETE CASCADE,
                               id_member BIGINT NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
                               PRIMARY KEY (id_board, id_member)
);

CREATE INDEX idx_column_board_id ON columnn(board_id);
CREATE INDEX idx_card_column_id  ON card(column_id);
CREATE INDEX idx_board_owner    ON board(owner_id);
CREATE INDEX idx_members_board  ON board_members(id_board);
CREATE INDEX idx_members_user   ON board_members(id_member);