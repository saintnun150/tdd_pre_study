# 테스트 대역

## 목차
1. [대역의 필요성](#대역의-필요성)
2. [대역을 이용한 테스트](#대역을-이용한-테스트)
3. [대역을 사용한 외부 상황 흉내와 결과 검증](#대역을-사용한-외부-상황-흉내와-결과-검증)
4. [대역의 종류](#대역의-종류)
5. [대역 예시](#대역-예시)
    - [스텁 - 약한 암호 확인 기능](#스텁---약한-암호-확인-기능)
    - [가짜 - 리포지토리 구현](#가짜---리포지토리-구현)
    - [스파이 - 이메일 발송 여부 확인](#스파이---이메일-발송-여부-확인)
    - [모의 - 스텁과 스파이 대체](#모의---스텁과-스파이-대체)
6. [상황과 결과 확인을 위한 협업 대상 도출과 대역 사용](#상황과-결과-확인을-위한-협업-대상-도출과-대역-사용)
7. [대역과 개발 속도](#대역과-개발-속도)
8. [모의 객체 과하게 사용하지 않기](#모의-객체-과하게-사용하지-않기)

## 대역의 필요성

테스트를 작성할 때 외부 요인이 필요한 경우 테스트 대역이 필요합니다.

주요 예:
- 파일 시스템 사용
- DB CRUD
- 외부 HTTP 통신

예시:
> 자동 이체 등록기 -> 카드번호 검사기 -> API 호출 -> 카드사 카드 정보 API

이런 경우, 카드 정보를 얻기 위해 외부 서비스가 관련 테스트 환경을 제공하지 않으면 테스트하기 어렵습니다. 이럴 때 대역을 사용하면 테스트를 진행할 수 있습니다.

## 대역을 이용한 테스트

`AutoDebitRegister`를 테스트하기 위해 다음과 같은 대역을 사용할 수 있습니다:

- `CardNumberValidator` -> `StubCardNumberValidator`
- `AutoDebitInfoRepository` -> `StubAutoDebitInfoRepository`

이 대역들은 실제 카드번호 검증 기능을 구현하지 않고, 대신 단순한 구현으로 실제 구현을 대체합니다.

예시 코드:

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

실제 DB 대신 메모리 기반 대역 예시:

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

- `StubCardNumberValidator`: 외부 API 연동 없이 동작 확인. 카드번호 유효성을 흉내냄.
- `MemoryAutoDebitInfoRepository`: 실제 DB 연동 없이 CU 확인. DB 등록, 수정을 흉내냄.

이처럼 대역을 사용하면 테스트 대상에 대해 쉽고 빠르게 검증이 가능합니다.

## 대역의 종류

| 종류 | 설명 |
|-----|------|
| 스텁(Stub) | 구현을 단순한 것으로 대체. 테스트에 맞게 단순히 원하는 동작을 수행 |
| 가짜(Fake) | 제품에는 적합하지 않지만, 실제 동작하는 구현을 제공. DB 대신 메모리 등을 이용해 구현한 가짜 대역 |
| 스파이(Spy) | 호출된 내역을 기록. 기록한 내용은 테스트 결과를 검증할 때 사용. 스텁의 일종 |
| 모의(Mock) | 기대한 대로 상호작용하는지 행위를 검증. 기대한 대로 동작하지 않으면 예외를 발생할 수 있음. 스텁이자 스파이의 일종 |

## 대역 예시

회원 가입 기능 예시를 통해 대역을 확인해보겠습니다. 먼저 필요한 기능 단위에 대한 설계 내용입니다:

- `UserRegister`: 회원 가입에 대한 핵심 로직을 수행
- `WeakPasswordChecker`: 암호가 약한지 검사
- `UserRepository`: 회원 정보에 대한 CRUD 기능 제공
- `EmailNotifier`: 이메일 발송 기능 제공

### 스텁 - 약한 암호 확인 기능

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

### 가짜 - 리포지토리 구현

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

### 스파이 - 이메일 발송 여부 확인

회원 가입 성공 시 안내 메일을 발송하고, 이메일 주소로 해당 이메일을 사용했는지 확인해야 합니다. 이럴 때 스파이 대역을 사용합니다.

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

### 모의 - 스텁과 스파이 대체

스텁과 스파이 대역은 모의(Mock) 객체로 대체할 수 있습니다.

```java
public class UserRegisterMockTest {
    private WeakPasswordChecker mockPasswordChecker = Mockito.mock(WeakPasswordChecker.class);
    private EmailNotifier mockEmailNotifier = Mockito.mock(EmailNotifier.class);
    
    @DisplayName("약한 암호면 가입 실패")
    @Test
    void test1() {
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

@DisplayName("가입 완료 후 메일 전송")
@Test
void test4() {
    userRegister.register("id", "pw", "lowell@lowell.com");
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    then(mockEmailNotifier)
            .should()
            .sendRegisterEmail(captor.capture());

    String realEmail = captor.getValue();
    assertEquals("lowell@lowell.com", realEmail);
}
```

## 상황과 결과 확인을 위한 협업 대상 도출과 대역 사용

1. 제어하기 힘든 외부 상황을 별도 타입으로 분리
2. 테스트 코드는 별도로 분리한 타입의 대역을 생성
3. 생성한 대역을 테스트 대상의 생성자 등을 이용해 전달
4. 대역을 이용해 상황 구성

## 대역과 개발 속도

대역은 외부 API, DB 같은 의존하는 대상을 구현하지 않아도 테스트 대상을 완성할 수 있게 만듭니다. 이는 대기시간을 줄이며 개발 속도를 증가시킵니다.

## 모의 객체 과하게 사용하지 않기

모의 객체(Mock)는 스텁과 스파이 역할을 둘 다 지원하므로 많이 사용됩니다. 그러나 과도한 모의 객체 사용은 테스트 코드의 복잡도를 증가시킵니다.

아래 예는 회원 가입 성공을 모의 객체로 변환한 경우입니다:

```java
public class UserRegisterMockOvercaseTest {
    private UserRegister userRegister;
    private MemoryUserRepository fakeRepository = new MemoryUserRepository();
    private UserRepository mockRepository = Mockito.mock(UserRepository.class);

    @DisplayName("같은 ID가 없으면 가입 성공")
    @Test
    void test1() {
        userRegister.register("id", "pw", "email");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        then(mockRepository)
                .should()
                .save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals("id", savedUser.getId());
        assertEquals("email", savedUser.getEmail());
    }

    @DisplayName("같은 ID가 없으면 가입 성공")
    @Test
    void test2() {
        userRegister.register("id", "pw", "email");
        User savedUser = fakeRepository.findById("id");
        assertEquals("id", savedUser.getId());
        assertEquals("email", savedUser.getEmail());
    }
}
```

모의 객체를 사용할 경우 메서드 호출 시간, 전달한 인자 저장 검증 등이 필요합니다. 반면 가짜 구현을 사용하면 더 단순합니다.

모의 객체 사용을 피해야 하는 상황:
- 결과 값을 확인하는 수단으로 사용할 때
- 모의 객체 메서드 호출 여부를 결과 검증 수단으로 사용할 때