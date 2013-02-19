ACIS WORKING GROUP SERIES SERVICE - README
==========================================
A service for managing the ACIS Working Group Series, a series of working papers created by members of the Advanced Community Information Systems (ACIS) group at Chair of Computer Science 5 - Databases & Information Systems, RWTH Aachen University, Germany.

This README includes documentation on the following tasks:

	1) starting a development server
	2) packaging a deployable version

IMPORTANT NOTE: both tasks require Apache Maven 2.x as main build tool. Not all tasks will work with Apache Maven 3.x!
	
1) Starting a development server

1.1) Prepare a mysql database using the predefined AWGS schema SQL script:
	
	mysql -u <awgs-service-user> -p < ./src/main/resources/sql/schema_awgs.sql
	
1.2) Start a development server, using the following commandline:

	mvn clean jetty:run -D

There are several configuration options available:

        -Djetty.port=<NUM> : specify the port on which the Web service is reachable. Default value is 8080.
        
        -Dxmpp.host=<HOSTNAME> : specify the hostname of the XMPP server on which the AWGS Bot should listen. If no value is set for this
 parameter, the service will omit starting the AWGS Bot.
        -Dxmpp.port=<PORT> : specify the port of the XMPP server on which the AWGS Bot should listen. Default value is 5222 (XMPP standar
d port for client-server connections).
        -Dxmpp.user=<LOGIN> : specify the login of the XMPP user representing the AWGS Bot. Such a user must already exist. The service w
ill not create such a user on-the-fly. Default value is "awgs-bot".
        -Dxmpp.pass=<PWD>: specify the password of the XMPP user representing the AWGS Bot. Default value is "1234567890".
The 
  (Customize your database connection in src/main/resources/applicationContext.xml, if not done already.)

  Start the service with

      mvn clean jetty:run
	
  After a successful start, the service is available under the base URL 
  
			localhost:8080/
			

	

