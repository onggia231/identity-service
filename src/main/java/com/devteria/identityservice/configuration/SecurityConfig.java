package com.devteria.identityservice.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
// Kích hoạt bảo mật web toàn diện, cấu hình các quyền truy cập và xác thực người dùng trên toàn bộ ứng dụng web.
@EnableWebSecurity
// Kích hoạt bảo mật phương thức cho phép kiểm soát quyền truy cập ở mức phương thức cụ thể, sử dụng các chú thích như @PreAuthorize và @Secured.
@EnableMethodSecurity

public class SecurityConfig {

    // endpoint khong can authentication
    private static final String[] PUBLIC_ENDPOINTS = {
            "/users", "/auth/token", "/auth/introspect", "/auth/logout", "/auth/refresh"
    };

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // Cấu hình quyền truy cập
        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS)
                .permitAll() // tat ca request PUBLIC_ENDPOINTS ko can xac thuc
                .anyRequest() // anyRequest().authenticated() con lai tat ca yeu cau khac can phai xac thuc
                .authenticated());

        // Cấu hình bảo mật OAuth2
        // Khi thuc hien 1 request can khai them token
        httpSecurity.oauth2ResourceServer(
                oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                                //  decoder(customJwtDecoder) cung cấp một JwtDecoder tùy chỉnh để giải mã các JWT.
                                //  Điều này cho phép bạn kiểm tra tính hợp lệ của token và giải mã nó.
                                .decoder(customJwtDecoder)
                                // Cung cấp một JwtAuthenticationConverter tùy chỉnh để chuyển đổi từ JWT thành đối tượng Authentication mà Spring Security sử dụng
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        //  xử lý các lỗi xác thực, chẳng hạn như khi token không hợp lệ hoặc không có token, bằng cách trả về mã lỗi HTTP 401 (Unauthorized)
                        .authenticationEntryPoint(
                                new JwtAuthenticationEntryPoint())
        );

        // Tắt bảo vệ CSRF
        // Tắt bảo vệ chống Cross-Site Request Forgery (CSRF)
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("*"); //  Cho phép tất cả các nguồn (origins) truy cập vào tài nguyên của ứng dụng
        corsConfiguration.addAllowedMethod("*"); // Cho phép tất cả các phương thức HTTP như GET, POST, PUT, DELETE
        corsConfiguration.addAllowedHeader("*"); // Cho phép tất cả các tiêu đề HTTP trong yêu cầu

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        // registerCorsConfiguration("/**", corsConfiguration) Điều này có nghĩa là cấu hình CORS được áp dụng cho tất cả các yêu cầu đến ứng dụng
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    // Custom lai dinh dang cua jwt khong cho tien to mac dinh
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        // chuyển đổi các quyền từ JWT thành đối tượng GrantedAuthority của Spring Security.
        // GrantedAuthority là một interface đại diện cho các quyền của người dùng
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // Thiết lập tiền tố (prefix) cho các quyền được lấy từ JWT.
        // Mặc định, Spring Security thường thêm tiền tố ROLE_ trước mỗi quyền (như ROLE_USER).
        // Ở đây, tiền tố được đặt là một chuỗi rỗng, có nghĩa là không thêm tiền tố nào vào các quyền
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        // dùng để chuyển đổi JWT thành đối tượng Authentication của Spring Security.
        // Đối tượng Authentication là một đại diện cho thông tin xác thực của người dùng và quyền của họ trong hệ thống
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    // được sử dụng để mã hóa (encode) và xác thực (verify) mật khẩu người dùng
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

}

/*
Nhiệm Vụ:
    Cấu Hình Bảo Mật: SecurityConfig cấu hình các quy tắc bảo mật cho ứng dụng, bao gồm việc phân quyền truy cập và cấu hình OAuth2.
Luồng Hoạt Động:
    Cấu Hình Quy Tắc Truy Cập: Định nghĩa các quy tắc phân quyền, cho phép hoặc từ chối truy cập các endpoint dựa trên phương thức và URL.
    Cấu Hình OAuth2: Đặt cấu hình cho OAuth2 Resource Server để sử dụng JWT và xử lý các lỗi xác thực.
    Tắt CSRF: Tắt bảo vệ CSRF (Cross-Site Request Forgery) nếu không cần thiết.
*/