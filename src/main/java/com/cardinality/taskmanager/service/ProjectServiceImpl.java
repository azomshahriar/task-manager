package com.cardinality.taskmanager.service;


import com.cardinality.taskmanager.config.SecurityUtils;
import com.cardinality.taskmanager.dto.ProjectDto;
import com.cardinality.taskmanager.entity.Project;
import com.cardinality.taskmanager.entity.User;
import com.cardinality.taskmanager.entity.User.Role;
import com.cardinality.taskmanager.exception.BadRequestException;
import com.cardinality.taskmanager.exception.CommonException;
import com.cardinality.taskmanager.exception.ElementNotFoundException;
import com.cardinality.taskmanager.repository.ProjectRepository;
import com.cardinality.taskmanager.repository.UserRepository;
import com.cardinality.taskmanager.util.TaskManErrors;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository;



    @Transactional
    public void createProject(ProjectDto projectDto){
        Project project = adaptProject(projectDto);
        projectRepository.save(project);
    }

    private Project adaptProject(ProjectDto projectDto){
        Project project = new Project();
        project.setDescription(projectDto.getDescription());
        project.setName(projectDto.getName());
        project.setCreatedDate(Instant.now());

        if(SecurityUtils.getCurrentUserLogin().isPresent()){
            String userName = SecurityUtils.getCurrentUserLogin().get();
            User user = userRepository.findByUserName(userName);
            if(user!=null)
               project.setUser(user);
            else
                throw new ElementNotFoundException(TaskManErrors.getErrorCode(TaskManErrors.TASK_USER,TaskManErrors.USER_NOT_FOUND),
                        TaskManErrors.getErrorMessage(TaskManErrors.ERROR_MAP.get(TaskManErrors.USER_NOT_FOUND)));

        }else {
            throw new ElementNotFoundException(TaskManErrors.getErrorCode(TaskManErrors.TASK_USER,TaskManErrors.USER_NOT_FOUND),
                    TaskManErrors.getErrorMessage(TaskManErrors.ERROR_MAP.get(TaskManErrors.USER_NOT_FOUND)));

        }



        return project;
    }

    @Transactional(readOnly = true)
    public Page<ProjectDto> getProjects(Pageable pageable){
        Page<Project> projectPage;
        if(SecurityUtils.hasCurrentUserThisAuthority(Role.ADMIN.name())){
            projectPage = projectRepository.findAll(pageable);
        }else {
            String userName = SecurityUtils.getCurrentUserLogin().get();
            User user = userRepository.findByUserName(userName);
            projectPage= projectRepository.findAllByUser(user,pageable);
        }

        return projectPage.map(this::adaptProjectDto);

    }

    private ProjectDto adaptProjectDto(Project project){

        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(project.getId());
        projectDto.setName(project.getName());
        projectDto.setDescription(project.getDescription());
        return projectDto;

    }


    public void deleteProject(Long projectId){

        Optional<Project> projectOp = projectRepository.findById(projectId);
        if(!projectOp.isPresent())
            throw new ElementNotFoundException(TaskManErrors.getErrorCode(TaskManErrors.TASK_USER,TaskManErrors.PROJECT_NOT_FOUND),
                    TaskManErrors.getErrorMessage(TaskManErrors.ERROR_MAP.get(TaskManErrors.PROJECT_NOT_FOUND)));


        if(SecurityUtils.hasCurrentUserThisAuthority(Role.ADMIN.name())){
             projectRepository.delete(projectOp.get());
        }else {
            String userName = SecurityUtils.getCurrentUserLogin().get();
            if(userName.equalsIgnoreCase(projectOp.get().getUser().getUserName())){
                projectRepository.delete(projectOp.get());
            }else{
                throw new CommonException(TaskManErrors.getErrorCode(TaskManErrors.TASK_USER,TaskManErrors.FORBIDDEN_OPERATION),
                        TaskManErrors.getErrorMessage(TaskManErrors.ERROR_MAP.get(TaskManErrors.FORBIDDEN_OPERATION)));

            }

        }
    }

    public Page<ProjectDto> findProjectByUser(Long userId,Pageable pageable){
        Optional<User> userOptional = userRepository.findById(userId);
        if(!userOptional.isPresent()){
            throw new BadRequestException(
                    TaskManErrors.getErrorCode(TaskManErrors.TASK_USER,TaskManErrors.USER_NOT_FOUND),
                    TaskManErrors.getErrorMessage(TaskManErrors.ERROR_MAP.get(TaskManErrors.USER_NOT_FOUND)));

        }
       Page<Project> projectPage= projectRepository.findAllByUser(userOptional.get(),pageable);
       return projectPage.map(this::adaptProjectDto);
    }
}
