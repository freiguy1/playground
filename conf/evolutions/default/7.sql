# --- !Ups

CREATE TABLE vegasTeam (
    vegasTeamId INT AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    captainName VARCHAR(64) NOT NULL,
    miles FLOAT NOT NULL,
    PRIMARY KEY(vegasTeamId)
);

# --- !Downs

DROP TABLE vegasTeam;
