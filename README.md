## 연동 절차
1. OAuth2 Client와 Google / Naver / Kakao 인가 서버와의 연동을 통해 인증을 진행하고 사용자 정보를 가져온다.
    - Google / Naver / Kakao 서비스에 신규 서비스를 생성한다.
        - Google : [https://console.cloud.google.com](https://console.cloud.google.com/)
        - Naver : https://developers.naver.com/main/
        - Kakao : https://developers.kakao.com/
    - application.yml 설정
2. 응답 받은 정보를 바탕으로 JWT를 발급한다.
    - 최초로 로그인한 경우 사용자 정보를 내 서비스의 DB에 저장한다.
<br>

## 각 소셜 로그인 요청 URL
- 구글 : http://localhost:8081/oauth2/authorization/google
- 카카오 : http://localhost:8081/oauth2/authorization/kakao
<br>

#### 참고 강의
- 인프런 정수원 <스프링 시큐리티 OAuth2 강의> 섹션 10
- 유튜브 개발자유미 <스프링 OAuth2 클라이언트 JWT>
- 유튜브 개발자유미 <스프링 JWT 심화>
<br>

## DB 설정
### 도커를 활용해 MySQL 설치
1. MySQL Docker 이미지 다운로드<br>
`docker pull mysql:8.0`
    
3. MySQL Docker 컨테이너 생성 및 실행<br>
`docker run --name hlbg-mysql -e MYSQL_ROOT_PASSWORD=hlbg240310 -d -p 3306:3306 mysql:8.0`
    
4. MySQL 접속<br>
`docker exce -it hlbg-mysql mysql -u root -p`

### DB 생성
- Schema 생성
```sql
CREATE DATABASE patients_meal
CHARACTER SET utf8             # 데이터 UTF-8 인코딩
COLLATE utf8_general_ci;       # 대소문자를 구분하지 않는 정렬 사용
```

- user 테이블 생성
```sql
CREATE TABLE user (
	id             BIGINT AUTO_INCREMENT PRIMARY KEY,
	username       VARCHAR(50)                         NOT NULL UNIQUE COMMENT '고유 식별자 (Ex. kakao_123456789)',
	nickname       VARCHAR(50)                         NOT NULL UNIQUE COMMENT '닉네임',
	email          VARCHAR(100)                        NULL COMMENT '이메일',
	role           VARCHAR(20)                         NOT NULL COMMENT '권한',
	created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
	updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
	last_logged_at TIMESTAMP                           NULL COMMENT '마지막 로그인 일시',
  refresh_token  VARCHAR(255)                        NULL COMMENT 'Refresh Token 값',
	deleted_at     TIMESTAMP                           NULL COMMENT '삭제일시'
);
```
