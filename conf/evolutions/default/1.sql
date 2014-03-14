# --- !Ups

CREATE TABLE user (
	userId INT AUTO_INCREMENT,
	email VARCHAR(128) NOT NULL,
	PRIMARY KEY(userId));
	
CREATE TABLE passwordInfo (
	passwordInfoId INT AUTO_INCREMENT,
	hasher VARCHAR(128) NOT NULL,
	password VARCHAR(128) NOT NULL,
	salt VARCHAR(128),
	PRIMARY KEY(passwordInfoId));
	
CREATE TABLE oAuth1Info(
	oAuth1InfoId INT AUTO_INCREMENT,
	token VARCHAR(512) NOT NULL,
	secret VARCHAR(512) NOT NULL,
	PRIMARY KEY(oAuth1InfoId));
	
CREATE TABLE oAuth2Info(
	oAuth2InfoId INT AUTO_INCREMENT,
	accessToken VARCHAR(512) NOT NULL,
	tokenType VARCHAR(128),
	expiresIn INT,
	refreshToken VARCHAR(128),
	PRIMARY KEY(oAuth2InfoId));
	
CREATE TABLE identity (
	providerId VARCHAR(64),
	userSSId VARCHAR(64),
	userId INT NOT NULL,
	firstName VARCHAR(64) NOT NULL,
	lastName VARCHAR(64) NOT NULL,
	fullName VARCHAR(128) NOT NULL,
	email VARCHAR(128),
	avatarUrl VARCHAR(128),
	oAuth1InfoId INT,
	oAuth2InfoId INT,
	passwordInfoId INT,
	authenticationMethod VARCHAR(64),
	PRIMARY KEY(providerId, userSSId),
	FOREIGN KEY(userId) REFERENCES user(userId),
	FOREIGN KEY(oAuth1InfoId) REFERENCES oAuth1Info(oAuth1InfoId),
	FOREIGN KEY(oAuth2InfoId) REFERENCES oAuth2Info(oAuth2InfoId),
	FOREIGN KEY(passwordInfoId) REFERENCES passwordInfo(passwordInfoId));

	
CREATE TABLE token (
	tokenId INT AUTO_INCREMENT,
	uuid VARCHAR(64) NOT NULL,
	email VARCHAR(128) NOT NULL,
	creationTime TIMESTAMP NOT NULL,
	expirationTime TIMESTAMP NOT NULL,
	isSignUp BOOLEAN NOT NULL,
	PRIMARY KEY(tokenId));
  
  # --- !Downs
  
  DROP TABLE token;
  DROP TABLE identity;
  DROP TABLE user;
  DROP TABLE passwordInfo;
  DROP TABLE oAuth1Info;
  DROP TABLE oAuth2Info;
  
  
