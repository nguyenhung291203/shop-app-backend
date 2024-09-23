package com.example.shopappbackend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.shopappbackend.dto.RoleDTO;
import com.example.shopappbackend.exception.DataIntegrityViolationException;
import com.example.shopappbackend.exception.NotFoundException;
import com.example.shopappbackend.model.Role;
import com.example.shopappbackend.repository.RoleRepository;
import com.example.shopappbackend.service.RoleService;
import com.example.shopappbackend.utils.LocalizationUtil;
import com.example.shopappbackend.utils.MessageKey;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final LocalizationUtil localizationUtil;

    @Override
    public Role insertRole(RoleDTO roleDTO) {
        if (this.roleRepository.existsByName(roleDTO.getName()))
            throw new DataIntegrityViolationException(
                    localizationUtil.getLocaleResolver(MessageKey.ROLE_ALREADY_EXIST));
        return this.roleRepository.save(Role.builder().name(roleDTO.getName()).build());
    }

    @Override
    public List<Role> getAllRoles() {
        return this.roleRepository.findAll();
    }

    @Override
    public Role updateRole(Long id, RoleDTO roleDTO) {
        Role role = this.roleRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(
                        localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " role id: " + id)));
        if (this.roleRepository.existsByName(roleDTO.getName()))
            throw new DataIntegrityViolationException(
                    localizationUtil.getLocaleResolver(MessageKey.ROLE_ALREADY_EXIST));
        role.setName(role.getName());
        return this.roleRepository.save(role);
    }

    @Override
    public void deleteRoleById(Long id) {
        this.roleRepository.deleteById(id);
    }
}
