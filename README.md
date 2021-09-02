This is sample task-management component. This service also can be used as micro service component by introducing central auth.

Dependency of this project:
- Java 11
- MySql Server
- Spring boot 2.5
- maven

This is a demo of
    - basic Spring boot project
    - spring security and authentication & authorization.
    - Error Handling and Error Message
    - Messaging Internationalization
    - Basic Unit,Integration and System testing
    - annotation based input validation

How to test:
  - mvn test

How ot build:
  - mvn clean verify

How to run:
  - to run the project you need mysql server and set up mysql user/pass at application.yml or any environment
  - then build the project using command: mvn clean verify
  - there will be a jar file at target folder
  - run the jar file following command: java -jar target/task-manager-0.0.1-SNAPSHOT.jar
  - Then you can access the application at http://localhost:8080/
  - at start up of the application there is a default admin user/pass(admin:87654321), using this credential admin
  user can create user.

API Doc:
 - you will find all api definition at http://localhost:8080/swagger-ui/index.html


 Sample Rest Request:

Admin Login:
Url: localhost:8070/api/v1/authenticate
Method:POST
Request Body:
{
    "username":"admin",
    "password":"87654321"
}

NB: All request need header: Authentiation: Bearer:<token>
User can also pass language header: Accept-Language:en/bn

Create a Task:
URL: localhost:8070/api/v1/task
Method: POST
Request:
{
    "description":"Sample Task",
    "dueDate":"2021-09-10T00:00:00.00Z",
    "projectId":1,
    "status":"OPEN"
}

Task Search:
URL:localhost:8070/api/v1/task/search?expired=true



##sample data source config
 datasource:
    username: root
    password: password
    url: jdbc:mysql://localhost:3306/task_manager

Future Improvement:

- Add distributed tracing and custom distributed tracing
- Add centralized logging feature
- docker image and docker build
- docker compose with mysql db
- DB migration FlyWay
- implement Idempotent operation using idempotent key
