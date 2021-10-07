package com.example.taskmanager.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectDto {

    private Long id;

    @NotBlank(message = "Project name can't be blank.")
    private String name;

    private String description;
}
