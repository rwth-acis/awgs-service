@echo on

mvn clean jetty:run -Djetty.port=9000 -Dxmpp.host=localhost

* `-Djetty.port=<NUM>` : port on which the Web service is reachable. Default value is 8080.
* `-Dxmpp.host=<HOSTNAME>` : hostname of the XMPP server on which the AWGS Bot should listen. If no value is set for this parameter, the service will omit starting the AWGS Bot.
* `-Dxmpp.port=<PORT>` : port of the XMPP server on which the AWGS Bot should listen. Default value is 5222 (XMPP standard port for client-server connections).
* `-Dxmpp.user=<LOGIN>` : login of the XMPP user representing the AWGS Bot. Such a user must already exist on the XMPP server! The service will not create such a user on-the-fly. Default value is "awgs-bot".
* `-Dxmpp.pass=<PWD>` : specify the password of the XMPP user representing the AWGS Bot. Default value is "1234567890".