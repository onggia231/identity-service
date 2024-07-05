package com.devteria.identityservice.configuration;

import java.util.HashSet;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.devteria.identityservice.entity.User;
import com.devteria.identityservice.enums.Role;
import com.devteria.identityservice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    //    @Autowired  // su dung @RequiredArgsConstructo va FieldDefaults ko can  @Autowired
    private PasswordEncoder passwordEncoder;

    // ConditionalOnProperty doc file application.yaml va test.properties xem prefix la spring thi xet:
    // neu la value = "spring.datasource.driverClassName" + havingValue = "com.mysql.cj.jdbc.Driver"
    // thi Bean ko duoc khoi tao
    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "spring.datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        log.info("Init application...");
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                var roles = new HashSet<String>();
                roles.add(Role.ADMIN.name());
                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        //                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it");
            }
        };
    }
}
