package com.devteria.identityservice.mapper;

import org.mapstruct.Mapper;

import com.devteria.identityservice.dto.request.PermissionRequest;
import com.devteria.identityservice.dto.response.PermissionResponse;
import com.devteria.identityservice.entity.Permission;

@Mapper(componentModel = "spring") // triển khai của mapper này sẽ được quản lý bởi Spring
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request); // chuyển đổi một đối tượng PermissionRequest thành một đối tượng Permission.

    PermissionResponse toPermissionResponse(Permission permission); // chuyển đổi một đối tượng Permission thành một đối tượng PermissionResponse.
}
