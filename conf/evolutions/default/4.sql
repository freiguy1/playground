# --- !Ups

CREATE TABLE nextCompetitionInfo (
    nextCompetitionInfoId INT AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    details VARCHAR(2048) NULL,
    month VARCHAR(20) NOT NULL,
    PRIMARY KEY(nextCompetitionInfoId)
);

# --- !Downs

drop table nextCompetitionInfo;
