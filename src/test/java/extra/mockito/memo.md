# Mockito 기초 사용법

## 1. 모의 객체 생성
Mockito의 `mock()` 메서드를 사용하여 특정 타입의 모의 객체를 생성할 수 있습니다.

```java
public class GameGenMockTest {
    @DisplayName("모의 객체 생성")
    @Test
    void mockTest() {
        GameNumGen genMock = mock(GameNumGen.class);
    }
}
```

`GameNumGen` 인터페이스 예시:
```java
public interface GameNumGen {
    String generate(GameLevel level);
}
```

## 2. 스텁 설정
`BDDMockito`를 사용하여 모의 객체의 특정 메서드 호출 시 반환값을 설정할 수 있습니다.

- `BDDMockito.given()` : 모의 객체가 특정 값을 반환하도록 설정
- `willReturn()` : 설정된 값 반환

### 예시: 특정 메서드 호출 시 스텁 동작
```java
public class GameGenMockTest {
    @DisplayName("모의 객체 스텁 설정")
    @Test
    void mockTest() {
        GameNumGen genMock = mock(GameNumGen.class);
        
        // genMock.generate(GameLevel.EASY) 호출 시 "123" 리턴
        given(genMock.generate(GameLevel.EASY))
                .willReturn("123");

        String num = genMock.generate(GameLevel.EASY);
        assertEquals("123", num);
    }
}
```

### 특정 예외 발생 설정
메서드 호출 시 예외를 발생시키는 스텁 설정도 가능합니다.

```java
@DisplayName("모의 객체 예외 발생")
@Test
void mockThrowTest() {
    GameNumGen genMock = mock(GameNumGen.class);
    given(genMock.generate(null))
            .willThrow(IllegalArgumentException.class);

    assertThrows(IllegalArgumentException.class, () -> genMock.generate(null));
}
```

### void 메서드에서 예외 발생 설정
```java
public class VoidMethodStubTest {
    @DisplayName("리턴타입이 void인 메서드에서 예외 발생")
    @Test
    void test1() {
        List<String> mockList = mock(List.class);
        willThrow(UnsupportedOperationException.class)
                .given(mockList)
                .clear();
    }
}
```

#### List.clear() 메서드 예외 처리
`List.clear()` 메서드는 반환 타입이 `void`입니다. 그러나 리스트가 다음과 같은 유형일 경우 `UnsupportedOperationException`이 발생할 수 있습니다:

- **불변 리스트**: `Collections.unmodifiableList()`
- **고정 크기 리스트**: `Arrays.asList()`
- **커스텀 리스트**: 읽기 전용으로 설계된 사용자 정의 리스트

예시로, 커스텀 리스트에서 `clear()` 메서드를 호출하면 예외를 발생시키는 클래스를 만들 수 있습니다:

```java
class CustomReadOnlyList<E> extends AbstractList<E> {
    @Override
    public E get(int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear() is not supported.");
    }
}
```

## 3. 인자 매칭 처리
`ArgumentMatchers`를 사용하여 메서드 인자의 특정 값 또는 임의의 값을 매칭할 수 있습니다.

| 리턴 타입  | 값                                    |
|-----------|---------------------------------------|
| 원시 타입  | 각 타입의 기본 값 (int: 0, boolean: false 등) |
| 참조 타입  | null                                  |

```java
@DisplayName("인자 매칭 처리")
@Test
void matchedGivenParams() {
    GameNumGen genMock = mock(GameNumGen.class);
    given(genMock.generate(GameLevel.EASY)).willReturn("123");
    String num = genMock.generate(GameLevel.NORMAL);
    assertNull(num);  // 스텁 설정이 없으므로 기본값(null) 반환
}
```

### ArgumentMatchers를 이용한 인자 매칭
Mockito의 `ArgumentMatchers`를 사용하면 특정 값을 정확하게 매칭하지 않고, 임의의 값과 일치하도록 설정할 수 있습니다. 다양한 데이터 타입에 대해 임의의 값으로 매칭을 설정할 수 있습니다.

