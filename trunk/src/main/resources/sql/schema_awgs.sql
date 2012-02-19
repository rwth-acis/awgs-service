DROP TABLE IF EXISTS AUTHORS;
DROP TABLE IF EXISTS USER;
DROP TABLE IF EXISTS ITEM;

CREATE TABLE USER (
	JID char(255) NOT NULL,
	PASS char(255) NOT NULL,
	NAME char(255),
	EMAIL char(255),
	CONSTRAINT USER_PK PRIMARY KEY (JID)
); 

CREATE TABLE ITEM (
  ID char(255) NOT NULL,
  NAME char(255) NOT NULL,
  DESCRIPTION text NOT NULL,
  URL char(255) NOT NULL,
  STATUS int(5) NOT NULL,
  TIME timestamp NOT NULL,
  CONSTRAINT ITEM_PK PRIMARY KEY  (ID),
  CONSTRAINT ITEM_UK UNIQUE KEY (URL)
);

CREATE TABLE AUTHORS (
	ID int(11) NOT NULL auto_increment,
	USER char(255) NOT NULL,
	ITEM char(255) NOT NULL,
	TIME timestamp NOT NULL,
	CONSTRAINT AUTHORS_PK PRIMARY KEY (ID),
	CONSTRAINT AUTHORS_UK UNIQUE KEY (USER,ITEM,TIME),
	CONSTRAINT AUTHORS_USER_FK FOREIGN KEY (USER) REFERENCES USER (JID),
	CONSTRAINT AUTHORS_ITEM_FK FOREIGN KEY (ITEM) REFERENCES ITEM (ID)
);

INSERT INTO USER VALUES ('foo@role.dbis.rwth-aachen.de','bar','Foo Bar','foo@bar.org');

INSERT INTO ITEM VALUES ('AWGS-2012-003','Analysis & Visualization of Real-time Social Interactions with MobSOSX','Social interactions on the Web increasingly become real-time and thus enable communication patterns beyond the traditional request-response still predominant in todays Web services. Although many proprietary or customized real-time protocols exist,the open standard XMPP protocol has gained significant traction for social Web applications over the last few years. XMPP protocol transcripts are thus rich information sources for the analysis of social networks and interactions under one single umbrella. In this paper we present MobSOSX, a model and homonymous software system for recording XMPP transcripts and present MobSOS-QV as a toolkit for analyzing, visualizing, and sharing information on different aspects of social interactions of individuals or even whole communities across a federated network of institutions.','http://merkur.informatik.rwth-aachen.de/bscw/bscw.cgi/d3216546/ReKl11.pdf',0,NOW());
INSERT INTO ITEM VALUES ('AWGS-2012-004','Todays top RESTful Web services and why they are not RESTful','Since Fieldings seminal contribution on the REST architecture style in 2000, the so-called class of RESTful services has taken off to challenge whole classes of previously existing Web services. Several books have since then emerged, providing a set of valuable guidelines and design principles for the development of truly RESTful services. However, todays most popular RESTful services adopt only very few of these guidelines. In this paper we present an in-depth analysis of the top 25 RESTful services listed on programmableweb.com against 17 RESTful design principles found in literature. Results provide evidence that hardly any of the services claiming to be RESTful is truly RESTful.','http://merkur.informatik.rwth-aachen.de/bscw/bscw.cgi/d3315589/SRKl12.pdf',1,NOW());

INSERT INTO AUTHORS VALUES (1,'foo@role.dbis.rwth-aachen.de','AWGS-2012-003',NOW());
INSERT INTO AUTHORS VALUES (2,'foo@role.dbis.rwth-aachen.de','AWGS-2012-004',NOW());

