package com.devteria.identityservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devteria.identityservice.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);

    // Optional tranh loi NullPointerException va no cấp các phương thức tiện ích để xử lý giá trị không tồn tại một cách an toàn và gọn gàng
    // 1 so phuong thuc: isPresent, ifPresent, orElse, orElseGet, orElseThrow
    Optional<User> findByUsername(String username);
}
/*

A) Các phương thức cơ bản từ JpaRepository bao gồm:
S save(S entity): Lưu hoặc cập nhật một entity.
Iterable<S> saveAll(Iterable<S> entities): Lưu tất cả các entity.
        Optional<T> findById(ID id): Tìm một entity theo khóa chính.
boolean existsById(ID id): Kiểm tra sự tồn tại của entity theo khóa chính.
Iterable<T> findAll(): Lấy tất cả các entity.
        Iterable<T> findAllById(Iterable<ID> ids): Lấy tất cả các entity theo danh sách khóa chính.
long count(): Đếm số lượng các entity.
void deleteById(ID id): Xóa entity theo khóa chính.
void delete(T entity): Xóa một entity.
void deleteAll(Iterable<? extends T> entities): Xóa tất cả các entity.
void deleteAll(): Xóa tất cả các entity.

Paging and Sorting Methods:

Page<T> findAll(Pageable pageable): Lấy một trang các entity.
List<T> findAll(Sort sort): Lấy tất cả các entity với sắp xếp.

B) Phương thức tùy chỉnh:

Find By:

Optional<T> findByProperty(String property): Tìm một entity theo thuộc tính.
List<T> findByPropertyLike(String property): Tìm danh sách entity theo thuộc tính với điều kiện LIKE.
Exists:

boolean existsByProperty(String property): Kiểm tra sự tồn tại của entity theo thuộc tính.
Count:

long countByProperty(String property): Đếm số lượng entity theo thuộc tính.
Delete:

void deleteByProperty(String property): Xóa các entity theo thuộc tính.
Custom Queries:

Bạn có thể sử dụng @Query để định nghĩa các truy vấn JPQL hoặc SQL tùy chỉnh:

@Query("SELECT u FROM User u WHERE u.username = :username")
Optional<User> findByUsername(@Param("username") String username);

Sorting and Paging:

Page<T> findByProperty(String property, Pageable pageable): Tìm các entity theo thuộc tính với phân trang.
List<T> findByProperty(String property, Sort sort): Tìm các entity theo thuộc tính với sắp xếp.


*/