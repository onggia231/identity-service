package com.devteria.identityservice.configuration;

import com.devteria.identityservice.constant.PredefinedRole;
import com.devteria.identityservice.entity.Role;
import com.devteria.identityservice.entity.User;
import com.devteria.identityservice.repository.RoleRepository;
import com.devteria.identityservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor // Chỉ tạo constructor cho các trường final và @NonNull. Các trường không final hoặc không @NonNull sẽ không được bao gồm trong constructor này
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
// level = AccessLevel.PRIVATE tat ca cac truong private, makeFinal = true tat ca cac truong final
@Slf4j
public class ApplicationInitConfig {

    //    @Autowired  // su dung @RequiredArgsConstructor ko can khai @Autowired, su dung @FieldDefaults(level = AccessLevel.PRIVATE ko can khai private
    PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    // ConditionalOnProperty doc file application.yaml va test.properties xem prefix la spring thi xet:
    // neu la value = "spring.datasource.driverClassName" + havingValue = "com.mysql.cj.jdbc.Driver"
    // thi Bean ko duoc khoi tao
    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    // kiem tra xem spring.datasource.driverClassName = com.mysql.cj.jdbc.Driver thi ham applicationRunner duoc hoat dong
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        log.info("Initializing application.....");
        return args -> {
            if (userRepository.findByUsername(ADMIN_USER_NAME).isEmpty()) { // kiem tra xem da co tai khoan ADMIN chua

                // tao 1 Role USER
                roleRepository.save(Role.builder() // su dung builder de bao tri, mo rong, linh hoat (Luu ý: Doi tuong Role khai lombok @Builder)
                        .name(PredefinedRole.USER_ROLE)
                        .description("User role")
                        .build());

                // tao 1 Role Admin
                Role adminRole = roleRepository.save(Role.builder()
                        .name(PredefinedRole.ADMIN_ROLE)
                        .description("Admin role")
                        .build());

                var roles = new HashSet<Role>();
                roles.add(adminRole);

                // tao USER ADMIN
                User user = User.builder()
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it");
            }
            log.info("Application initialization completed .....");
        };
    }
}

/*
Nhiệm Vụ:
    Khởi Tạo Dữ Liệu Mặc Định: thực hiện nhiệm vụ khởi tạo dữ liệu cơ bản trong cơ sở dữ liệu khi ứng dụng khởi động.
    Ví dụ, nó có thể tạo ra các vai trò và người dùng mặc định nếu chưa có trong cơ sở dữ liệu.
Luồng Hoạt Động:
    Kiểm Tra: Khi ứng dụng khởi động, ApplicationRunner sẽ kiểm tra xem người dùng admin đã tồn tại chưa.
    Tạo Vai Trò: Nếu không có người dùng admin, nó sẽ tạo vai trò mới (user và admin).
    Tạo Người Dùng: Tạo một người dùng admin với mật khẩu mã hóa.
    Lưu Dữ Liệu: Lưu các vai trò và người dùng vào cơ sở dữ liệu.
*/