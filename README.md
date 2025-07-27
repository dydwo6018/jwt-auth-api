#  Spring Boot JWT 인증/인가 프로젝트

## 1. 프로젝트 개요

이 프로젝트는 JWT(Json Web Token)를 기반으로 사용자 인증 및 인가 기능을 구현한 Spring Boot 백엔드 애플리케이션입니다.  
회원가입, 로그인, 권한 확인, 관리자 권한 부여 등 기본적인 인증 흐름을 포함하고 있으며, Swagger UI를 통해 API 테스트가 가능합니다.

---

## 2. 기술 스택

| 항목 | 내용 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3, Spring Security |
| Build Tool | Gradle |
| Auth | JWT (AccessToken) |
| Infra | AWS EC2 |
| 문서화 | Swagger (springdoc-openapi) |

---

## 3. 실행 방법

```bash
#  로컬에서 실행

# 프로젝트 빌드
./gradlew clean build

# 애플리케이션 실행
java -jar build/libs/jwt-auth-api-0.0.1-SNAPSHOT.jar

# JAR 파일 전송
scp -i ~/Downloads/ec2-key.pem build/libs/jwt-auth-api-0.0.1-SNAPSHOT.jar ubuntu@54.172.207.19:/home/ubuntu/

# EC2 접속
ssh -i ~/Downloads/ec2-key.pem ubuntu@54.172.207.19

# 서버 백그라운드 실행
nohup java -jar jwt-auth-api-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```
## 4. 배포 정보
Swagger UI 주소:
- http://54.172.207.19:8080/swagger-ui/index.html

EC2 API 엔드포인트:
- http://54.172.207.19:8080

## 5. 🧾 API 명세

| 메서드 | URL                                         | 설명                                | 인증 필요 |
|--------|----------------------------------------------|-------------------------------------|-----------|
| POST   | `/api/v1/auth/signup`                       | 회원가입 (username, password)       | ❌ 없음   |
| POST   | `/api/v1/auth/login`                        | 로그인 (JWT access/refresh 발급)   | ❌ 없음   |
| GET    | `/api/v1/auth/users`                        | 전체 사용자 조회                    | ❌ 없음   |
| PATCH  | `/api/v1/auth/admin/users/{username}/roles` | 관리자 권한 부여 (ADMIN으로 변경)   | ✅ 필요   |

---

### 🔐 JWT 인증 방법 (Swagger UI)

- JWT가 필요한 API는 Swagger 우측 상단 `Authorize` 버튼을 클릭해 다음과 같이 입력해야 합니다:



### 6. JWT 인증 방식 요약
- 로그인 시 `AccessToken`이 응답으로 반환됩니다.
- 인증이 필요한 요청은 `Authorization: Bearer {AccessToken}` 헤더가 필요합니다.
- Swagger 상단 Authorize 버튼을 통해 토큰을 입력하고 인증된 API를 테스트할 수 있습니다.

### 7. 디렉토리 구조 

```bash
com.springbootjwtauth
├── config                # 시큐리티 설정
├── controller            # 인증 관련 컨트롤러
├── dto                   # 요청/응답 DTO
├── exception             # 공통 예외 처리 
├── jwt                   # JWT 토큰 필터 및 처리
├── model                 # User 모델 클래스
├── service               # 인증 관련 비즈니스 로직
├── security              # 사용자 인증 처리
└── repository            # 사용자 저장소 (JPA)
```
