package com.devteria.identityservice.dto.response;

import java.time.LocalDate;
import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
//    String password; // khong ai tra password ve nen ko can nua
    String firstName;
    String lastName;
    LocalDate dob;
    Set<String> roles;
//    Set<RoleResponse> roles;
}
