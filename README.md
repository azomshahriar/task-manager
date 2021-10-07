
# Task Manager REST API
This is sample task-management project. This service also can be used as micro service component by introducing central auth server.

Dependency of this project:
- Java 11
- MySQL DB Server
- Spring boot 2.5.4
- Maven

### This demo project can be an example of-
    - basic Spring boot project
    - spring security and authentication & authorization.
    - Error Handling and Error Message
    - Messaging Internationalization
    - Basic Unit,Integration and System testing
    - annotation based input validation
    - auditing using spring security

How to test:
  - mvn test

How ot build:
  - mvn clean verify

How to run:
  - To run the project you need mysql server and set up mysql user/pass at application.yml or any other environment
  - Build the project using command: mvn clean verify 
  - Run the jar file following command: java -jar target/task-manager-0.0.1-SNAPSHOT.jar
  - Then you can access the application at http://localhost:8070/
  - At the start up of the application default admin user/pass(admin:87654321) will be inserted at DB, using this credential admin
  user can create other user.

API Doc:
 - All api definitions are available at http://localhost:8070/swagger-ui/index.html


## Sample Rest Request:

### Admin Login:

Url: http://localhost:8070/api/v1/authenticate

Method:POST

Request:

    {
        "username":"admin",
        "password":"87654321"
    }

### NB: 
All request need header: Authentiation: Bearer: token 
    
User can also pass language header: Accept-Language:en/bn

### Create a Project:
URL: http://localhost:8070/api/v1/project

Method: POST

Request:

    {
        "name":"Test Project",
        "description":"Description Test Project"
    }
    
### Create a Task:
URL: http://localhost:8070/api/v1/task

Method: POST
    
 Request:
    
    {
        "description":"Sample Task",
        "dueDate":"2021-09-10T00:00:00.00Z",
        "projectId":1,
        "status":"OPEN"
    }

### Task Search:
    
URL:http://localhost:8070/api/v1/task/search?expired=true



### datasource config:
     datasource:
        username: root
        password: password
        url: jdbc:mysql://localhost:3306/task_manager?autoReconnect=true&useSSL=false&createDatabaseIfNotExist=true

## More Improvement notes for performance, scalability & maintainability:
- Add more test cases.
- Add distributed tracing.
- Distributed cache(redis/memcache) to improve read performance.
- implement CQRS and separate independent read using MySQL read replica.    
- Store change event(task, project) history.
- Add centralized logging feature(using ELK).
- docker file and docker compose file.
- Add script(deployment, service, ingress file) for k8 deployment.    
- DB migration FlyWay
- Implement Idempotent operation using idempotent key.
- Code quality check(sonar quebe)  and code style check.    
- implement Spring cloud config & vault for configuration management.
- implement refresh token and csrf token.  
- incorporate CI/CD pipeline.  
    
    
    
    
    
  
