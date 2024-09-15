# 테스트 범위와 종류

## 테스트 범위
- 일반적인 웹 애플리케이션은 **브라우저**, **웹 서버** (예: 톰캣), **데이터베이스**로 구성됩니다.
- 테스트 범위는 **목적**과 **수행자**에 따라 달라집니다.

## 테스트 종류

### 1. 단위 테스트 (Unit Test)
- **목적**: 개별 코드나 컴포넌트의 동작 확인
- **범위**: 주로 클래스나 메서드 단위
- **특징**: 가장 작은 단위의 테스트, 빠른 실행 속도

### 2. 통합 테스트 (Integration Test)
- **목적**: 시스템 구성 요소 간 상호작용 확인
- **대상**: 프레임워크, 라이브러리, DB, 기능 구현 코드
- **예시**: 스프링 + JPA + MySQL을 이용한 회원 가입 서비스 클래스 테스트

### 3. 기능 테스트 (Functional Test)
- **목적**: 사용자 관점에서 시스템 기능 동작 확인
- **범위**: 모든 관련 구성 요소를 포함 (웹서버, DB, 브라우저 등)
- **별칭**: E2E (End-to-End) 테스트

## 테스트 범위 간 주요 차이점
1. **구성 요소 필요성**:
    - 단위 테스트: 구현 코드만으로 가능
    - 통합/기능 테스트: 추가 구성 요소 필요
2. **실행 속도**:
    - 단위 테스트: 빠름
    - 통합/기능 테스트: 상대적으로 느림
3. **결과 확인 용이성**:
    - 단위 테스트: 쉬움
    - 통합/기능 테스트: 어려울 수 있음 (대역 사용으로 보완)

## 테스트 자동화 시 고려사항
- **핵심**: 테스트 범위에 따른 코드 개수와 실행 시간 파악
- **기능/통합 테스트**: 정상 케이스 + 주요 특수 케이스만 테스트
- **단위 테스트**: 다양한 상황 고려, 더 많은 코드 작성 필요

## 외부 연동이 필요한 테스트 예시

### 1. SpringBoot와 DB(JPA, MySQL) 통합 테스트
- **목적**: 실제 DB 연동 테스트
- **변경점**: `MemoryUserRepository` → 실제 DB 연동 `UserRepository`

### 2. WireMock을 이용한 REST 클라이언트 테스트
- **용도**: 외부 HTTP 통신 모의 테스트
- **장점**: 실제 서버 없이 HTTP 응답 및 타임아웃 테스트 가능

### 3. SpringBoot 내장 서버를 이용한 API 기능 테스트
- **특징**: 스프링 부트의 웹 환경 테스트 기능 활용
- **예시 코드**:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiE2ETest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void weakPwResponse() {
        String resBody = "{\"id\":\"id\", \"pw\":\"123\", \"email\":\"lowell@lowell.com\"}";

        RequestEntity<String> req = RequestEntity.post(URI.create("/users"))
                                                 .contentType(MediaType.APPLICATION_JSON)
                                                 .body(resBody);

        ResponseEntity<String> res = restTemplate.exchange(req, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("WeakPasswordException"));
    }
}
```

이 예제는 임의 포트로 내장 서버를 구성하고, `TestRestTemplate`을 사용해 POST 요청을 보내고 응답을 검증합니다.