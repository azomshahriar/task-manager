package com.cardinality.taskmanager.controller;


import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cardinality.taskmanager.dto.JwtRequest;
import com.cardinality.taskmanager.dto.JwtResponse;
import com.cardinality.taskmanager.dto.TaskDto;
import com.cardinality.taskmanager.entity.Task.Status;
import com.cardinality.taskmanager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
//@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TaskService taskService;

    public static String authToken;

//    @BeforeAll
//    public static void login() throws Exception{
//        adminLoginTest();
//    }
    @Test
    @Order(1)
    //@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:test-data.sql")
    public void adminLoginTest() throws Exception{

        JwtRequest loginRequest = new JwtRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("87654321");

        ResultActions result  =mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))

        ).andExpect(
                status().isOk()
        );

        String respString = result.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        authToken = jsonParser.parseMap(respString).get("token").toString();
        System.out.println(authToken);


    }

    @Test
    @Order(2)
    public void testCreateTask() throws Exception{

        doNothing().when(taskService).createTask(any(TaskDto.class));

        TaskDto taskDto = new TaskDto();
        taskDto.setDescription("Sample task");
        taskDto.setStatus(Status.OPEN);
        taskDto.setDueDate(Instant.now().plus(10, ChronoUnit.DAYS));
        taskDto.setProjectId(1L);

        mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/task", taskDto)
                        .header("Authorization","Bearer "+authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto))

        ).andExpect(
                status().isCreated()
        );
    }

    @Test
    @Order(3)
    public void testTask_Description_Mendatory() throws Exception{

        doNothing().when(taskService).createTask(any(TaskDto.class));
        TaskDto taskDto = new TaskDto();
        taskDto.setDescription("");
        taskDto.setStatus(Status.OPEN);
        taskDto.setDueDate(Instant.now().plus(10, ChronoUnit.DAYS));
        taskDto.setProjectId(1L);

        mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/task", taskDto)
                        .header("Authorization","Bearer "+authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto))

        ).andExpect(
                status().is4xxClientError()
        ).andExpect(jsonPath("$.message", Matchers.containsString("description can't be blank")));
    }

    @Test
    @Order(4)
    public void testTask_Status_Mendatory() throws Exception{

        doNothing().when(taskService).createTask(any(TaskDto.class));
        TaskDto taskDto = new TaskDto();
        taskDto.setDescription("Description");
        taskDto.setDueDate(Instant.now().plus(10, ChronoUnit.DAYS));
        taskDto.setProjectId(1L);

        mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/task", taskDto)
                        .header("Authorization","Bearer "+authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto))

        ).andExpect(
                status().is4xxClientError()
        ).andExpect(jsonPath("$.message", Matchers.containsString("Status can't be null")));
    }

    @Test
    @Order(5)
    public void testTask_Project_Mendatory() throws Exception{

        doNothing().when(taskService).createTask(any(TaskDto.class));
        TaskDto taskDto = new TaskDto();
        taskDto.setDescription("Description");
        taskDto.setStatus(Status.IN_PROGRESS);
        taskDto.setDueDate(Instant.now().plus(10, ChronoUnit.DAYS));

        mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/task", taskDto)
                        .header("Authorization","Bearer "+authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto))

        ).andExpect(
                status().is4xxClientError()
        ).andExpect(jsonPath("$.message", Matchers.containsString("must me under a project")));
    }

    @Test
    @Order(6)
    public void testTask_DueDate_Should_Future() throws Exception{

        doNothing().when(taskService).createTask(any(TaskDto.class));
        TaskDto taskDto = new TaskDto();
        taskDto.setDescription("Description");
        taskDto.setStatus(Status.IN_PROGRESS);
        taskDto.setDueDate(Instant.now().minus(1, ChronoUnit.DAYS));

        mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/task", taskDto)
                        .header("Authorization","Bearer "+authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto))

        ).andExpect(
                status().is4xxClientError()
        ).andExpect(jsonPath("$.message", Matchers.containsString("Date Should be future")));
    }
}
