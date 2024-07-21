package com.devteria.identityservice.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data // Tạo ra các phương thức getter, setter, toString(), equals(), và hashCode()
@Builder // Tạo ra một Builder pattern cho class
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // Loại bỏ các trường có giá trị null khi serialize JSON
public class ApiResponse<T> {
    @Builder.Default // chỉ định giá trị mặc định cho trường code có thể được ghi đè bởi builder
    private int code = 1000;

    private String message;
    private T result;
}
