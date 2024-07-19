package com.devteria.identityservice.configuration;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.devteria.identityservice.dto.request.ApiResponse;
import com.devteria.identityservice.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // AuthenticationEntryPoint xử lý các tình huống khi một người dùng cố gắng truy cập tài nguyên được bảo vệ mà chưa được xác thực.
    // => Phương thức này được gọi khi một yêu cầu không được xác thực truy cập vào tài nguyên được bảo vệ
    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
/*
Nhiệm Vụ:
    Xử Lý Lỗi 401: JwtAuthenticationEntryPoint là một bộ xử lý lỗi được kích hoạt khi người dùng không được xác thực.
    Nó trả về mã lỗi HTTP 401 cùng với thông điệp lỗi JSON.
Luồng Hoạt Động:
    Nhận Lỗi Xác Thực: Khi người dùng không được xác thực, JwtAuthenticationEntryPoint được kích hoạt.
    Thiết Lập Phản Hồi: Cài đặt mã trạng thái HTTP và tiêu đề của phản hồi.
    Gửi Phản Hồi: Viết thông điệp lỗi vào phản hồi và gửi lại cho client.
*/