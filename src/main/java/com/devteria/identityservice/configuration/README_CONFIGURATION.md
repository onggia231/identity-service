Khởi Đầu Ứng Dụng:

    ApplicationInitConfig chạy để đảm bảo dữ liệu cơ bản như người dùng admin và vai trò đã được khởi tạo.

Xử Lý Yêu Cầu HTTP:

    Khi có yêu cầu đến, SecurityConfig kiểm tra quy tắc phân quyền và cấu hình bảo mật.
    CustomJwtDecoder giải mã JWT để kiểm tra và xác thực token.
    Nếu có lỗi xác thực, JwtAuthenticationEntryPoint sẽ trả về thông điệp lỗi HTTP 401.

Quản Lý CORS:

    Cấu hình CORS sẽ được áp dụng nếu có yêu cầu từ các nguồn khác nhau.