| 메서드                                                                                                               | 설명                        |
|-------------------------------------------------------------------------------------------------------------------|---------------------------|
| anyInt()<br/>anyShort()<br/>anyLong()<br/>anyByte()<br/>anyChar()<br/>anyDouble()<br/>anyFloat()<br/>anyBoolean() | 기본 데이터 타입에 대한 임의 값 일치     |
| anyString()                                                                                                       | 문자열에 대한 임의 값 일치           |
| any()                                                                                                             | 임의 타입에 대한 일치              |
| anyList()<br/>anySet()<br/>anyMap()<br/>anyCollection()                                                           | 임의 콜렉션에 대한 일치             |
| matches(String)<br/>matches(Pattern)                                                                              | 정규표현식을 이용한 String 값 일치 여부 |
| eq(value)                                                                                                         | 특정 값과 정확히 일치하는 값 설정       |

#### 인자가 두 개 이상인 경우 주의사항
스텁을 설정할 메서드의 인자가 두 개 이상인 경우, 한 인자가 `ArgumentMatcher`를 사용하면 나머지 인자들도 `ArgumentMatcher`로 설정해야 합니다. 즉, 임의 값과 특정 값을 함께 사용할 때는 `ArgumentMatchers.eq()`를 사용해야 합니다.

#### 잘못된 예시:
```java
@DisplayName("인자가 두 개일 때 잘못된 설정")
@Test
void overTwoParamsAnyMatchTest() {
    List<String> mockList = mock(List.class);
    given(mockList.set(anyInt(), "123")).willReturn("456"); // 두 번째 인자에 명확한 값 사용
    String old = mockList.set(5, "123");
}
```
위 코드는 `ArgumentMatcher`와 명확한 값을 혼합하여 사용했기 때문에 올바르지 않습니다.

#### 올바른 예시:
```java
@DisplayName("인자가 두 개일 때 ArgumentMatcher 사용")
@Test
void mixAnyAndEq() {
    List<String> mockList = mock(List.class);
    given(mockList.set(anyInt(), eq("123"))).willReturn("456"); // eq()로 명확한 값 사용
    String old = mockList.set(5, "123");
    assertEquals("456", old);
}
```

`ArgumentMatchers.eq()` 메서드를 사용하여 특정 값과 임의 값을 함께 설정할 수 있습니다.

```java
@DisplayName("스텁 설정 시 임의 값과 특정 값 혼합 사용")
@Test
void mixAnyAndEq() {
    List<String> mockList = mock(List.class);
    given(mockList.set(anyInt(), eq("123"))).willReturn("456");
    String old = mockList.set(5, "123");
    assertEquals("456", old);
}
```

## 4. 행위 검증
모의 객체가 호출된 횟수를 검증할 수 있습니다.

| 메서드           | 설명                         |
|------------------|------------------------------|
| only()           | 한 번만 호출                 |
| times(int)       | 지정한 횟수만큼 호출          |
| never()          | 호출되지 않음                |
| atLeast(int)     | 적어도 지정한 횟수만큼 호출  |
| atLeastOnce()    | 한 번 이상 호출              |
| atMost(int)      | 최대 지정한 횟수만큼 호출     |

## 5. 인자 캡처
`ArgumentCaptor`를 사용하면 메서드 호출 시 전달된 인자를 캡처하여 검증할 수 있습니다.

```java
@DisplayName("가입 완료 후 메일 전송")
@Test
void test4() {
    userRegister.register("id", "pw", "lowell@lowell.com");
    
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    then(spyEmailNotifier)
            .should()
            .sendRegisterEmail(captor.capture());

    String realEmail = captor.getValue();
    assertEquals("lowell@lowell.com", realEmail);
}
```

## 6. JUnit 5 확장 설정
`Mockito`와 `JUnit 5`를 함께 사용하여 테스트를 확장할 수 있습니다.

### Gradle 의존성 추가
```groovy
testImplementation("org.mockito:mockito-junit-jupiter:5.13.0")
```

### Mockito 확장 사용 예시
```java
@ExtendWith(MockitoExtension.class)
public class JUnit5ExtensionTest {

    @Mock
    private GameNumGen genMock;
}
```