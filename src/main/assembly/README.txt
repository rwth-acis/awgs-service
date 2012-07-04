AWGS Service
============
A service for managing the ACIS Working Group Series, a series of working papers created by members of the ACIS working group at Chair of Computer Science 5 - Databases & Information Systems, RWTH Aachen University, Germany.

To start the service do the following:

1) Change to bin directory

	cd ./bin
	
2) Configure start script
	
In the bin directory you find two start scripts, one for Windows-based systems (start.cmd) and one for Unix-based systems (start.sh). 
Each of them contains a commandline for starting the AWGS Web Service (+ AWGS Bot, if configured properly). 

There are several configuration options available:

	--port <NUM> : specify the port on which the Web service is reachable. Default value is 8080.
	
	-Dxmpp.host <HOSTNAME> : specify the hostname of the XMPP server on which the AWGS Bot should listen. If no value is set for this parameter, the service will omit starting the AWGS Bot.
	-Dxmpp.port <PORT> : specify the port of the XMPP server on which the AWGS Bot should listen. Default value is 5222 (XMPP standard port for client-server connections).
	-Dxmpp.user <LOGIN> : specify the login of the XMPP user representing the AWGS Bot. Such a user must already exist. The service will not create such a user on-the-fly. Default value is "awgs-bot".
	-Dxmpp.pass <PWD>: specify the password of the XMPP user representing the AWGS Bot. Default value is "1234567890".

3) Run start script

Once you have finished configuration, execute the respective start script.
	
	./start.sh OR ./start.cmd

For Unix-based systems it might be necessary to adjust access rights of the script before execution:
	
	chmod 744 ./start.sh
	
In the case of productive use on a Unix-based server, we recommend the following command line:

	nohup ./start.sh &
	
