# --- !Ups

CREATE TABLE transaction (
    transactionId INT AUTO_INCREMENT,
    userId INT NOT NULL,
    amount DOUBLE NOT NULL,
    time TIMESTAMP NOT NULL,
    note VARCHAR(128) NULL,
    PRIMARY KEY(transactionId),
    FOREIGN KEY(userId) REFERENCES user(userId)
);

CREATE TABLE balance (
    balanceId INT AUTO_INCREMENT,
    userId INT NOT NULL,
    amount DOUBLE NOT NULL,
    PRIMARY KEY(balanceId),
    FOREIGN KEY(userId) REFERENCES user(userId)
);

# --- !Downs

drop table balance;
drop table transaction;
