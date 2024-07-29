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

## Cài MySQL

Câu lệnh để tải MySQL từ Docker:
docker pull mysql:8.0.36-debian

Chạy MySQL với root password là: root
docker run --name mysql-8.0.36 -p 13306:13306 -e MYSQL_USER=namngoc -e MYSQL_ROOT_PASSWORD=namngoc -d mysql:8.0.36-debian

## Deploy SpringBoot

Cách 1: Cài maven trên máy rồi chạy lệnh

Vào project D:\COURSE\SpringBoot\SpringBoot Devteria\identity-service>

Mở Terminal rồi chạy lệnh (Thêm ./ hoặc không thêm ./ xem cái nào chạy được thì dùng)

./mvnw package (chạy lệnh build)

./mvnw package -DskipTests (bỏ test)

-> cd .\target\
-> dir ( xem có file jar chưa)

Vào thư mục chứa file jar đã build rồi chạy Terminal:
    java -jar .\identity-service-0.0.1-SNAPSHOT.jar
    ( java -jar .\FILE_JAR )


Cách 2: Vào tool Intellij chạy Lifecycle: package

Lưu ý: 
    - File build đang chạy muốn thoát bấm Ctrl + c
    - Cần cài java
    - Chạy Terminal: mvn spotless:apply nếu muốn format code trước khi buid để tránh lỗi


## Docker guideline
### Create network:
`docker network create devteria-network`
### Start MySQL in devteria-network
`docker run --network devteria-network --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -d mysql:8.0.36-debian`
### Run your application in devteria-network
`docker run --name identity-service --network devteria-network -p 8080:8080 -e DBMS_CONNECTION=jdbc:mysql://mysql:3306/identity_service identity-service:0.9.0`


## Build Docker image

