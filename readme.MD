**# Identity service
This microservice is responsible for:
* Onboarding users
* Roles and permissions
* Authentication

## Tech stack
* Build tool: maven >= 3.9.5
* Java: 21
* Framework: Spring boot 3.2.x
* DBMS: MySQL

## Prerequisites
* Java SDK 21
* A MySQL server

## Start application
`mvn spring-boot:run`

## Build application
`mvn clean package`

## Deploy SpringBoot

Cách 1: Cài maven trên máy rồi chạy lệnh

Vào project D:\COURSE\SpringBoot\SpringBoot Devteria\identity-service>

Mở Terminal rồi chạy lệnh (Thêm ./ hoặc không thêm ./ xem cái nào chạy được thì dùng)

./mvnw package (chạy lệnh build)

./mvnw package -DskipTests (bỏ test)

-> cd .\target\
-> ls ( xem có file jar chưa)

Vào thư mục chứa file jar đã build rồi chạy Terminal:
    java -jar .\identity-service-0.0.1-SNAPSHOT.jar
    ( java -jar .\FILE_JAR )


Cách 2: Vào tool Intellij chạy Lifecycle: package

Lưu ý: 
    - File build đang chạy muốn thoát bấm Ctrl + c
    - Cần cài java
    - Chạy Terminal: mvn splotless:apply nếu muốn format code trước khi buid để tránh lỗi