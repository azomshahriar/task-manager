package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.Task.Status;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.entity.User.Role;
import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class TaskRepositoryTest {

    @Autowired TaskRepository taskRepository;
    @Autowired UserRepository userRepository;
    @Autowired ProjectRepository projectRepository;

    @Test
    void injectedComponentsAreNotNull() {
        Assertions.assertNotNull(taskRepository);
    }

    public User getUser() {
        User user1 = userRepository.findByUserName("user1");
        if (user1 == null) {
            user1 = new User();
            user1.setUserName("user1");
            user1.setPassword("87654321");
            user1.setRole(Role.USER);
            user1.setFullName("User 1");
            user1 = userRepository.save(user1);
        }

        return user1;
    }

    public Project getProject() {
        Project project = new Project();
        project.setName("Project1");
        project.setDescription("P Description");
        project.setUser(getUser());
        // return project;
        return projectRepository.save(project);
    }

    @Test
    public void createTaskTest() {
        User user = getUser();
        Project project = getProject();
        project.setUser(user);
        Task task = new Task();
        task.setStatus(Status.OPEN);
        task.setDescription("Task1");
        task.setProject(project);
        task.setUser(user);
        task = taskRepository.save(task);

        Assertions.assertTrue(task.getId() > 0);
    }

    @Test
    public void throwExceptionWithOutProject() {
        User user = getUser();
        user = userRepository.save(user);
        Project project = getProject();
        project.setUser(user);
        Task task = new Task();
        task.setStatus(Status.OPEN);
        task.setDescription("Task1");
        task.setUser(user);
        Assertions.assertThrows(Exception.class, () -> taskRepository.save(task));
    }

    @Test
    public void throwExceptionWithOutUser() {

        User user = userRepository.findByUserName("user1");
        Project project = getProject();
        project.setUser(user);
        projectRepository.save(project);
        Task task = new Task();
        task.setStatus(Status.OPEN);
        task.setDescription("Task1");
        task.setProject(project);
        task.setCreatedDate(Instant.now());
        Assertions.assertThrows(Exception.class, () -> taskRepository.save(task));
    }

    @Test
    public void closeTask() {
        createTaskTest();
        Task task = taskRepository.findAll().get(0);
        task.setStatus(Status.CLOSED);
        task = taskRepository.save(task);
        Assertions.assertEquals(Status.CLOSED, task.getStatus());
    }
}
