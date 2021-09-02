package com.cardinality.taskmanager.repository;

import com.cardinality.taskmanager.entity.User;
import com.cardinality.taskmanager.entity.User.Role;
import javax.management.relation.RelationNotFoundException;
import javax.persistence.UniqueConstraint;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void injectedComponentsAreNotNull(){
        Assertions.assertNotNull(userRepository);
    }
    @Test
    public void testUniqueUserName(){
        User user1 = new User();
        user1.setUserName("userTest");
        user1.setPassword("87654321");
        user1.setRole(Role.USER);
        user1.setFullName("User 1");

        userRepository.save(user1);
        User user2 = new User();
        user2.setUserName("userTest");
        user1.setPassword("87654321");
        user2.setRole(Role.USER);
        user2.setFullName("User 2");

        Assertions.assertThrows(ConstraintViolationException.class,()->userRepository.save(user2));
    }

}
