DROP TABLE IF EXISTS USER;
DROP TABLE IF EXISTS MEDIUM;
DROP TABLE IF EXISTS ACHIEVEMENT;
DROP TABLE IF EXISTS RATES;
DROP TABLE IF EXISTS ACHIEVES;

CREATE TABLE USER (
	LOGIN char(255) NOT NULL,
	NAME char(255) NOT NULL,
	PASS char(255) NOT NULL,
	XP int(11) NOT NULL default 0,
	CONSTRAINT USER_PK PRIMARY KEY (LOGIN),
	CONSTRAINT USER_XP_C CHECK (XP >= 0)
); 

CREATE TABLE MEDIUM (
  ID int(11) NOT NULL auto_increment,
  URL char(255) NOT NULL,
  DESCRIPTION text NOT NULL,
  CONSTRAINT MEDIUM_PK PRIMARY KEY  (ID),
  CONSTRAINT MEDIUM_UK UNIQUE KEY (URL)
);

CREATE TABLE ACHIEVEMENT (
	ID int(11) NOT NULL auto_increment,
	NAME char(255) NOT NULL,
	IMGURL char(255) NOT NULL,
	CONSTRAINT ACHIEVEMENT_PK PRIMARY KEY (ID),
	CONSTRAINT ACHIEVEMENT_UK UNIQUE KEY (NAME)
);

CREATE TABLE RATES (
	ID int(11) NOT NULL auto_increment,
	USER char(255) NOT NULL,
	MEDIUM int(11) NOT NULL,
	RATING int(1) NOT NULL,
	TIME timestamp NOT NULL,
	CONSTRAINT RATES_PK PRIMARY KEY (ID),
	CONSTRAINT RATES_UK UNIQUE KEY (USER,MEDIUM,TIME),
	CONSTRAINT RUSER_FK FOREIGN KEY (USER) REFERENCES USER (ID),
	CONSTRAINT MEDIUM_FK FOREIGN KEY (MEDIUM) REFERENCES MEDIUM (ID),
	CONSTRAINT RATES_RATING_C CHECK (RATING >= 0 AND RATING <=1)
); 

CREATE TABLE ACHIEVES (
	ID int(11) NOT NULL auto_increment,
	USER char(255) NOT NULL,
	ACHIEVEMENT int(11) NOT NULL,
	TIME timestamp NOT NULL,
	CONSTRAINT ACHIEVES_PK PRIMARY KEY (ID),
	CONSTRAINT ACHIEVES_UK UNIQUE KEY (USER,ACHIEVEMENT,TIME),
	CONSTRAINT AUSER_FK FOREIGN KEY (USER) REFERENCES USER (ID),
	CONSTRAINT ACHIEVEMENT_FK FOREIGN KEY (ACHIEVEMENT) REFERENCES ACHIEVEMENT (ID)
); 

INSERT INTO USER VALUES ('renzel','Dominik Renzel','aaaaa',100);
INSERT INTO USER VALUES ('klamma','Ralf Klamma','00000',1000);
INSERT INTO USER VALUES ('pevil','President Evil','aaaaa',50);

INSERT INTO MEDIUM VALUES (1,'http://static.prisonplanet.com/p/images/may2011/020511top.jpg','Der Osama bin Liggedi');
INSERT INTO MEDIUM VALUES (2,'http://udssr.net/wp-content/uploads/2010/03/wolfram-goldbarren-faelschung_01_hq.jpg','Jefaelschte Joltbarren');

INSERT INTO ACHIEVEMENT VALUES (1,'Henkerorden','http://1.bp.blogspot.com/-VYNG6hCyGV8/Tghv_YMhTsI/AAAAAAAAAjY/iSE20voVSK0/s400/henker.jpg');
INSERT INTO ACHIEVEMENT VALUES (2,'Sauforden','http://www.verschoett.de/assets/images/autogen/a_502.jpg');
INSERT INTO ACHIEVEMENT VALUES (3,'Hundeorden','http://bild0.qimage.de/hundeverein-1-freizeithundeverein-foto-bild-36349790.270.jpg');

INSERT INTO RATES VALUES (1,'renzel',1,1,NOW());
INSERT INTO RATES VALUES (2,'renzel',2,1,NOW());
INSERT INTO RATES VALUES (3,'klamma',1,1,NOW());
INSERT INTO RATES VALUES (4,'klamma',2,0,NOW());
INSERT INTO RATES VALUES (5,'pevil',1,1,NOW());
INSERT INTO RATES VALUES (6,'pevil',2,0,NOW());

INSERT INTO ACHIEVES VALUES (1,'renzel',1,NOW());
INSERT INTO ACHIEVES VALUES (2,'renzel',2,NOW());
INSERT INTO ACHIEVES VALUES (3,'klamma',2,NOW());
INSERT INTO ACHIEVES VALUES (4,'klamma',3,NOW());
INSERT INTO ACHIEVES VALUES (5,'pevil',1,NOW());
INSERT INTO ACHIEVES VALUES (6,'pevil',2,NOW());


