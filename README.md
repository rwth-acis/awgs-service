# ACIS Working Group Series (AWGS) Service

A service for managing the ACIS Working Group Series, a series of working papers created by members of the Advanced Community Information Systems (ACIS) group at Chair of Computer Science 5 - Databases & Information Systems, RWTH Aachen University, Germany.

This README includes documentation on the following tasks:

1. Running AWGS Service
2. Building AWGS Service from Source

__IMPORTANT__: both tasks require Apache Maven 2.x as main build tool. Not all tasks will work with Apache Maven 3.x!
	
## Running AWGS Service

### Prepare database (only once)

Prepare a mysql database using the predefined AWGS schema SQL script:
	
```mysql -u <awgs-service-user> -p < ./src/main/resources/sql/schema_awgs.sql```
		
Optionally, you can pre-populate your DB with a test data set:
		
```mysql -u <awgs-service-user> -p < ./src/main/resources/sql/testdata_awgs.sql```
	
Be sure to configure the database connection in src/main/resources/applicationContext.xml, if not done already.
	
### Start server
	
Use the following commandline to start a development server

```mvn clean jetty:run -D...```

There are several optional configuration parameters available:

* `-Djetty.port=<NUM>` : port on which the Web service is reachable. Default value is 8080.
* `-Dxmpp.host=<HOSTNAME>` : hostname of the XMPP server on which the AWGS Bot should listen. If no value is set for this parameter, the service will omit starting the AWGS Bot.
* `-Dxmpp.port=<PORT>` : port of the XMPP server on which the AWGS Bot should listen. Default value is 5222 (XMPP standard port for client-server connections).
* `-Dxmpp.user=<LOGIN>` : login of the XMPP user representing the AWGS Bot. Such a user must already exist on the XMPP server! The service will not create such a user on-the-fly. Default value is "awgs-bot".
* `-Dxmpp.pass=<PWD>` : specify the password of the XMPP user representing the AWGS Bot. Default value is "1234567890".

Per default, the service will be available for testing under the base URL `http://localhost:8080`

## Building AWGS Service from Source

Use the following commandline to build and package a deployable version of the AWGS service:
	
```mvn clean package```
	
After successful completion, you will find deployables in subdirectory `./target`:
	
```
./target/awgs-service-0.3.zip
./target/awgs-service-0.3.tar.gz
```			

Unpack and follow the instructions above to run the service.
	

