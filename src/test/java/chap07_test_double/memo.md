# 테스트 대역

## 대역의 필요성
테스트를 작성할 때 외부 요인이 필요할 경우
주요 예   
* 파일 시스템 사용
* DB CRUD
* 외부 HTTP 통신

ex) 주요 예
자동 이체 등록기 -> 카드번호 검사기 -> api 호출 -> 카드사 카드 정보 API   

이렇게 카드정보를 얻기 위해서는 외부 서비스가 관련 테스트 환경을 제공하지 않으면 힘들다.   
이럴 경우 대역을 사용하면 테스트를 진행할 수 있다.

## 대역을 이용한 테스트
대역을 이용해 AutoDebitRegister를 테스트하는 코드를 다시 작성하자   
CardNumberValidator -> StubCardNumberValidator   
AutoDebitInfoRepository  -> StubAutoDebitInfoRepository   
여기서 StubCardNumberValidator, StubAutoDebitInfoRepository의 역할은 실제 카드번호 검증 기능을 구현X. 대신 단순한 구현으로 실제 구현을 대체    

아래는 예시
```java
public class StubCardNumberValidator extends CardNumberValidator {
    private String invalidNo;
    
    public void setInvalidNo(String invalidNo) {
        this.invalidNo = invalidNo;
    }

    @Override
    public CardValidity validate(String cardNumber) {
        if (invalidNo != null && invalidNo.equals(cardNumber)) {
            return CardValidity.INVALID;
        }
        return CardValidity.VALID;
    }
}

public class StubAutoDebitInfoRepository implements AutoDebitInfoRepository {
    @Override
    public AutoDebitInfo findOne(String userId) {
        return null;
    }

    @Override
    public void save(AutoDebitInfo newInfo) {

    }
}
```

이 대역들을 통해 AutoDebitRegister를 다시 테스트해보자.   

또한 실제 DB 대신 Memory 기반으로 대역을 만들어 사용할 수 있다.
```java
public class MemoryAutoDebitInfoRepository implements AutoDebitInfoRepository {
    private Map<String, AutoDebitInfo> infos = new HashMap<>();

    @Override
    public AutoDebitInfo findOne(String userId) {
        return infos.get(userId);
    }

    @Override
    public void save(AutoDebitInfo newInfo) {
        infos.put(newInfo.getUserId(), newInfo);

    }
}
```

## 대역을 사용한 외부 상황 흉내와 결과 검증

* StubCardNumberValidator -> 외부 API 연동 없이 동작확인. 카드번호 유효성을 흉내
* MemoryAutoDebitInfoRepository -> 실제 DB 연동 없이 CU 확인. DB 등록, 수정을 흉내

이처럼 대역을 사용하면 테스트 대상에 대해 쉽고 빠르게 검증 가능

## 대역의 종류

| 종류 | 설명                                                                |   
|----|-------------------------------------------------------------------|
| 스텁(Stub) | 구현을 단순한 것으로 대체. 테스트에 맞게 단순히 원하는 동작을 수행                            |
| 가짜(Fake) | 제품에는 적합하지 않지만, 실제 동작하는 구현을 제공. DB 대신에 메모리 등을 이용해 구현한 가짜 대역        |
| 스파이(Spy) | 호출된 내역을 기록. 기록한 내용은 테스트 결과를 검증할 때 사용. 스텁의 일종                      |
| 모의(Mock) | 기대한 대로 상호작용하는지 행위를 검증. 기대한 대로 동작하지 않으면 예외를 발생할 수 있다. 스텁이자 스파이의 일종 | 

회원 가입 기능 예시를 통해 대역을 확인해보자.   
아래는 구현하기 전 필요한 기능 단위에 대해 설계한 내용이다.
* UserRegister: 회원 가입에 대한 핵심 로직을 수행
* WeakPasswordChecker: 암호가 약한지 검사
* UserRepository: 회원 정보에 대한 CRUD 기능 제공
* EmailNotifier: 이메일 발송 기능 제공

