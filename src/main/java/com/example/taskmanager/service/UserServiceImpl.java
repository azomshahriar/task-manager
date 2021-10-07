package com.example.taskmanager.service;

import com.example.taskmanager.config.SecurityUtils;
import com.example.taskmanager.dto.UserDto;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.exception.ElementNotFoundException;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.util.TaskManErrors;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired UserRepository userRepository;

    @Autowired PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(UserDto userDto) {

        User user = userRepository.findByUserName(userDto.getUserName());
        if (user != null)
            throw new ElementNotFoundException(
                    TaskManErrors.getErrorCode(
                            TaskManErrors.TASK_USER, TaskManErrors.USER_ALREADY_AVAILABLE),
                    TaskManErrors.getErrorMessage(
                            TaskManErrors.ERROR_MAP.get(TaskManErrors.USER_ALREADY_AVAILABLE)));

        // todo password length validation
        user = adaptUser(userDto);

        userRepository.save(user);
    }

    private User adaptUser(UserDto userDto) {
        User user = new User();
        user.setUserName(userDto.getUserName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setFullName(userDto.getFullName());
        user.setRole(userDto.getRole());
        user.setCreatedBy(SecurityUtils.getCurrentUserLogin().get());
        user.setCreatedDate(Instant.now());
        return user;
    }

    public Page<UserDto> getUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(this::adaptUserDto);
    }

    public UserDto getUser(String userName) {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            throw new ElementNotFoundException(
                    TaskManErrors.getErrorCode(
                            TaskManErrors.TASK_USER, TaskManErrors.USER_NOT_FOUND),
                    TaskManErrors.getErrorMessage(
                            TaskManErrors.ERROR_MAP.get(TaskManErrors.USER_NOT_FOUND)));
        }
        return adaptUserDto(user);
    }

    private UserDto adaptUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUserName(user.getUserName());
        userDto.setFullName(user.getFullName());
        userDto.setRole(user.getRole());
        return userDto;
    }
}
