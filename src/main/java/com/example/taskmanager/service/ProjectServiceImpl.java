package com.example.taskmanager.service;

import com.example.taskmanager.config.SecurityUtils;
import com.example.taskmanager.dto.ProjectDto;
import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.entity.User.Role;
import com.example.taskmanager.exception.BadRequestException;
import com.example.taskmanager.exception.CommonException;
import com.example.taskmanager.exception.ElementNotFoundException;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.util.TaskManErrors;
import java.time.Instant;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired ProjectRepository projectRepository;

    @Autowired UserRepository userRepository;

    @Transactional
    public void createProject(ProjectDto projectDto) {
        Project project = adaptProject(projectDto);
        projectRepository.save(project);
    }

    private Project adaptProject(ProjectDto projectDto) {
        Project project = new Project();
        project.setDescription(projectDto.getDescription());
        project.setName(projectDto.getName());
        project.setCreatedDate(Instant.now());
        project.setUser(getSecurityUser());
        return project;
    }

    @Transactional(readOnly = true)
    public Page<ProjectDto> getProjects(Pageable pageable) {
        Page<Project> projectPage;
        if (SecurityUtils.hasCurrentUserThisAuthority(Role.ADMIN.name())) {
            projectPage = projectRepository.findAll(pageable);
        } else {
            projectPage = projectRepository.findAllByUser(getSecurityUser(), pageable);
        }
        return projectPage.map(this::adaptProjectDto);
    }

    private User getSecurityUser() {
        String userName = SecurityUtils.getCurrentUserLogin().get();
        User user = userRepository.findByUserName(userName);
        if (user == null)
            throw new ElementNotFoundException(
                    TaskManErrors.getErrorCode(
                            TaskManErrors.TASK_USER, TaskManErrors.INVALID_USER_CONTEXT),
                    TaskManErrors.getErrorMessage(
                            TaskManErrors.ERROR_MAP.get(TaskManErrors.INVALID_USER_CONTEXT)));
        return user;
    }

    private ProjectDto adaptProjectDto(Project project) {

        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(project.getId());
        projectDto.setName(project.getName());
        projectDto.setDescription(project.getDescription());
        return projectDto;
    }

    public void deleteProject(Long projectId) {

        Optional<Project> projectOp = projectRepository.findById(projectId);
        if (!projectOp.isPresent())
            throw new ElementNotFoundException(
                    TaskManErrors.getErrorCode(
                            TaskManErrors.TASK_USER, TaskManErrors.PROJECT_NOT_FOUND),
                    TaskManErrors.getErrorMessage(
                            TaskManErrors.ERROR_MAP.get(TaskManErrors.PROJECT_NOT_FOUND)));

        if (SecurityUtils.hasCurrentUserThisAuthority(Role.ADMIN.name())) {
            projectRepository.delete(projectOp.get());
        } else {
            String userName = SecurityUtils.getCurrentUserLogin().get();
            if (userName.equalsIgnoreCase(projectOp.get().getUser().getUserName())) {
                projectRepository.delete(projectOp.get());
            } else {
                throw new CommonException(
                        TaskManErrors.getErrorCode(
                                TaskManErrors.TASK_USER, TaskManErrors.FORBIDDEN_OPERATION),
                        TaskManErrors.getErrorMessage(
                                TaskManErrors.ERROR_MAP.get(TaskManErrors.FORBIDDEN_OPERATION)));
            }
        }
    }

    public Page<ProjectDto> findProjectByUser(Long userId, Pageable pageable) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new BadRequestException(
                    TaskManErrors.getErrorCode(
                            TaskManErrors.TASK_USER, TaskManErrors.USER_NOT_FOUND),
                    TaskManErrors.getErrorMessage(
                            TaskManErrors.ERROR_MAP.get(TaskManErrors.USER_NOT_FOUND)));
        }
        Page<Project> projectPage = projectRepository.findAllByUser(userOptional.get(), pageable);
        return projectPage.map(this::adaptProjectDto);
    }
}
