package com.devteria.identityservice.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import com.devteria.identityservice.dto.request.UserCreationRequest;
import com.devteria.identityservice.dto.response.UserResponse;
import com.devteria.identityservice.entity.User;
import com.devteria.identityservice.exception.AppException;
import com.devteria.identityservice.repository.UserRepository;

@SpringBootTest
// @TestPropertySource("/test.properties")
public class UserServiceTest {

    //    @AutoConfigureMockMvc // tao request toi controller
    //    @TestPropertySource("/test.properties")
    //// @TestPropertySource("/test.properties") khai bao file test.properties de khi test no khong phu thuoc vao db
    // local
    //// ApplicationInitConfig config

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private UserCreationRequest request;
    private UserResponse userResponse;
    private User user;
    private LocalDate dob;

    @BeforeEach
    void initData() {
        dob = LocalDate.of(1990, 1, 1);

        request = UserCreationRequest.builder()
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .password("12345678")
                .dob(dob)
                .build();

        userResponse = UserResponse.builder()
                .id("cf0600f538b3")
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .dob(dob)
                .build();

        user = User.builder()
                .id("cf0600f538b3")
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest_success() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        // WHEN
        var response = userService.createUser(request);
        // THEN

        Assertions.assertThat(response.getId()).isEqualTo("cf0600f538b3");
        Assertions.assertThat(response.getUsername()).isEqualTo("john");
    }

    //    @Test
    //    void createUser_userExisted_fail() {
    //        // GIVEN
    //        when(userRepository.existsByUsername(anyString())).thenReturn(true);
    //
    //        // WHEN
    //        var exception = assertThrows(AppException.class, () -> userService.createUser(request));
    //
    //        // THEN
    //        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1002);
    //    }

    //    @Test
    //    @WithMockUser(username = "namngoc")
    //    void getMyInfo_valid_success() {
    //        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    //
    //        var response = userService.getMyInfo();
    //
    //        Assertions.assertThat(response.getUsername()).isEqualTo("namngoc");
    //        Assertions.assertThat(response.getId()).isEqualTo("cf0600f538b3");
    //    }

    @Test
    @WithMockUser(username = "john")
    void getMyInfo_userNotFound_error() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(null));

        // WHEN
        var exception = assertThrows(AppException.class, () -> userService.getMyInfo());

        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1005);
    }
}
