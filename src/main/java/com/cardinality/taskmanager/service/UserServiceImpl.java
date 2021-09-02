package com.cardinality.taskmanager.service;

import com.cardinality.taskmanager.config.SecurityUtils;
import com.cardinality.taskmanager.dto.UserDto;
import com.cardinality.taskmanager.entity.User;
import com.cardinality.taskmanager.exception.ElementNotFoundException;
import com.cardinality.taskmanager.repository.UserRepository;

import com.cardinality.taskmanager.util.TaskManErrors;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired  PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(UserDto userDto){

        User user = userRepository.findByUserName(userDto.getUserName());
        if(user!=null)
            throw new ElementNotFoundException(
                TaskManErrors.getErrorCode(TaskManErrors.TASK_USER,TaskManErrors.USER_ALREADY_AVAILABLE),
                TaskManErrors.getErrorMessage(TaskManErrors.ERROR_MAP.get(TaskManErrors.USER_ALREADY_AVAILABLE)));

        //todo password length validation
        user = adaptUser(userDto);

        userRepository.save(user);
    }

    private User adaptUser( UserDto userDto){
        User user = new User();
        user.setUserName(userDto.getUserName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setFullName(userDto.getFullName());
        user.setRole(userDto.getRole());
        user.setCreatedBy(SecurityUtils.getCurrentUserLogin().get());
        user.setCreatedDate(Instant.now());

        return user;
    }

}
