package com.example.shopappbackend.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.shopappbackend.dto.RoleDTO;
import com.example.shopappbackend.response.ResponseApi;
import com.example.shopappbackend.service.RoleService;
import com.example.shopappbackend.utils.LocalizationUtil;
import com.example.shopappbackend.utils.MessageKey;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/roles")
@Validated
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    private final LocalizationUtil localizationUtil;

    @GetMapping()
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(ResponseApi.builder()
                .data(roleService.getAllRoles())
                .message(localizationUtil.getLocaleResolver(MessageKey.ROLE_GET_SUCCESSFULLY))
                .build());
    }

    @PostMapping()
    public ResponseEntity<?> insertRole(@Valid @RequestBody RoleDTO roleDTO) {
        return new ResponseEntity<>(
                ResponseApi.builder()
                        .message(localizationUtil.getLocaleResolver(MessageKey.ROLE_INSERT_SUCCESSFULLY))
                        .data(roleService.insertRole(roleDTO))
                        .build(),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoleById(@Valid @PathVariable Long id) {
        this.roleService.deleteRoleById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ResponseApi.builder()
                        .data(null)
                        .message(localizationUtil.getLocaleResolver(MessageKey.ROLE_DELETE_SUCCESSFULLY))
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoleById(@Valid @PathVariable Long id, @RequestBody RoleDTO roleDTO) {
        return new ResponseEntity<>(
                ResponseApi.builder()
                        .data(roleService.updateRole(id, roleDTO))
                        .message(localizationUtil.getLocaleResolver(MessageKey.ROLE_UPDATE_SUCCESSFULLY))
                        .build(),
                HttpStatus.OK);
    }
}
