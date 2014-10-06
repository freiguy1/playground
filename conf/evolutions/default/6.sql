# --- !Ups

CREATE TABLE authenticator (
    authenticatorId VARCHAR(256) UNIQUE NOT NULL,
    userId VARCHAR(64) NOT NULL,
    providerId VARCHAR(64) NOT NULL,
    creationDate TIMESTAMP NOT NULL,
    lastUsed TIMESTAMP NOT NULL,
    expirationDate TIMESTAMP NOT NULL
);

# --- !Downs

DROP TABLE authenticator;