본 프로젝트는 스파르타 한달 인턴 과제 전형 구현 결과물입니다.

## 프로젝트 소개
Spring Security를 이용한 JWT 인증/인가를 구현하고 유닛테스트를 작성하였습니다.

## 구현 사항

### 1. 배포 링크 
[http://54.166.174.224/swagger-ui/index.html#/user-controller/login](http://54.166.174.224/swagger-ui/index.html#/user-controller/login)

### 2. 유닛 테스트
- [JWT 생성/검증 유닛 테스트](https://github.com/mii2026/Login/blob/develop/src/test/java/com/example/login/infrastructure/security/JwtTokenizerTest.java)
- [서비스 유닛 테스트](https://github.com/mii2026/Login/blob/develop/src/test/java/com/example/login/application/service/UserServiceTest.java)
- [컨트롤러 유닛 테스트](https://github.com/mii2026/Login/blob/develop/src/test/java/com/example/login/presentation/controller/UserControllerTest.java)

### 3. AI 코드리뷰 및 리팩토링
기능 구현 후 Chat GPT를 통해 코드리뷰를 받아 리팩토링을 진행했습니다.

- [서비스 로직 리팩토링](https://github.com/mii2026/Login/pull/18)
- [전체 디렉토리 구조 리팩토링](https://github.com/mii2026/Login/pull/14)

### 4. Pull Request
Convention을 작성하고 이에 맞춰 Pull Request를 생성했습니다.
- [Convention](https://github.com/mii2026/Login/wiki/Convention)
