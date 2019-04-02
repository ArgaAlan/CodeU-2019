# CodeU Starter Project

**This repo contains Team 6 (Team JADANG)'s starter project for Google CodeU Spring 2019**

## Deploying to AppEngine
**To run locally:**  
```
mvn appengine:devserver
```  
The app can be explored by navigating to <a href="http://localhost:8080/">localhost:8080</a> or to <a href="http://localhost:8080/_ah/admin">the admin page</a> for debugging.  

**To push this code to the public-facing production server</strong>:**  
```
mvn appengine:update
``` 
You will need to login the first time this is run.
For problems or more information, see <a href="https://sites.google.com/codeustudents.com/spring-2019/week-0-setup/app-engine-setup?authuser=0">the project documentation</a>.

## Important Note:
Due to rapidly changing file structure and data properties, it will likely be necessary to run 
```
mvn clean
```
before running the code after pulling changes locally
