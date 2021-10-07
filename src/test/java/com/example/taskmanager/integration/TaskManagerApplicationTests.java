package com.example.taskmanager.integration;

import com.example.taskmanager.dto.JwtResponse;
import com.example.taskmanager.dto.ProjectDto;
import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.dto.UserDto;
import com.example.taskmanager.entity.Task.Status;
import com.example.taskmanager.entity.User.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TaskManagerApplicationTests {

    @Value("${server.port}")
    private int serverPort;

    private String baseUrl;
    private RestTemplate restTemplate;
    private String loginUrl;
    public static String authToken;
    public static String user1 = "user1";
    public static String user2 = "user2";

    @Autowired ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + serverPort;
        System.out.println(baseUrl);
        this.loginUrl = "/api/v1/authenticate";
        this.restTemplate = new RestTemplate();
    }

    @Test
    @Order(1)
    void contextLoads() {
        System.out.println("-------------Context Loaded------------");
        System.out.println("Base Url:" + baseUrl);
    }

    @Test
    @Order(2)
    // @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts =
    // "classpath:test-data.sql")
    public void adminLoginTest() throws Exception {

        JSONObject loginRequest =
                new JSONObject().put("username", "admin").put("password", "87654321");
        String url = this.baseUrl + this.loginUrl;
        System.out.println("Calling:" + url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(loginRequest.toString(), headers);
        ResponseEntity<JwtResponse> responseEntity =
                restTemplate.postForEntity(url, request, JwtResponse.class);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        System.out.println("Token:" + responseEntity.getBody().getToken());
        authToken = responseEntity.getBody().getToken();
    }

    @Test
    @Order(2)
    public void createUserTest() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setUserName(user1);
        userDto.setPassword("87654321");
        userDto.setRole(Role.USER);
        userDto.setFullName("User1");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + authToken);
        HttpEntity<String> request =
                new HttpEntity<String>(objectMapper.writeValueAsString(userDto), headers);
        ResponseEntity responseEntity =
                restTemplate.postForEntity(
                        this.baseUrl + "/api/v1/admin/user", request, String.class);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        userDto.setUserName(user2);
        request = new HttpEntity<String>(objectMapper.writeValueAsString(userDto), headers);
        responseEntity =
                restTemplate.postForEntity(
                        this.baseUrl + "/api/v1/admin/user", request, String.class);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @Order(3)
    public void checkOnlyAdminCanCreateUser() throws Exception {
        JSONObject loginRequest =
                new JSONObject().put("username", user1).put("password", "87654321");
        String loginUrl = this.baseUrl + this.loginUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<String>(loginRequest.toString(), headers);
        ResponseEntity<JwtResponse> responseEntity =
                restTemplate.postForEntity(loginUrl, request, JwtResponse.class);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        System.out.println("User Token:" + responseEntity.getBody().getToken());
        String userToken = responseEntity.getBody().getToken();

        // try to create user
        UserDto userDto = new UserDto();
        userDto.setUserName("testuser");
        userDto.setPassword("87654321");
        userDto.setRole(Role.USER);
        userDto.setFullName("testuser");
        headers.add("Authorization", "Bearer " + userToken);

        final HttpEntity<String> request1 =
                new HttpEntity<String>(objectMapper.writeValueAsString(userDto), headers);

        Assertions.assertThrows(
                Exception.class,
                () -> {
                    ResponseEntity response =
                            restTemplate.postForEntity(
                                    this.baseUrl + "/api/v1/admin/user", request1, String.class);
                });
    }

    @Test
    @Order(4)
    public void createProjectAndTask_Corresponding_User() throws Exception {
        ProjectDto projectDtoAdmin = new ProjectDto();
        projectDtoAdmin.setName("Project admin");
        projectDtoAdmin.setDescription("P D A");

        ProjectDto projectDto1 = new ProjectDto();
        projectDto1.setName("Project1");
        projectDto1.setDescription("P D 1");

        ProjectDto projectDto2 = new ProjectDto();
        projectDto2.setName("Project2");
        projectDto2.setDescription("P D 2");

        createObject(projectDtoAdmin, this.baseUrl + "/api/v1/project", authToken);
        createObject(projectDto1, this.baseUrl + "/api/v1/project", getTokenUser(user1));
        createObject(projectDto2, this.baseUrl + "/api/v1/project", getTokenUser(user2));
        createObject(projectDto2, this.baseUrl + "/api/v1/project", getTokenUser(user2));

        Page<ProjectDto> adminProject =
                getUserProjects(this.baseUrl + "/api/v1/project", authToken);
        Page<ProjectDto> user1Project =
                getUserProjects(this.baseUrl + "/api/v1/project", getTokenUser(user1));
        Page<ProjectDto> user2Project =
                getUserProjects(this.baseUrl + "/api/v1/project", getTokenUser(user2));
        Assertions.assertEquals(4, adminProject.getTotalElements());
        Assertions.assertEquals(1, user1Project.getTotalElements());
        Assertions.assertEquals(2, user2Project.getTotalElements());

        TaskDto taskDtoAdmin = new TaskDto();
        taskDtoAdmin.setProjectId(adminProject.getContent().get(0).getId());
        taskDtoAdmin.setDescription("TaskAdmin");
        taskDtoAdmin.setStatus(Status.OPEN);
        taskDtoAdmin.setDueDate(Instant.now().plus(10, ChronoUnit.DAYS));

        TaskDto taskDtoUser1 = new TaskDto();
        taskDtoUser1.setProjectId(user1Project.getContent().get(0).getId());
        taskDtoUser1.setDescription("TaskUser1");
        taskDtoUser1.setStatus(Status.OPEN);
        taskDtoUser1.setDueDate(Instant.now().plus(10, ChronoUnit.DAYS));

        TaskDto taskDtoUser2 = new TaskDto();
        taskDtoUser2.setProjectId(user2Project.getContent().get(0).getId());
        taskDtoUser2.setDescription("TaskUser2");
        taskDtoUser2.setStatus(Status.OPEN);
        taskDtoUser2.setDueDate(Instant.now().plus(10, ChronoUnit.DAYS));

        createObject(taskDtoAdmin, this.baseUrl + "/api/v1/task", authToken);
        createObject(taskDtoUser1, this.baseUrl + "/api/v1/task", getTokenUser(user1));
        createObject(taskDtoUser2, this.baseUrl + "/api/v1/task", getTokenUser(user2));
        taskDtoUser2.setDescription("TaskUser3");
        taskDtoUser2.setStatus(Status.CLOSED);
        createObject(taskDtoUser2, this.baseUrl + "/api/v1/task", getTokenUser(user2));

        Page<TaskDto> adminTasks = getUserTask(this.baseUrl + "/api/v1/project", authToken);
        Page<TaskDto> user1Tasks =
                getUserTask(this.baseUrl + "/api/v1/project", getTokenUser(user1));
        Page<TaskDto> user2Tasks =
                getUserTask(this.baseUrl + "/api/v1/project", getTokenUser(user2));

        Assertions.assertEquals(4, adminTasks.getTotalElements());
        Assertions.assertEquals(1, user1Tasks.getTotalElements());
        Assertions.assertEquals(2, user2Tasks.getTotalElements());
    }

    public Page<ProjectDto> getUserProjects(String url, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<RestResponsePage<ProjectDto>> responseEntity =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        request,
                        new ParameterizedTypeReference<RestResponsePage<ProjectDto>>() {});
        return responseEntity.getBody();
    }

    public Page<TaskDto> getUserTask(String url, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<RestResponsePage<TaskDto>> responseEntity =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        request,
                        new ParameterizedTypeReference<RestResponsePage<TaskDto>>() {});
        return responseEntity.getBody();
    }

    public void createObject(Object object, String url, String token) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<String> request =
                new HttpEntity<String>(objectMapper.writeValueAsString(object), headers);
        ResponseEntity responseEntity = restTemplate.postForEntity(url, request, JwtResponse.class);
    }

    public String getTokenUser(String userName) throws Exception {
        JSONObject loginRequest =
                new JSONObject().put("username", userName).put("password", "87654321");
        String loginUrl = this.baseUrl + this.loginUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<String>(loginRequest.toString(), headers);
        ResponseEntity<JwtResponse> responseEntity =
                restTemplate.postForEntity(loginUrl, request, JwtResponse.class);
        return responseEntity.getBody().getToken();
    }

    @Test
    @Order(5)
    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:update-due-date.sql")
    public void checkExpiredTask() {
        String searchUrl = this.baseUrl + "/api/v1/task/search?expired=true";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + authToken);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<List<TaskDto>> responseEntity =
                restTemplate.exchange(
                        searchUrl,
                        HttpMethod.GET,
                        request,
                        new ParameterizedTypeReference<List<TaskDto>>() {});
        Assertions.assertEquals(1, responseEntity.getBody().size());
    }

    @Test
    @Order(6)
    public void checkClosedTask() {
        String searchUrl = this.baseUrl + "/api/v1/task/search?status=CLOSED";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + authToken);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<List<TaskDto>> responseEntity =
                restTemplate.exchange(
                        searchUrl,
                        HttpMethod.GET,
                        request,
                        new ParameterizedTypeReference<List<TaskDto>>() {});

        Assertions.assertEquals(1, responseEntity.getBody().size());
    }

    @Test
    @Order(6)
    public void adminSearchTest() {
        UserDto userDto1 = getUser(user1);
        UserDto userDto2 = getUser(user2);
        String taskByUser = this.baseUrl + "/api/v1/admin/task/byUser/";
        String projectByUser = this.baseUrl + "/api/v1/admin/project/byUser/";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + authToken);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<RestResponsePage<TaskDto>> responseEntity =
                restTemplate.exchange(
                        taskByUser + userDto1.getId(),
                        HttpMethod.GET,
                        request,
                        new ParameterizedTypeReference<RestResponsePage<TaskDto>>() {});

        Assertions.assertEquals(1, responseEntity.getBody().getTotalElements());
        responseEntity =
                restTemplate.exchange(
                        taskByUser + userDto2.getId(),
                        HttpMethod.GET,
                        request,
                        new ParameterizedTypeReference<RestResponsePage<TaskDto>>() {});
        Assertions.assertEquals(2, responseEntity.getBody().getTotalElements());

        ResponseEntity<RestResponsePage<ProjectDto>> responseEntityP =
                restTemplate.exchange(
                        projectByUser + userDto1.getId(),
                        HttpMethod.GET,
                        request,
                        new ParameterizedTypeReference<RestResponsePage<ProjectDto>>() {});
        Assertions.assertEquals(1, responseEntityP.getBody().getTotalElements());
        responseEntityP =
                restTemplate.exchange(
                        projectByUser + userDto2.getId(),
                        HttpMethod.GET,
                        request,
                        new ParameterizedTypeReference<RestResponsePage<ProjectDto>>() {});
        Assertions.assertEquals(2, responseEntityP.getBody().getTotalElements());
    }

    private UserDto getUser(String user) {
        String getUserUrl = this.baseUrl + "/api/v1/admin/user/" + user;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + authToken);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<UserDto> responseEntity =
                restTemplate.exchange(getUserUrl, HttpMethod.GET, request, UserDto.class);

        return responseEntity.getBody();
    }
}
