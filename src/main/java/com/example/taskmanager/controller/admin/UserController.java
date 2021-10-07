package com.example.taskmanager.controller.admin;

import com.example.taskmanager.dto.UserDto;
import com.example.taskmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/user")
public class UserController {

    @Autowired UserService userService;

    @Operation(
            summary = "Create a new user",
            description = "",
            tags = {"user"})
    @PostMapping
    public ResponseEntity createUser(@RequestBody @Valid UserDto userDto) {
        log.info("New Project. Name:{}", userDto.getUserName());
        userService.createUser(userDto);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "List of User",
            description = "",
            tags = {"user"})
    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(Pageable pageable) {
        ;
        return ResponseEntity.ok().body(userService.getUsers(pageable));
    }

    @Operation(
            summary = "Get a user by userName",
            description = "",
            tags = {"user"})
    @GetMapping("/{userName}")
    public ResponseEntity<UserDto> getUser(
            @PathVariable(name = "userName", required = true) String userName) {
        log.info("Get user Info: {}", userName);
        return ResponseEntity.ok().body(userService.getUser(userName));
    }
}