## 스텁(Stub) - 약한 암호 확인 기능
```java
public interface WeakPasswordChecker {
    boolean checkPasswordWeak(String pw);
}

public class StubWeakPasswordChecker implements WeakPasswordChecker {
    private boolean weak;

    public void setWeak(boolean weak) {
        this.weak = weak;
    }

    @Override
    public boolean checkPasswordWeak(String pw) {
        return weak;
    }
}

public void register(String id, String pw, String email) {
    if (passwordChecker.checkPasswordWeak(pw)) {
        throw new WeakPasswordException();
    }
}
```

## 가짜(Fake) - 리포지토리 구현
```java
public interface UserRepository {
    void save(User user);
    User findById(String id);
}

public class MemoryUserRepository implements UserRepository {
    private Map<String, User> users = new HashMap<>();

    @Override
    public void save(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public User findById(String id) {
        return users.get(id);
    }
}

public void register(String id, String pw, String email) {
    if (passwordChecker.checkPasswordWeak(pw)) {
        throw new WeakPasswordException();
    }
    User user = userRepository.findById(id);
    if (user != null) {
        throw new DuplicateIdException();
    }
    userRepository.save(new User(id, pw, email));
}
```

## 스파이(Spy) - 이메일 발송 여부 확인
회원 가입에 성공 -> 회원 가입 안내 메일 발송   
UserRegister가 EmailNotifier의 메일 발송 기능을 실행 -> 이메일 주소로 해당 이메일을 사용했는지 확인   
이럴 때 스파이 대역을 사용한다.   

EmailNotifier 기능 구현   
* 이메일 발송 여부
* 발송 요청 이메일 주소 제공

```java
public interface EmailNotifier {
    void sendRegisterEmail(String email);
}

public class SpyEmailNotifier implements EmailNotifier {
    private boolean notified;
    private String email;

    public boolean isNotified() {
        return notified;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public void sendRegisterEmail(String email) {
        this.notified = true;
        this.email = email;
    }
}

public void register(String id, String pw, String email) {
    // 중략
    userRepository.save(new User(id, pw, email));
    emailNotifier.sendRegisterEmail(email);
}
```

위 기능 구현을 UserRegister에서 호출하자

## 모의(Mock) - 스텁, 스파이 대체
스텁과 스파이 대역은 모의(Mock) 객체로 대체할 수 있다.

* 스텁
```java
public class UserRegisterMockTest {
    private WeakPasswordChecker mockPasswordChecker = Mockito.mock(WeakPasswordChecker.class);
    private EmailNotifier mockEmailNotifier = Mockito.mock(EmailNotifier.class);
    
    // 중략
    @DisplayName("약한 암호면 가입 실패")
    @Test
    void test1() {
        // pw 인자를 사용해 모의 객체의 checkPasswordWeak 메서드를 호출하면 결과로 true를 리턴
        given(mockPasswordChecker.checkPasswordWeak("pw"))
                .willReturn(true);
        assertThrows(WeakPasswordException.class, () -> {
            userRegister.register("id", "pw", "email");
        });
    }

    @DisplayName("회원 가입시 암호 검사 수행 여부")
    @Test
    void test2() {
        userRegister.register("id", "pw", "email");

        then(mockPasswordChecker)
                .should()
                .checkPasswordWeak(anyString());
    }
}
```

* 스파이
```java
@DisplayName("가입 완료 후 메일 전송")
@Test
void test4() {
    userRegister.register("id", "pw", "lowell@lowell.com");
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    then(mockEmailNotifier)
            .should()
            .sendRegisterEmail(captor.capture());
    // 모의 객체 메서드의 인자가 captor에 담긴다.
    // sendRegisterEmail에서는 email 인자

    String realEmail = captor.getValue();
    assertEquals("lowell@lowell.com", realEmail);
}
```


### Mockito 기초 사용법

