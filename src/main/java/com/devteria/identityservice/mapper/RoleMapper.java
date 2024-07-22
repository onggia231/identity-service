package com.devteria.identityservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.devteria.identityservice.dto.request.RoleRequest;
import com.devteria.identityservice.dto.response.RoleResponse;
import com.devteria.identityservice.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    // Chỉ định rằng thuộc tính permissions của đối tượng Role sẽ bị bỏ qua trong quá trình chuyển đổi.
    // Điều này có thể hữu ích nếu bạn không muốn hoặc không cần ánh xạ thuộc tính này từ RoleRequest sang Role.
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
