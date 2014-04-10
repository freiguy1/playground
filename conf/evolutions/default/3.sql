# --- !Ups

CREATE TABLE team (
    teamId INT AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    PRIMARY KEY(teamId)
);

CREATE TABLE competition (
    competitionId INT AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    instructions VARCHAR(1024) NOT NULL,
    PRIMARY KEY(competitionId)
);

CREATE TABLE teamCompetitionResult (
    teamId INT,
    competitionId INT,
    pointsEarned INT NOT NULL,
    notes VARCHAR(32) NULL,
    PRIMARY KEY(teamId, competitionId),
    FOREIGN KEY(teamId) REFERENCES team(teamId),
    FOREIGN KEY(competitionId) REFERENCES competition(competitionId)
);

# --- !Downs

drop table teamCompetitionResult;
drop table competition;
drop table team; 
