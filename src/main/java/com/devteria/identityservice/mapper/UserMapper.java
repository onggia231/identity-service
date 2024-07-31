package com.devteria.identityservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.devteria.identityservice.dto.request.UserCreationRequest;
import com.devteria.identityservice.dto.request.UserUpdateRequest;
import com.devteria.identityservice.dto.response.UserResponse;
import com.devteria.identityservice.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true) // vi List<String> roles; nen can ignore
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
    // Phương thức này cập nhật một đối tượng User hiện có từ thông tin trong UserUpdateRequest.
    // @MappingTarget User user: Chỉ định rằng User là đối tượng sẽ bị cập nhật
    // @Mapping(target = "roles", ignore = true): Chỉ định rằng thuộc tính roles của đối tượng User sẽ bị bỏ qua trong
    // quá trình cập nhật.
    // Điều này có thể được sử dụng để tránh thay đổi thuộc tính roles khi cập nhật người dùng từ UserUpdateRequest
}