#### 모의 객체 생성
Mockito.mock() -> 특정 타입의 모의 객체 생성
```java
public class GameGenMockTest {
    @DisplayName("모의 객체 생성")
    @Test
    void mockTest() {
        GameNumGen genMock = mock(GameNumGen.class);
    }
}
public interface GameNumGen {
    String generate(GameLevel level);
}
```

#### 스텁 설정
BDDMockito class -> 모의 객체에 스텁 구성
* BDDMockito.given() -> 모의 객체가 특정 값을 리턴가능 하도록 스텁 생성
* Stub.BDDMockito.willReturn() -> 스텁에서 특정 값을 리턴

##### 스텁 설정에 매칭되는 메서드 실행
given 메서드 파라미터로 넘긴 인자의 객체값을 비교하기
```java
public class GameGenMockTest {
    @DisplayName("모의 객체 생성")
    @Test
    void mockTest() {
        // 1. 모의 객체 생성
        GameNumGen genMock = mock(GameNumGen.class);
        // 2. 스텁 설정
        // genMock.generate(GameLevel.EASY) 호출 시 "123" 리턴
        given(genMock.generate(GameLevel.EASY))
                .willReturn("123");

        // 3. 스텁 설정에 매칭되는 메서드 실행(given 메서드 파라미터)
        String num = genMock.generate(GameLevel.EASY);
        assertEquals("123", num);
    }
}
```

##### 특정 타입의 익셉션을 발생하도록 스텁 설정
```java
@DisplayName("모의 객체 익셉션 발생")
    @Test
    void mockThrowTest() {
        GameNumGen genMock = mock(GameNumGen.class);
        given(genMock.generate(null))
                .willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> genMock.generate(null));
    }
```
아래 예를 void 리턴 타입의 Method 실행에 대해 익셉션를 발생시키는 코드이다.
```java
public class VoidMethodStubTest {
    @DisplayName("리턴타입 void throw")
    @Test
    void test1() {
        List<String> mockList = mock(List.class);
        // vpid 타입일 경우 willThrow로 시작
        willThrow(UnsupportedOperationException.class)
                .given(mockList)
                .clear();

        // clear라는 메서드는 리턴타입이 void인데
        // given에 주어진 List 타입이 이를 지원하지 않을 경우 UnsupportedOperationException 발생
    }
}
```

List.clear() 메서드는 void 타입이다. 그러나 clear 할 수 없는 list 형태일 경우
UnsupportedOperationException을 발생시킨다.
* 불변 리스트 -> Collections.unmodifiableList()
* 고정 크기 리스트 -> Arrays.asList()
* 커스텀 리스트 -> 읽기 전용
```java
class CustomReadOnlyList<E> extends AbstractList<E> {
    @Override
    public E get(int index) {
    }

    @Override
    public int size() {
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear() is not supported.");
    }
}

```

#### 인자 매칭 처리
mockito는 일치하는 스텁 설정이 없을 경우 리턴 타입의 기본 값을 리턴

| 리턴 타입  | 값                                      |
|--------|----------------------------------------|
| 원시 타입 | 각 기본 값<br/> int: 0<br/> boolean: false |
| 참조 타입 | null                                   |

```java
@DisplayName("인자 매칭 처리")
    @Test
    void matchedGivenParams() {
        GameNumGen genMock = mock(GameNumGen.class);
        given(genMock.generate(GameLevel.EASY)).willReturn("123");
        String num = genMock.generate(GameLevel.NORMAL);
        assertEquals("123", num);
    }
```

org.mockito.ArgumentMatchers class 사용 시 정확하게 일치하는 값 대신 임의의 값에 일치하도록 설정 가능   

