package com.example.taskmanager.controller.admin;

import com.example.taskmanager.dto.ProjectDto;
import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.service.ProjectService;
import com.example.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired TaskService taskService;
    @Autowired ProjectService projectService;

    @Operation(
            summary = "List of Task By User",
            description = "",
            tags = {"admin"})
    @GetMapping("/task/byUser/{userId}")
    public ResponseEntity<Page<TaskDto>> getTaskByProject(
            @PathVariable(name = "userId", required = true) Long userId, Pageable pageable) {
        log.info("Get Task By project. Project Id:{}.");
        return ResponseEntity.ok(taskService.findTaskByUser(userId, pageable));
    }

    @Operation(
            summary = "List of Project",
            description = "",
            tags = {"admin"})
    @GetMapping("/project/byUser/{userId}")
    public ResponseEntity<Page<ProjectDto>> getProjectByUser(
            @PathVariable(name = "userId", required = true) Long userId, Pageable pageable) {
        log.info("Get all Project.");
        return ResponseEntity.ok(projectService.findProjectByUser(userId, pageable));
    }
}
