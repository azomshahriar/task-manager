package com.cardinality.taskmanager.service;

import com.cardinality.taskmanager.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    void createUser(UserDto userDto);
    Page<UserDto> getUsers(Pageable pageable);
    UserDto getUser(String userName);
}
