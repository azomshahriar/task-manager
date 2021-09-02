package com.cardinality.taskmanager.dto;

import com.cardinality.taskmanager.entity.User;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
