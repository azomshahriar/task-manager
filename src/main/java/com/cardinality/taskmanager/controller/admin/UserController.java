package com.cardinality.taskmanager.controller.admin;

import com.cardinality.taskmanager.dto.ProjectDto;
import com.cardinality.taskmanager.dto.UserDto;
import com.cardinality.taskmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/user")
public class UserController {

    @Autowired
    UserService userService;

    @Operation(summary = "Create a new user", description = "", tags={ "user" })
    @PostMapping
    public ResponseEntity createProject(@RequestBody @Valid UserDto userDto){
        log.info("New Project. Name:{}",userDto.getUserName());
        userService.createUser(userDto);
        return ResponseEntity.ok().build();
    }
}
