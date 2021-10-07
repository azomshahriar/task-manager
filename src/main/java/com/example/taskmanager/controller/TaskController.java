package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.entity.Task.Status;
import com.example.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/task")
public class TaskController {

    @Autowired TaskService taskService;

    @Operation(
            summary = "Create a new task",
            description = "",
            tags = {"task"})
    @PostMapping
    public ResponseEntity createTask(@RequestBody @Valid TaskDto taskDto) {
        log.info("Create New task: Description:{}", taskDto.getDescription());
        taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "List of Task",
            description = "",
            tags = {"task"})
    @GetMapping
    public ResponseEntity<Page<TaskDto>> getTask(Pageable pageable) {
        log.info("Get all Task.");
        return ResponseEntity.ok(taskService.getTasks(pageable));
    }

    @Operation(
            summary = " Task detail",
            description = "",
            tags = {"task"})
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskDetail(
            @PathVariable(name = "id", required = true) Long id) {
        log.info("Get Task.  Id:{}.");
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @Operation(
            summary = "List of Task By Project",
            description = "",
            tags = {"task"})
    @GetMapping("/byProject/{projectId}")
    public ResponseEntity<Page<TaskDto>> getTaskByProject(
            @PathVariable(name = "projectId", required = true) Long projectId, Pageable pageable) {
        log.info("Get Task By project. Project Id:{}.");
        return ResponseEntity.ok(taskService.getTasksByProject(projectId, pageable));
    }

    @Operation(
            summary = "List of Expired Task",
            description = "",
            tags = {"task"})
    @GetMapping("/expired")
    public ResponseEntity<Page<TaskDto>> getExpiredTask(Pageable pageable) {
        log.info("Get expired Task.");
        return ResponseEntity.ok(taskService.getExpiredTasks(pageable));
    }

    @Operation(
            summary = "Update Task",
            description = "",
            tags = {"task"})
    @PutMapping()
    public ResponseEntity<Page<TaskDto>> updateTask(@RequestBody @Valid TaskDto taskDto) {
        log.info("Update Task.Request:{}", taskDto);
        taskService.updateTask(taskDto);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Search Task",
            description = "Search Task By Project Id or Status or expired.",
            tags = {"task"})
    @GetMapping("/search")
    public ResponseEntity<List<TaskDto>> searchTask(
            @RequestParam(value = "projectId", required = false) Long projectId,
            @RequestParam(value = "expired", required = false) Boolean expired,
            @RequestParam(value = "status", required = false) Status status,
            Pageable pageable) {
        log.info(
                "Search Task. Param. ProjectId:{}, Expired:{},Status:{}",
                projectId,
                expired,
                status); // project,status,duedate
        return ResponseEntity.ok(taskService.searchTasks(projectId, expired, status));
    }
}
