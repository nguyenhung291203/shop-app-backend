package com.example.shopappbackend.service;

import com.example.shopappbackend.dto.UserRegisterDTO;
import com.example.shopappbackend.exception.BadRequestException;
import com.example.shopappbackend.exception.DataIntegrityViolationException;
import com.example.shopappbackend.exception.NotFoundException;
import com.example.shopappbackend.model.Role;
import com.example.shopappbackend.model.User;
import com.example.shopappbackend.repository.RoleRepository;
import com.example.shopappbackend.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class UserServiceTest {
    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;


    UserRegisterDTO userRegisterDTO;
    User userResponse;

    @BeforeEach
    void setup() {
        userRegisterDTO = UserRegisterDTO.builder().fullName("Nguyen Van Hung").address("Tân Thành - Vụ Bản - Nam Định").phoneNumber("0869885517").dateOfBirth(new Date()).password("123456").retypePassword("123456").roleId(1L).build();

        userResponse = User.builder().fullName("Nguyen Van Hung").address("Tân Thành - Vụ Bản - Nam Định").phoneNumber("0869885517").role(new Role(1L, "ROLE_USER")).id(1L).facebookAccountId(null).googleAccountId(null).build();


    }

    @Test
    void register_validRequest_success() {
        when(userRepository.existsUserByPhoneNumber(anyString())).thenReturn(false);

        when(roleRepository.findById(userRegisterDTO.getRoleId())).thenReturn(java.util.Optional.of(new Role(1L, "ROLE_USER")));

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        when(userRepository.save(ArgumentMatchers.any())).thenReturn(userResponse);

        var response = userService.register(userRegisterDTO);

        Assertions.assertThat(response.getId()).isEqualTo(1L);
        Assertions.assertThat(response.getFullName()).isEqualTo(userRegisterDTO.getFullName());
        Assertions.assertThat(response.getAddress()).isEqualTo(userRegisterDTO.getAddress());
    }


    @Test
    void register_phoneNumberExist_fail() {
        when(userRepository.existsUserByPhoneNumber(anyString())).thenReturn(true);
        var ex = assertThrows(DataIntegrityViolationException.class, () -> userService.register(userRegisterDTO));
        Assertions.assertThat(ex.getMessage()).isEqualTo("Phone number already exists");
    }

    @Test
    void register_validPassword_fail() {
        userRegisterDTO.setPassword("123456890");
        var ex = assertThrows(BadRequestException.class, () -> userService.register(userRegisterDTO));
        Assertions.assertThat(ex.getMessage()).isEqualTo("Invalid password");
    }

    @Test
    void register_notFoundRole_fail() {
        userRegisterDTO.setRoleId(10L);
        var ex = assertThrows(NotFoundException.class, () -> userService.register(userRegisterDTO));
        Assertions.assertThat(ex.getMessage()).isEqualTo("Can not found");
    }
}
