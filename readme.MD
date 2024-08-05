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
docker run --name mysql-8.0.36 -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -d mysql:8.0.36-debian

## Deploy SpringBoot

Cách 1: Cài maven trên máy rồi chạy lệnh

Vào project D:\COURSE\SpringBoot\SpringBoot Devteria\identity-service>

Mở Terminal rồi chạy lệnh (Thêm ./ hoặc không thêm ./ xem cái nào chạy được thì dùng)

./mvnw package (chạy lệnh build)  | mvn package

./mvnw package -DskipTests (bỏ test) | mvn package -DskipTests

-> cd .\target\
-> dir ( xem có file jar chưa)

Vào thư mục chứa file jar đã build rồi chạy Terminal:
    java -jar .\identity-service-0.0.1-SNAPSHOT.jar

Cách 2: Vào tool Intellij chạy Lifecycle: package

Lưu ý: 
    - File build đang chạy muốn thoát bấm Ctrl + c
    - Cần cài môi trường java để chạy file jar
    - Chạy Terminal: mvn spotless:apply nếu muốn format code trước khi buid để tránh lỗi

## Build Docker image
### Build project
docker build -t <account>/identity-service:0.0.1 . (tên account trên docker destop - namngoc231)
### Run project
docker run -d identity-service:0.0.1

## Docker guideline
### Create network:
`docker network create devteria-network` (co the doi ten khac namngoc-network - tạo 1 network để kết nối đến docker)
### Start MySQL in devteria-network ( run Sql docker với network đã tạo)
`docker run --network devteria-network --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -d mysql:8.0.36-debian`
### Run your application in devteria-network
`docker run --name identity-service --network devteria-network -p 8080:8080 -e DBMS_CONNECTION=jdbc:mysql://mysql:3306/identity_service identity-service:0.0.1`

# Publish Docker image lên Docker Hub
Đăng ký tài khoản trên docker hub rồi login trên docker desktop
Chạy lệnh để Build Docker image (docker build -t <account>/identity-service:0.0.1 .)
Chạy: docker image push <account>/identity-service:0.0.1 (de push len docker hub)
Kiem tra sau khi push:
 Vao web https://hub.docker.com/ -> Repositories (Xem image da push len)
Chon Public View -> Docker Pull Command de tai (docker pull namngoc231/identity-service)
 -> Chay cmd: docker pull namngoc231/identity-service:0.0.1 (kem them version)
Chay lenh run: docker run --name identity-service --network devteria-network -p 8080:8080 -e DBMS_CONNECTION=jdbc:mysql://mysql:3306/identity_service namngoc231/identity-service:0.0.1


## Các lệnh docker
docker image ls (xem image dang quan ly)
docker image rm identity-service:0.0.1 (Xoa REPOSITORY:TAG)

