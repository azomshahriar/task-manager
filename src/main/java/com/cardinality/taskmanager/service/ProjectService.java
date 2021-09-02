package com.cardinality.taskmanager.service;

import com.cardinality.taskmanager.dto.ProjectDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {

     void createProject(ProjectDto projectDto);

     Page<ProjectDto> getProjects(Pageable pageable);

     void deleteProject(Long projectId);
     Page<ProjectDto> findProjectByUser(Long userId,Pageable pageable);

}