| 메서드                                                                                                               | 설정                        |
|-------------------------------------------------------------------------------------------------------------------|---------------------------|
| anyInt()<br/>anyShort()<br/>anyLong()<br/>anyByte()<br/>anyChar()<br/>anyDouble()<br/>anyFloat()<br/>anyBoolean() | 기본 데이터 타입에 대한 임의 값 일치     |
| anyString()                                                                                                       | 문자열에 대한 임의 값 일치           |
| any()                                                                                                             | 임의 타입에 대한 일치              |
| anyList()<br/>anySet()<br/>anyMap()<br/>anyCollection()                                                           | 임의 콜렉션에 대한 일치             |
| matches(String)<br/>matches(Pattern)                                                                              | 정규표현식을 이용한 String 값 일치 여부 |
| eq(value)                                                                                                         | 특정 value와 일치 여부           |


스텁을 설정할 메서드의 인자가 두 개 이상인 경우 주의할 점이 있다.

```java
@DisplayName("스텁을 설정할 메서드의 인자가 두 개 이상일 때 정확한 값에 일치하도록 설정")
@Test
void overTwoParamsAnyMatchTest() {
    List<String> mockList = mock(List.class);
    given(mockList.set(anyInt(), "123")).willReturn("456");
    String old = mockList.set(5, "123");
}
```
위 코드에서 mockList.set의 첫 번째 인자는 ArgumentMatcher를 사용해 임의값을 설정, 두 번째 인자는 특정 값을 사용했다.
ArgumentMatchers의 anyInt(), any() 등의 메서드는 내부적으로 인자의 일치 여부를 판단할 때 ArgumentMatcher를 이용한다.   
Mockito는 한 인자라도 ArgumentMatcher를 사용해서 설정할 경우 모든 인자를 ArgumentMatcher를 이용해 설정 해야한다.   
만약 임의 값과 특정 값을 함께 사용할 경우 아래와 값이 ArgumentMatchers.eq() 메서드를 사용해라

```java
@DisplayName("스텁을 설정할 메서드의 인자가 두 개 이상인데 임의 값 일치와 정확한 값 일치가 필요할 때")
    @Test
    void mixAnyAndEq() {
        List<String> mockList = mock(List.class);
        given(mockList.set(anyInt(), eq("123"))).willReturn("456");
        String old = mockList.set(5, "123");
        assertEquals("456", old);
    }
```


#### 행위 검증
모의 객체의 역할 중 하나는 실제로 모의 객체가 호출됐는 지 검증하는 것이다.

| 메서드           | 설정              |
|---------------|-----------------|
| only()        | 한 번만 호출         |
| times(int)    | 지정한 횟수만큼 호출     |
| never()       | 호출하지 않음         |
| atLeast(int)  | 적어도 지정한 횟수만큼 호출 |
| atLeastOnce() | atLeast(1) 과 동일 |
| atMost(int) | 최대 지정한 횟수만큼 호출 |

#### 인자 캡쳐
모의 객체를 호출할 때 사용한 인자를 검증해야 한다. String, int 같은 타입은 쉽게 검증 가능하나, 여러 속성을 가진 객체는 쉽게 검증하기 어렵다. 이럴 때 인자 캡쳐를 사용한다.   
Mockito.ArgumentCaptor를 사용하면 메서드 호출 여부를 검증하는 과정에서 실체 호출할 때 전달한 인자를 보관할 수 있다.

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

* String 타입 인자를 보관할 수 있는 ArgumentCaptor 생성
* 생성한 ArgumentCaptor을 모의 객체 호출 여부를 검증하는 코드에서 인자로 전달.
* 모의 객체를 실행할 때 사용한 인자는 ArgumentCaptor.getValue() 메서드로 구할 수 있다.
* 이 값을 검증에 사용하면 된다.

#### JUnit 5 확장 설정
Mockito - JUnit 라이브러리를 사용하면 애노테이션을 통해 모의 객체를 생성 가능하다.

```groovy
testImplementation("org.mockito:mockito-junit-jupiter:5.13.0")
```

```java
@ExtendWith(MockitoExtension.class)
public class JUnit5ExtensionTest {

    @Mock
    private GameNumGen genMock;
}
```



















