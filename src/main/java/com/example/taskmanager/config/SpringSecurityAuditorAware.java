package com.example.taskmanager.config;

import com.example.taskmanager.util.TaskManConstants;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

/** Implementation of {@link AuditorAware} based on Spring Security. */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SecurityUtils.getCurrentUserLogin().orElse(TaskManConstants.SYSTEM));
    }
}
