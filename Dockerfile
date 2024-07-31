# Stage 1: build - Sử dụng Maven để biên dịch và đóng gói ứng dụng, đảm bảo mã nguồn được định dạng đúng bằng Spotless.
# Start with a Maven image that includes JDK 17
# Sử dụng image Maven có sẵn JDK 17 từ Amazon Corretto.
# Đặt tên cho giai đoạn này là build.
FROM maven:3.9.8-amazoncorretto-17 AS build

# Copy source code and pom.xml file to /app folder
# Thiết lập thư mục làm việc là /app.
# Sao chép file pom.xml và thư mục src của mã nguồn vào thư mục /app trong container.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Apply Spotless formatting: Chạy Maven plugin Spotless để định dạng mã nguồn theo quy chuẩn.
RUN mvn spotless:apply

# Build source code with maven
# Chạy lệnh mvn package để đóng gói ứng dụng, bỏ qua việc chạy các bài kiểm thử
RUN mvn package -DskipTests

#Stage 2: create image (Deploy) - Sử dụng JDK 17 của Amazon Corretto để chạy ứng dụng đã được đóng gói trong file .jar.
# Start with Amazon Correto JDK 17
# Sử dụng image Amazon Corretto JDK 17 cho giai đoạn triển khai
FROM amazoncorretto:17.0.4

# Set working folder to App and copy complied file from above step
# Thiết lập thư mục làm việc là /app.
# Sao chép file .jar đã được đóng gói từ giai đoạn build vào thư mục /app và đặt tên là app.jar.
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Command to run the application
# Định nghĩa lệnh để chạy ứng dụng khi container khởi động.
# Ở đây, nó sẽ chạy lệnh java -jar app.jar để khởi động ứng dụng Java.
ENTRYPOINT ["java", "-jar", "app.jar"]

# Chạy cmd:  docker build -t identity-service:0.0.1 .
# Từ những config ở trên khi buid sẽ tạo 1 Images trên docker
# Run docker: docker run -d identity-service:0.0.1