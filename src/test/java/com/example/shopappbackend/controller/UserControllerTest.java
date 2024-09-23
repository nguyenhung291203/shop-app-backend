package com.example.shopappbackend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import com.example.shopappbackend.dto.UserLoginDTO;
import com.example.shopappbackend.dto.UserRegisterDTO;
import com.example.shopappbackend.model.Role;
import com.example.shopappbackend.model.User;
import com.example.shopappbackend.response.ResponseApi;
import com.example.shopappbackend.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Kích hoạt profile test
public class UserControllerTest {
    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    private UserRegisterDTO userRegisterDTO;
    private User userResponse;
    private UserLoginDTO userLoginDTO;

    @BeforeEach
    void setup() {
        userLoginDTO = UserLoginDTO.builder()
                .phoneNumber("0869885512")
                .password("123456")
                .build();
        userRegisterDTO = UserRegisterDTO.builder()
                .fullName("Nguyen Van Hung")
                .address("Tân Thành - Vụ Bản - Nam Định")
                .phoneNumber("0869885517")
                .dateOfBirth(new Date())
                .password("123456")
                .retypePassword("123456")
                .roleId(1L)
                .build();

        userResponse = User.builder()
                .fullName("Nguyen Van Hung")
                .address("Tân Thành - Vụ Bản - Nam Định")
                .phoneNumber("0869885517")
                .role(new Role(1L, "ROLE_USER"))
                .id(1L)
                .facebookAccountId(null)
                .googleAccountId(null)
                .build();
    }

    @Test
    void register_validRequest_success() throws Exception {
        when(userService.register(ArgumentMatchers.any())).thenReturn(userResponse);
        var response = userController.register(userRegisterDTO);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ResponseApi responseBody = (ResponseApi) response.getBody();
        assertEquals("Register successfully", responseBody.getMessage());
        assertEquals(userResponse, responseBody.getData());
    }

    @Test
    @WithMockUser(username = "0869885512", password = "123456", authorities = "USER")
    void login_validRequest_success() throws Exception {
        String token = "123456";
        when(userService.login(any())).thenReturn(token);
        var response = userController.login(userLoginDTO);
        ResponseApi responseApi = (ResponseApi) response.getBody();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(token, responseApi.getData());
        assertEquals("Login successfully", responseApi.getMessage());
    }
}
