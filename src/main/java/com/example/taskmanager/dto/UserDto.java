package com.example.taskmanager.dto;

import com.example.taskmanager.entity.User;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
public class UserDto {

    private Long id;

    @NotBlank
    @Length(min = 4, max = 15)
    private String userName;

    private String fullName;

    @NotNull
    @Length(min = 6, max = 12)
    private String password;

    @NotNull private User.Role role;
}
