CREATE TABLE appointment
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    room_id    BIGINT NOT NULL,
    owner      VARCHAR(255) NULL,
    start_time datetime(6)           NULL,
    end_time   datetime(6)           NULL,
    CONSTRAINT pk_appointment PRIMARY KEY (id)
);

CREATE TABLE demo
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    name     VARCHAR(255) NULL,
    capacity INT NOT NULL,
    CONSTRAINT pk_demo PRIMARY KEY (id)
);

CREATE TABLE revchanges
(
    rev        BIGINT NOT NULL,
    entityname VARCHAR(255) NULL
);

CREATE TABLE revinfo
(
    rev      BIGINT NOT NULL,
    revtstmp BIGINT NULL,
    CONSTRAINT pk_revinfo PRIMARY KEY (rev)
);

ALTER TABLE appointment
    ADD CONSTRAINT FK_APPOINTMENT_ON_ROOM FOREIGN KEY (room_id) REFERENCES demo (id);

ALTER TABLE revchanges
    ADD CONSTRAINT fk_revchanges_on_default_tracking_modified_entities_changelog FOREIGN KEY (rev) REFERENCES revinfo (rev);
ALTER TABLE reservation RENAME appointment;

CREATE TABLE revchanges
(
    rev        BIGINT       NOT NULL,
    entityname VARCHAR(255) NULL
);

CREATE TABLE revinfo
(
    rev      BIGINT NOT NULL,
    revtstmp BIGINT NULL,
    CONSTRAINT pk_revinfo PRIMARY KEY (rev)
);

ALTER TABLE revchanges
    ADD CONSTRAINT fk_revchanges_on_default_tracking_modified_entities_changelog FOREIGN KEY (rev) REFERENCES revinfo (rev);

ALTER TABLE demo
    MODIFY name VARCHAR(255);

ALTER TABLE demo
    MODIFY name VARCHAR(255) NULL;