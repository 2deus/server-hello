DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS demo;

CREATE TABLE demo (
                      id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
                      name            VARCHAR(100)    NOT NULL,
                      capacity        INT             NOT NULL,
                      CONSTRAINT chk_demo_cap
                          CHECK (capacity BETWEEN 1 AND 20)
);

CREATE TABLE reservation (
                             id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
                             demo_id         BIGINT          NOT NULL,
                             reserved_by     VARCHAR(50)     NOT NULL,
                             start_time      TIMESTAMP       NOT NULL,
                             end_time        TIMESTAMP       NOT NULL,
                             CONSTRAINT fk_reservation_demo
                                 FOREIGN KEY (demo_id) REFERENCES demo(id)
);

INSERT INTO demo (name, capacity) VALUES ('Seminar A', 8);
INSERT INTO demo (name, capacity) VALUES ('Study Pod', 4);
INSERT INTO demo (name, capacity) VALUES ('Rooftop Room', 12);

INSERT INTO reservation (demo_id, reserved_by, start_time, end_time)
VALUES (1, 'Mina',  '2026-10-06 10:00:00', '2026-10-06 11:00:00');
INSERT INTO reservation (demo_id, reserved_by, start_time, end_time)
VALUES (1, 'Omar',  '2026-10-06 13:00:00', '2026-10-06 15:00:00');
INSERT INTO reservation (demo_id, reserved_by, start_time, end_time)
VALUES (2, 'Mina',  '2026-10-06 09:00:00', '2026-10-06 10:00:00');
INSERT INTO reservation (demo_id, reserved_by, start_time, end_time)
VALUES (1, 'Lucas', '2026-10-07 10:00:00', '2026-10-07 12:00:00');
INSERT INTO reservation (demo_id, reserved_by, start_time, end_time)
VALUES (2, 'Aiko',  '2026-10-07 14:00:00', '2026-10-07 15:00:00');
