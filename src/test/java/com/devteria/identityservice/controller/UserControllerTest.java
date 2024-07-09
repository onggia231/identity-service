package com.devteria.identityservice.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.devteria.identityservice.dto.request.UserCreationRequest;
import com.devteria.identityservice.dto.response.UserResponse;
import com.devteria.identityservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
// @TestPropertySource("/test.properties")
class UserControllerTest {

    //    @AutoConfigureMockMvc // tao request toi controller
    //    @TestPropertySource("/test.properties")
    //// @TestPropertySource("/test.properties") khai bao file test.properties de khi test no khong phu thuoc vao db
    // local
    //// ApplicationInitConfig config

    @Autowired
    private MockMvc mockMvc; // goi den api cua chung ta

    @MockBean
    private UserService userService;

    private UserCreationRequest request;
    private UserResponse userResponse;
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
    }

    @Test
    // test truong hop request success
    void createUser_validRequest_success() throws Exception {
        // GIVEN - nhung du lieu dau vao da biet truoc va minh du doan no xay ra nhu vay
        // request va userResponse chinh la GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        // Mockito.when dung khi userService goi den ham createUser no se khong goi den ham createUser ma tra truc tiep
        // ve userResponse
        // -> test Controller ham createUser se ko goi den ham trong Service (Test doc lap tung thang)
        Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(userResponse);

        // WHEN, THEN
        // WHEN: Khi nao request api
        // THEN: Khi request xay ra expect dieu gi
        // mockMvc.perform tao request
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                // day la bat dau xu ly THEN expect
                .andExpect(MockMvcResultMatchers.status().isOk()) // so sanh co tra ve code 200 ko
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000)) // so sanh co tra response 1000 ko
                .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("cf0600f538b3"));
    }

    @Test
    // test truong hop request fail
    void createUser_usernameInvalid_fail() throws Exception {
        // GIVEN
        request.setUsername("joh");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                // So sanh: USERNAME_INVALID(1003, "Username must be at least {min} characters",
                // HttpStatus.BAD_REQUEST),
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1003))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Username must be at least 4 characters"));
    }
}
