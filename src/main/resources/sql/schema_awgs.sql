DROP TABLE IF EXISTS ROOM;
DROP TABLE IF EXISTS ITEM;
DROP TABLE IF EXISTS INSULT;

CREATE TABLE ITEM (
  ID char(255) NOT NULL,
  NAME char(255) NOT NULL,
  DESCRIPTION text NOT NULL,
  URL char(255) NOT NULL,
  STATUS int(5) NOT NULL,
  OWNER char(255) NOT NULL,
  LASTUPDATE timestamp NOT NULL,
  CONSTRAINT ITEM_PK PRIMARY KEY  (ID),
  CONSTRAINT ITEM_UK UNIQUE KEY (URL)
);

CREATE TABLE ROOM (
	ID int(11) NOT NULL auto_increment,
	USER char(255) NOT NULL,
	ROOM char(255) NOT NULL,
	NICK char(255) NOT NULL,
	CONSTRAINT ROOM_PK PRIMARY KEY (ID),
	CONSTRAINT ROOM_UK UNIQUE KEY (ROOM)
);

CREATE TABLE INSULT (
	ID int(11) NOT NULL auto_increment,
	INSULT char(255) NOT NULL,
	CONTRIBUTOR char(255) NOT NULL,
	DATE timestamp NOT NULL,
	CONSTRAINT INSULT_PK PRIMARY KEY (ID)
);

INSERT INTO ITEM VALUES ('AWGS-2012-003','Analysis & Visualization of Real-time Social Interactions with MobSOSX','Social interactions on the Web increasingly become real-time and thus enable communication patterns beyond the traditional request-response still predominant in todays Web services. Although many proprietary or customized real-time protocols exist,the open standard XMPP protocol has gained significant traction for social Web applications over the last few years. XMPP protocol transcripts are thus rich information sources for the analysis of social networks and interactions under one single umbrella. In this paper we present MobSOSX, a model and homonymous software system for recording XMPP transcripts and present MobSOS-QV as a toolkit for analyzing, visualizing, and sharing information on different aspects of social interactions of individuals or even whole communities across a federated network of institutions.','http://merkur.informatik.rwth-aachen.de/bscw/bscw.cgi/d3216546/ReKl11.pdf',0,'renzel@role.dbis.rwth-aachen.de',NOW());
INSERT INTO ITEM VALUES ('AWGS-2012-004','Todays top RESTful Web services and why they are not RESTful','Since Fieldings seminal contribution on the REST architecture style in 2000, the so-called class of RESTful services has taken off to challenge whole classes of previously existing Web services. Several books have since then emerged, providing a set of valuable guidelines and design principles for the development of truly RESTful services. However, todays most popular RESTful services adopt only very few of these guidelines. In this paper we present an in-depth analysis of the top 25 RESTful services listed on programmableweb.com against 17 RESTful design principles found in literature. Results provide evidence that hardly any of the services claiming to be RESTful is truly RESTful.','http://merkur.informatik.rwth-aachen.de/bscw/bscw.cgi/d3315589/SRKl12.pdf',1,'renzel@role.dbis.rwth-aachen.de',NOW());

INSERT INTO ROOM VALUES (1, 'renzel@role.dbis.rwth-aachen.de','awgs-test@muc.role.dbis.rwth-aachen.de','awgs-bot');
INSERT INTO ROOM VALUES (2, 'renzel@role.dbis.rwth-aachen.de','awgs-test2@muc.role.dbis.rwth-aachen.de','awgs-bot');

INSERT INTO INSULT VALUES (1, 'Your mother stinks while shitting!','renzel@role.dbis.rwth-aachen.de', NOW());
INSERT INTO INSULT VALUES (2, 'Drop dead, sucker!', 'renzel@role.dbis.rwth-aachen.de', NOW());
INSERT INTO INSULT VALUES (3, 'I guess your parents should reconsider post-natal abortion...', 'renzel@role.dbis.rwth-aachen.de', NOW());
INSERT INTO INSULT VALUES (4, 'I ever thought brainless monkeys were extinct, but I was wrong...', 'renzel@role.dbis.rwth-aachen.de', NOW());
INSERT INTO INSULT VALUES (5, 'Go fuck yourself!', 'renzel@role.dbis.rwth-aachen.de', NOW());
INSERT INTO INSULT VALUES (6, 'Bread can decay. What can you do?', 'renzel@role.dbis.rwth-aachen.de', NOW());
INSERT INTO INSULT VALUES (7, 'You ever wanted to see me fuck your mother? Then run home quickly...', 'renzel@role.dbis.rwth-aachen.de', NOW());
INSERT INTO INSULT VALUES (8, 'What is your problem, loser?', 'renzel@role.dbis.rwth-aachen.de', NOW());
INSERT INTO INSULT VALUES (9, 'Did you ever think about killing yourself? Now would be a good time...', 'renzel@role.dbis.rwth-aachen.de', NOW());
