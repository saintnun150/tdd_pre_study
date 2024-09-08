## Assertions 클래스

| 메서드                               | 설명                |   
|--------------------------------------|---------------------|
| `assertEquals(expected, actual)`     | 값의 일치 여부       |   
| `assertNotEquals(unexpected, actual)`| 값의 불일치 여부     |
| `assertSame(obj1, obj2)`             | 동일한 객체인지 확인 |
| `assertNotSame(obj1, obj2)`          | 동일하지 않은 객체 확인 |
| `assertTrue(boolean condition)`      | 값이 `true`인지 확인 |
| `assertFalse(boolean condition)`     | 값이 `false`인지 확인 |
| `assertNull(obj1)`                   | 값이 `null`인지 확인 |
| `assertNotNull(obj1)`                | 값이 `null`이 아닌지 확인 |
| `fail()`                             | 테스트를 강제로 실패 처리 |

## Assertions Exceptions Checker

| 메서드                                                        | 설명                                                       |   
|---------------------------------------------------------------|-------------------------------------------------------------|
| `assertThrows(Class<T> expectedType, Executable executable)`   | `executable`을 실행한 결과로 `expectedType`의 예외가 발생하는지 검사 |
| `assertDoesNotThrow(Executable executable)`                   | `executable`을 실행한 결과로 예외가 발생하지 않는지 검사      |
| `assertAll(Executable... executables)`                        | 가변인자로 받은 모든 `executable`을 실행하고 그 결과로 검증 실패한 목록을 모아서 에러로 출력 |

## Test Method 라이프사이클

1. 테스트 메서드를 포함한 객체 생성
2. (존재하면) `@BeforeEach` 어노테이션이 붙은 메서드 실행
3. `@Test` 어노테이션이 붙은 메서드 실행
4. (존재하면) `@AfterEach` 어노테이션이 붙은 메서드 실행
5. **각 `@Test` 메서드를 실행할 때마다 각 사이클을 반복함**

## 어노테이션

| 어노테이션      | 설명                                                                                          |   
|-----------------|---------------------------------------------------------------------------------------------|
| `@BeforeEach`   | 테스트를 실행하는 데 필요한 준비 작업을 할 때 사용. 테스트에 사용할 임시 파일이나 객체 등을 생성. `private` 접근자 안 됨  | 
| `@AfterEach`    | 테스트를 실행한 후에 정리할 것이 있을 때 사용. `BeforeEach`에서 만든 임시 파일 등을 삭제. `private` 접근자 안 됨 | 
| `@BeforeAll`    | 한 클래스의 모든 테스트 메서드가 실행되기 전에 특정 작업을 수행할 때 사용. 정적 메서드에 사용하고 클래스의 모든 테스트 메서드를 실행하기 전에 딱 한 번 실행됨 | 
| `@AfterAll`     | `@BeforeAll`과 반대로 클래스의 모든 테스트 메서드를 실행한 뒤에 실행. 정적 메서드에 사용 | 

## 테스트 메서드 간 실행 순서 의존과 필드 공유하지 않기

1. 테스트 메서드가 특정 순서대로 실행된다는 가정하에 테스트 메서드를 작성하지 말 것
2. JUnit 버전에 따라 테스트 순서가 달라질 수 있으며, 순서에 의존한 테스트는 실패할 가능성이 있음
3. `readFileTest` 메서드가 먼저 실행될 경우 `file` 필드가 `null`이므로 테스트에 실패

## @DisplayName, @Disabled 어노테이션

| 어노테이션       | 설명                                                   |   
|------------------|--------------------------------------------------------|
| `@DisplayName`   | 테스트 메서드 표시 이름을 지정. 메서드명에 공백이나 특수 문자를 사용할 때 유용 |
| `@Disabled`      | 테스트 실행 대상에서 제외                                |

## 모든 테스트 실행하기

코드 푸시 또는 배포 전에 모든 테스트를 실행해 실패하는 케이스가 없는지 확인할 것!

### Maven

1. (기본) `mvn test`
2. (wrapper 사용) `mvnw test`

### Gradle

1. (기본) `gradle test`
2. (wrapper 사용) `gredlew test`
