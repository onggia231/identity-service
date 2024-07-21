package com.devteria.identityservice.entity;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    // tao Column unique thi khi co nhieu request tao user cung 1 luc se khong tao trung lap
    // COLLATE utf8mb4_unicode_ci phan biet chu hoa chu thuong tao ngoc hay Ngoc dong thoi se ko duoc
    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String username;

    String password;
    String firstName;
    LocalDate dob; // chi co ngay thang nam
    String lastName;

    @ManyToMany
    Set<Role> roles;
}
