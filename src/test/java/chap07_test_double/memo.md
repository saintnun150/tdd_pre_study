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












