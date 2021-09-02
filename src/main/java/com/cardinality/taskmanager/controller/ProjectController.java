package com.cardinality.taskmanager.controller;


import com.cardinality.taskmanager.dto.ProjectDto;
import com.cardinality.taskmanager.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @Operation(summary = "Create a new project", description = "", tags={ "project" })
    @PostMapping
    public ResponseEntity createProject(@RequestBody @Valid ProjectDto projectDto){
        log.info("New Project. Name:{}",projectDto.getName());
        projectService.createProject(projectDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "List of Project", description = "", tags={ "project" })
    @GetMapping
    public ResponseEntity<Page<ProjectDto>> getProject(Pageable pageable){
        log.info("Get all Project.");
        return ResponseEntity.ok(projectService.getProjects(pageable));
    }

    @Operation(summary = "List of Project", description = "", tags={ "project" })
    @DeleteMapping("/{id}")
    public ResponseEntity<Page<ProjectDto>> deleteProject(@PathVariable(name = "id",required = true) Long id){
        log.info("Delete Project.ID:{}",id);
        projectService.deleteProject(id);
        return ResponseEntity.ok().build();
    }

}
