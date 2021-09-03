package com.cardinality.taskmanager.config;

import com.cardinality.taskmanager.repository.UserRepository;
import javax.annotation.PostConstruct;
import javax.validation.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InitializeData {

    @Autowired
    private DataSource dataSource;
    @Autowired
    UserRepository userRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void loadData() {
        if(userRepository.findFirstUser()==null){
            ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(true, true, "UTF-8", new ClassPathResource("init-data.sql"));
            resourceDatabasePopulator.execute(dataSource);

        }
    }
}
