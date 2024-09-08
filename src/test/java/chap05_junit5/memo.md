Assertions 클래스

| 메서드                               | 설명            |   
|-----------------------------------|---------------|
| assertEquals(expected, actual)    | 값의 일치여부       |   
| assertnotEquals(unexpected, actual) | 값이 불일치여부      |
| assertSame(obj1, obj2)            | 동일한 객체인지      |
| assertNotSame(obj1, obj2)         | 동일하지 않은 객체 확인 |
| assertTrue(boolean condition)     | 값이 true인지     |
| assertFalse(boolean condition)    | 값이 false인지    |
| assertNull(obj1)                  | 값이 null인지     |
| assertNotNull(obj1) | 값이 null이 아닌지|
| fail() | 테스트를 강제로 실패처리|

Assertions Exceptions Checker

| 메서드                                                        | 설명                                                       |   
|------------------------------------------------------------|----------------------------------------------------------|
| assertThrows(Class<T> expectedType, Executable executable) | executable을 실행한 결과로 expectedType의 익셉션이 발생하는지 검사          |
| assertDoesNotThrows(Executable executable)                 | executable을 실행한 결과로 익셉션이 발생하지 않는지 검사                     |
| assertAll(Executable... executables)                       | 가변인자로 받은 모든 executable을 실행하고 그 결과로 검증 실패한 목록을 모아서 에러로 출력|


TestMethod 라이프사이클

1. 테스트 메서드를 포함한 객체 생성
2. (존재하면) @BeforeEach 어노테이션이 붙은 메서드 실행
3. @Test 어노테이션이 붙은 메서드 실행
4. (존재하면) @AfterEach 어노테이션이 붙은 메서드 실행
5. **각 @Test 메서드를 실행할 때마다 각 사이클을 반복함**

| 어노테이션      | 설명                                                                                          |   
|------------|---------------------------------------------------------------------------------------------|
| BeforeEach | 테스트를 실행하는데 필요한 준비 작업을 할 때 사용한다. 테스트에 사용할 임시 파일이나 객체 등을 생성. private 접근자 안 됨                  | 
| AfterEach  | 테스트를 실행한 후에 정리할 것이 있을 때 사용, BeforeEach에서 만든 임시 파일 등을 삭제.. private 접근자 안 됨                   | 
| BeforeAll  | 한 클래스의 모든 테스트 메서드가 실행되기 전에 특정 작업을 수행할 때 사용. 정적 메서드에 사용하고 클래서의 모든 테스트 메서드를 실행하기 전에 딱 한 번 실행됨 | 
| AfterAll   | BeforeAll과 반대로 클래스의 모든 테스트 메서드를 실행한 뒤에 실행. 정적 메서드에 사용.                                      | 


테스트 메서드 간 실행 순서 의존과 필드 공유하지 않기

1. 테스트 메서드가 특정 순서대로 실행된다는 가정하에 테스트 메서드를 작성하지 말 것
2. JUnit 버전에 따라 테스트 순서가 달라질 수도 있고 따라서 순서에 의존된 테스트들을 실패하게 됨
3. readFileTest 메서드가 먼저 실행될 경우 file필드가 null이므로 테스트에 실패

@DisplayName, @Disabled

 어노테이션       | 설명                                              |   
|-------------|-------------------------------------------------|
| DisplayName | 테스트 메서드 표시 이름을 지정. 메서드명에 공백이나 특수 문자를 사용해야할 때 유용 |
| Disabled   | 테스트 실행 대상에서 제외                                  |


모든 테스트 실행하기

코드 푸시 or 배포 전에 모든 테스트를 실행해서 실패하는 케이스가 없는지 확인할 것!

maven
1. (기본) mvn test
2. (wrapper 사용) mvnw test

gradle
1. (기본) gradle test
2. (wrapper 사용) gradlew test