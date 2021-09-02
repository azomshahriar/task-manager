package com.cardinality.taskmanager.controller;

import com.cardinality.taskmanager.config.JwtTokenUtil;
import com.cardinality.taskmanager.dto.JwtRequest;
import com.cardinality.taskmanager.dto.JwtResponse;
import com.cardinality.taskmanager.exception.CommonException;
import com.cardinality.taskmanager.service.JwtUserDetailsService;
import com.cardinality.taskmanager.util.TaskManErrors;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin
@RequestMapping("/api/v1/")
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "Login", description = "", tags={ "user" })
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new CommonException(TaskManErrors.getErrorCode(TaskManErrors.TASK_USER,TaskManErrors.INVALID_CREDENTIALS),
                    TaskManErrors.getErrorMessage(TaskManErrors.ERROR_MAP.get(TaskManErrors.INVALID_CREDENTIALS)));
        }
    }
}
