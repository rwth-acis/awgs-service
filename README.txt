ADVANCED WORKING GROUP SERIES SERVICE
=====================================

  (Customize your database connection in src/main/resources/applicationContext.xml, if not done already.)

  Start the service with

      mvn jetty:run
	
  After a successful start the service is available under the base URL 
  
	localhost:8080/awgs-service/
	
  The following resources are available under localhost:8080/ugnm-service/resources
  
	/users - collection resource; GET (retrieve list of all users), POST (create new user as subresource)
	/users/{login} - item resource; GET (retrieve user details), PUT (update user details), DELETE (delete user)

