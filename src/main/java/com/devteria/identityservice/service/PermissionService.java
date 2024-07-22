package com.devteria.identityservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devteria.identityservice.dto.request.PermissionRequest;
import com.devteria.identityservice.dto.response.PermissionResponse;
import com.devteria.identityservice.entity.Permission;
import com.devteria.identityservice.mapper.PermissionMapper;
import com.devteria.identityservice.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request); // convert request -> entity
        permission = permissionRepository.save(permission); // save voi doi tuong entity
        return permissionMapper.toPermissionResponse(permission); // return convert entity -> response
    }

    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }
}
