
# --- !Ups

CREATE TABLE system (
    systemId BIGINT AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    PRIMARY KEY(systemId)
);

CREATE TABLE belt(
    beltId BIGINT AUTO_INCREMENT,
    systemId BIGINT NOT NULL,
    planetNum VARCHAR(12) NOT NULL,
    beltNum VARCHAR(12) NOT NULL,
    hasRats BOOLEAN,
    lastChecked TIMESTAMP NULL,
    lastStatusChanged TIMESTAMP NULL,
    PRIMARY KEY(beltID),
    FOREIGN KEY(systemId) REFERENCES system(systemId)
);

INSERT INTO system (name) VALUES ('Barleguet');
INSERT INTO system (name) VALUES ('Ausmaert');
INSERT INTO system (name) VALUES ('Kenninck');
INSERT INTO system (name) VALUES ('Aulbres');
INSERT INTO system (name) VALUES ('Vestouve');
INSERT INTO system (name) VALUES ('Espigoure');

-- Barleguet Belts
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (1, 'II', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (1, 'V', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (1, 'VI', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (1, 'VII', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (1, 'VII', '2');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (1, 'VII', '3');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (1, 'VII', '4');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (1, 'VII', '5');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (1, 'VII', '6');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (1, 'VII', '7');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (1, 'VII', '8');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (1, 'VII', '9');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (1, 'VII', '10');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (1, 'VII', '11');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (1, 'VII', '12');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (1, 'VII', '13');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (1, 'IX', '1');

-- Ausmaert Belts
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (2, 'I', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (2, 'III', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (2, 'V', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (2, 'VII', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (2, 'VII', '2');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (2, 'VII', '3');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (2, 'VIII', '1');

-- Kenninck Belts
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (3, 'VIII', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (3, 'VIII', '2');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (3, 'VIII', '3');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (3, 'VIII', '4');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (3, 'IX', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (3, 'IX', '2');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (3, 'IX', '3');

-- Aulbres Belts
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'III', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'IV', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'VI', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'VI', '2');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'VII', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'VII', '2');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'VII', '3');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'VIII', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'VIII', '2');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'VIII', '3');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'VIII', '4');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'VIII', '5');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'VIII', '6');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'VIII', '7');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'VIII', '8');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'VIII', '9');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'IX', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'IX', '2');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'IX', '3');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'IX', '4');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'IX', '5');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'IX', '6');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'IX', '7');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'X', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'X', '2');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'XII', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (4, 'XII', '2');

-- Vestouve Belts
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (5, 'II', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (5, 'VI', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (5, 'IX', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (5, 'IX', '2');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (5, 'IX', '3');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (5, 'IX', '4');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (5, 'IX', '5');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (5, 'IX', '6');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (5, 'IX', '7');

-- Espigoure Belts
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (6, 'IV', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (6, 'V', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (6, 'VI', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (6, 'VII', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (6, 'VIII', '1');
INSERT INTO belt (systemId, planetNum, beltNum) VALUES (6, 'VIII', '2');



# --- !Downs

DROP TABLE belt;
DROP TABLE system;
