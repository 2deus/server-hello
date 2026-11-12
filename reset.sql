-- Back to the starting state: 3 rooms, 5 reservations, 3 accounts. Run inside roomdb (USE roomdb;).
DROP TABLE IF EXISTS app_user;
DROP TABLE IF EXISTS appointment;
DROP TABLE IF EXISTS demo;

CREATE TABLE demo (
    id        BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    capacity  INT          NOT NULL,
    CONSTRAINT chk_demo_capacity CHECK (capacity BETWEEN 1 AND 20)
);

CREATE TABLE appointment (
    id           BIGINT      AUTO_INCREMENT PRIMARY KEY,
    demo_id      BIGINT      NOT NULL,
    appointed_by  VARCHAR(50) NOT NULL,
    start_time   DATETIME    NOT NULL,
    end_time     DATETIME    NOT NULL,
    CONSTRAINT fk_appointment_demo FOREIGN KEY (demo_id) REFERENCES demo (id)
);

-- Week 10: accounts. password_hash holds a BCrypt hash (60 characters), never the password.
CREATE TABLE app_user (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(20)  NOT NULL,
    password_hash  VARCHAR(100) NOT NULL,
    role           VARCHAR(20)  NOT NULL,
    CONSTRAINT uq_app_user_username UNIQUE (username)
);

INSERT INTO demo (name, capacity) VALUES ('Seminar A', 8);
INSERT INTO demo (name, capacity) VALUES ('Study Pod', 4);
INSERT INTO demo (name, capacity) VALUES ('Rooftop Room', 12);

INSERT INTO appointment (demo_id, appointed_by, start_time, end_time) VALUES (1, 'Mina',  '2026-10-06 10:00:00', '2026-10-06 11:00:00');
INSERT INTO appointment (demo_id, appointed_by, start_time, end_time) VALUES (1, 'Omar',  '2026-10-06 13:00:00', '2026-10-06 15:00:00');
INSERT INTO appointment (demo_id, appointed_by, start_time, end_time) VALUES (2, 'Mina',  '2026-10-06 09:00:00', '2026-10-06 10:00:00');
INSERT INTO appointment (demo_id, appointed_by, start_time, end_time) VALUES (1, 'Lucas', '2026-10-07 10:00:00', '2026-10-07 12:00:00');
INSERT INTO appointment (demo_id, appointed_by, start_time, end_time) VALUES (2, 'Aiko',  '2026-10-07 14:00:00', '2026-10-07 15:00:00');

-- Week 10: three accounts. Passwords (for class only): mina1234, omar1234, admin1234
INSERT INTO app_user (username, password_hash, role) VALUES ('mina',  '$2a$10$ix/.xals1cT5fBy84xYGd./rBgU.YjsOFZ0hS649iIxeUqTiiOLzK', 'USER');
INSERT INTO app_user (username, password_hash, role) VALUES ('omar',  '$2a$10$50tuC8ry/VwJ7KOPT6EDb.1ncKve70rMAeSz9td9eL0Ow6UGeVUX6', 'USER');
INSERT INTO app_user (username, password_hash, role) VALUES ('admin', '$2a$10$8GIuHc2cp55HDtQXKnmAIeCe96sDjVCRtFkrfR2LbwFzvt5k507ze', 'ADMIN');
