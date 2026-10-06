DROP TABLE IF EXISTS demo;
DROP TABLE IF EXISTS reservation;

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