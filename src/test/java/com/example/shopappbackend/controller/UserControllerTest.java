package com.example.shopappbackend.controller;

import com.example.shopappbackend.dto.UserRegisterDTO;
import com.example.shopappbackend.model.Role;
import com.example.shopappbackend.model.User;
import com.example.shopappbackend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class UserControllerTest {
    @MockBean
    private UserService userService;
    @MockBean
    private ObjectMapper objectMapper;
    @MockBean
    private UserController userController;
    UserRegisterDTO userRegisterDTO;
    User userResponse;
    @MockBean
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        userRegisterDTO = UserRegisterDTO.builder().fullName("Nguyen Van Hung").address("Tân Thành - Vụ Bản - Nam Định").phoneNumber("0869885517").dateOfBirth(new Date()).password("123456").retypePassword("123456").roleId(1L).build();
        userResponse = User.builder().fullName("Nguyen Van Hung").address("Tân Thành - Vụ Bản - Nam Định").phoneNumber("0869885517").role(new Role(1L, "ROLE_USER")).id(1L).facebookAccountId(null).googleAccountId(null).build();
    }

    @Test
    void register_validRequest_success() throws Exception {
        when(userService.register(ArgumentMatchers.any())).thenReturn(userResponse);
        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDTO))
        );
    }
}
