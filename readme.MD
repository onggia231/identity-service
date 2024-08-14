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

Hoac muon bo test thi chay lenh duoi
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
    - Luu y bien moi truong khi deploy phai khai ro ra moi khong bi loi

## Build Docker image

Đầu tiên config file Dockerfile rồi mới chạy lệnh dưới 

### Build project
docker build -t <account>/identity-service:0.0.1 . (tên account trên docker destop - namngoc231)
### Run project
docker run -d <account>/identity-service:0.0.1

## Docker guideline
### Create network:
`docker network create devteria-network` (co the doi ten khac namngoc-network - tạo 1 network để kết nối đến docker)
### Start MySQL in devteria-network ( run Sql docker với network đã tạo)

`docker run --network devteria-network --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -d mysql:8.0.36-debian`
- Kiem tra xem mysql da create database chua
### Run your application in devteria-network
`docker run --name identity-service --network devteria-network -p 8080:8080 -e DBMS_CONNECTION=jdbc:mysql://mysql:3306/identity_service <account>/identity-service:0.0.1`

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


### Dang ky EC2 va login Termius bang key va host cua Ec2
1. Link EC2: https://ap-southeast-2.console.aws.amazon.com/ec2/home?region=ap-southeast-2#Home:
2. Chọn địa điểm gần mình ở góc phải màn hình: Vd: Tokyo
3. Click Launch instance -> Name (devteria) -> Quick Start (ubuntu) -> Instance type (t2.micro Free tier eligible - Ko ton phi)
   -> Key pair (login)  (Neu chua co click Create new key pair (Key pair name: aws) de tao key - Sd de login vao Termius ) Dang de folder: D:\Setting Environment\Key pair
   -> Configure storage (1x15Gib)
4. Quay lai tab Instances de check xem cai vua tao
5. Muon Stop hay xoa may ao thi vao Instance ID -> Instance state (Stop instance: Dung may ao lai, Terminate instance: Xoa may ao)
6. Vao Termius -> Keychain -> SSH Key -> Import from key file (D:\Setting Environment\Key pair)
7. Host ->  Create host -> Address (Paste Public IPv4 address hoac Public IPv4 DNS trong muc Instance EC2 aws) -> Connect
   -> Dien Username (ubuntu) -> Continue & Save -> Add % continue -> Chon key aws.pem -> Continue & Save

### Tu EC2 voi MySql cai tren Ubuntu qua Termius se Expose ra ngoai de MySql Workbench ket noi duoc
1. Instances -> Security -> Security groups -> Inbound rules (Port range: 22) -> Edit inbound rules -> Add rule -> Custom TCP (3306 trung port ma run MySql)
   -> CIDR blocks (Text field canh Custom - Chon: 0.0.0.0/0) -> Save rules
2. Lay Public IPv4 address (Instances) vao MySql Workbench ket noi den
3. Vao MySql Workbench tao database: create database identity_service;

### Lay code da push len docker hub: https://hub.docker.com/
1. Vao docker hub -> Chon service da tao -> Public View -> Chon tab Tags
   -> Copy cau lenh pull -> Chay sudo docker pull namngoc231/identity-service:0.0.1 tren Termius
2. Chay Termius tiep: sudo docker run --name identity-service --network devteria-network -p 8080:8080 -e DBMS_CONNECTION=jdbc:mysql://mysql:3306/identity_service namngoc231/identity-service:0.0.1

### Sau khi setup xong muon chay
1. EC2 -> Instances -> Security -> Security groups -> Inbound rules -> Edit inbound rules -> Add rule -> TCP (8080 giong config tren), CIDR blocks (Chon: 0.0.0.0/0)
2. Mo Postman test: http://52.68.55.32:8080/identity/auth/token (Thay local host = Public IPv4 address)
3. Ctrl+C : stop service
4. sudo docker ps -a: kiem tra service
5. sudo docker start NAMES( vd: identity-service): de start lai docker theo NAMES hoac theo CONTAINER ID
6. Luu y: Khong dung nen vao EC2 -> Instance state (Stop instance: Dung may ao lai, Terminate instance: Xoa may ao)

### Cai moi truong tren Ubuntu
1. Cai Docker: Chay lan luot cac lenh duoi (ctrl+shift+v de dan)
# Add Docker's official GPG key:
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc
# Add the repository to Apt sources:
echo \
"deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
$(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt-get update

sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

sudo docker run hello-world
2. Cai Mysql
   sudo docker network create devteria-network
   sudo docker run --network devteria-network --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -d mysql:8.0.36-debian
   sudo docker ps -a (De xem list docker dang chay)
   sudo docker rm <name>

### Kiem tra bo nho may ao
1. Chay ubuntu lenh: htop (Xem CPU, Mem, Swp) - bam ctrl+c de thoat

