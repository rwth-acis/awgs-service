# ACIS Working Group Series (AWGS) Service

A service for managing the ACIS Working Group Series, a series of working papers created by members of the Advanced Community Information Systems (ACIS) group at Chair of Computer Science 5 - Databases & Information Systems, RWTH Aachen University, Germany.

This README includes documentation on the following tasks:

1. Preparing AWGS Database
2. Running AWGS Service
3. Building AWGS Service from Source

__IMPORTANT: AWGS uses Apache Maven 2! Maven 3 will not work!__

## Preparing AWGS Database (only once)
Execute the AWGS schema SQL script as database admin user with sufficient privileges to create an AWGS database. The script will also create a database user `awgs` with respective privileges granted.

```mysql -u root -p < ./src/main/resources/sql/schema_awgs.sql```

__IMPORTANT:__ The default configuration assumes that MySQL is running on the same machine like AWGS. However, we recommend to use a different machine and also set a different password than the default. In order to use a different password, please change `./src/main/resources/sql/schema_awgs.sql` before preparing the database and `./src/main/resources/applicationContext.xml` before running the service.

## Running AWGS Service
	
Use the following commandline to start a development server

```mvn clean jetty:run -D...```

There are several optional configuration parameters available:

* `-Djetty.port=<NUM>` : port on which the Web service is reachable. Default value is 8080.
* `-Dxmpp.host=<HOSTNAME>` : hostname of the XMPP server on which the AWGS Bot should listen. If no value is set for this parameter, the service will omit starting the AWGS Bot.
* `-Dxmpp.port=<PORT>` : port of the XMPP server on which the AWGS Bot should listen. Default value is 5222 (XMPP standard port for client-server connections).
* `-Dxmpp.user=<LOGIN>` : login of the XMPP user representing the AWGS Bot. Such a user must already exist on the XMPP server! The service will not create such a user on-the-fly. Default value is "awgs-bot".
* `-Dxmpp.pass=<PWD>` : specify the password of the XMPP user representing the AWGS Bot.

## Building AWGS Service from Source

Use the following commandline to build and package a deployable version of the AWGS service:
	
```mvn clean package```
	
After successful completion, you will find deployables in subdirectory `./target`:
	
```
./target/awgs-service-0.3.zip
./target/awgs-service-0.3.tar.gz
```			

Unpack and follow the instructions above to run the service.