# --- !Ups

CREATE TABLE entry (
    entryId INT AUTO_INCREMENT,
    number INT NOT NULL,
    name VARCHAR(128) NOT NULL,
    chefName VARCHAR(128) NOT NULL,
    description VARCHAR(2048) NULL,
    PRIMARY KEY(entryId)
);

CREATE TABLE vote (
    voteId INT AUTO_INCREMENT,
    entryId INT NOT NULL,
    voterName VARCHAR(128) NULL,
    comment VARCHAR(2048) NULL,
    FOREIGN KEY(entryId) REFERENCES entry(entryId),
    PRIMARY KEY(voteId)
);

# --- !Downs

drop table vote;
drop table entry;
