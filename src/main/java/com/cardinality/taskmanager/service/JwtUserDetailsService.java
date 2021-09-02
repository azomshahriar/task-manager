package com.cardinality.taskmanager.service;

import com.cardinality.taskmanager.repository.UserRepository;
import com.cardinality.taskmanager.util.TaskManErrors;
import java.util.ArrayList;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("Find User: {}",username);
        com.cardinality.taskmanager.entity.User user = userRepository.findByUserName(username);

        if (user!=null) {
            List<GrantedAuthority> roles = new ArrayList<>();
            roles.add(new SimpleGrantedAuthority(user.getRole().name()));
            return new User(user.getUserName(), user.getPassword(),roles);

        } else {
            throw new UsernameNotFoundException(TaskManErrors.getErrorMessage(TaskManErrors.ERROR_MAP.get(TaskManErrors.USER_NOT_FOUND)));
        }
    }
}

