# 테스트 가능한 설계

## 테스트가 어려운 코드

### 하드 코딩된 상수
테스트 코드 안에 경로, 주소 등 변경할 수 있는 인자들이 하드코딩 되어있다면 테스트를 어렵게 만든다.

### 의존 객체를 직접 생성
Dao 같은 의존 대상을 테스트하는 대상에 new 인스턴스로 직접 생성한다면 테스트를 할 때
해당 의존 대상의 기능을 온전히 구현해야한다. 이는 테스트에 필요한 시간을 증가시킨다.

### 정적 메서드 사용
xxUtil class 같은 정적 메서드 클래스를 의존할 때 해당 정적 메서드 정보가 변경되거나 기능상 문제가 생기면
테스트를 어렵게 만든다.

### 실행 시점에 따라 달라지는 결과
테스트 하려는 기능이 특정 시점을 체크한다고 했을 때 LocalData.now() 같이 호출 시점을 반환하는 값으로 작성하게되면
실행 시점에 따라 테스트 결과가 달라지게 되어 테스트에 오동작이 생길 수 있다.

```java
import java.time.LocalDate;

public void checkExpiredDate() {
    Item item = itemDao.findById(id);
    LocalDate now = LocalDate.now();
    if (item.isFinished(now)) {
        throw new IllegalStateException();
    }    
}
```

### 역할이 섞여 있는 코드

```java
import java.time.LocalDate;

public void checkExpiredDate() {
    Item item = itemDao.findById(id);
    LocalDate now = LocalDate.now();
    if (item.isFinished(now)) {
        throw new IllegalStateException();
    }    
}
```

위 메서드는 Item의 만료일만 체크하기 힘들다. 그 이유는 DB에서 Item 조회를 위한 itemDao ``대역``이 필요하기 때문이다.
단순히 날짜를 비교하는 것은 DB에서 Item을 조회하는 것과 상관이 없다.

### 그 외 테스트가 어려운 코드
* 메서드 중간에 소켓 통신 코드
* 콘솔 입출력 코드
* 의존 대상 클래스나 메서드가 final일 때 대역으로 대체하기 어려움
* 테스트 대상의 소스를 소유하고 있을 때 수정이 어려움 (ex. 라이브러리 사용)

## 테스트 가능한 설계

### 하드 코딩된 상수
생성자(ex. setter), 메서드 파라미터로 변경

### 의존 객체를 직접 생성
직접 생성이 아닌 주입 받는 형태로 변경

```java
public class ItemChecker {
    private ItemDao itemDao;
    
    public Item(ItemDao itemDao) {
        this.itemDao = itemDao;
    }
}

// 생성자가 없을 때
public class ItemChecker {
    private ItemDao itemDao = new ItemDao();

    public void setItemDao(ItemDao itemDao) {
        this.itemDao = itemDao;
    }
}
```
위와 같이 의존 대상을 주입 받을 수 있다면 그 대상을 대역으로 쉽게 변경가능하다.

```java
public class ItemExpiredTest {
    // 대역 생성
    private MemoryItemDao memoryItemDao = new MemoryItemDao();
    
    @Test
    void checkExpire() {
        ItemChecker checker = new ItemChecker();
        checker.setItemDao(memoryItemDao); // 대역 교체
        
        memoryItemDao.findAll(); // 대역을 통한 검증
    }
    
}
```

### 역할이 섞여 있는 코드
테스트 대상의 특정 기능 확인에 맞지 않는 코드는 분리하여 테스트를 용이하게 하자.

```java
import java.time.LocalDate;

public boolean checkExpiredDate() {
    Item item = itemDao.findById(id);
    LocalDate now = LocalDate.now();
    boolean isExpired = false;
    if (item.isFinished(now)) {
        isExpired = true;
    }
    item.setExpired(isExpired);
}

public Item checkItemState(Item item, LocalDate now, boolean expired) {
    if (item.isFinished(now)) {
        isExpired = true;
    }
    item.setExpired(isExpired);
    return item;
}
```
이제 대역은 Item이 만료되었으면 상태만 변경하는 기능만 테스트가 가능하다.

또한 시간이나 임의 값 생성 기능도 분리할 수 있다.

분리 전
```java
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DailyBatchLoader {
    private String basePath = ".";

    public int load() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        Path batchpath = Paths.get(basePath, date.format(formatter), "batch.txt");

        // ex. batchPath에서 NameCount 반환
        int result = batchpath.getNameCount();
        return result;
    }
}
```

분리 후
```java
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DailyBatchLoader {
    private String basePath = ".";
    private Times times = new Times();
    
    public void setTimes(Times times) {
        this.times = times;
    }

    public int load() {
        LocalDate date = times.today();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        Path batchpath = Paths.get(basePath, date.format(formatter), "batch.txt");

        // ex. batchPath에서 NameCount 반환
        int result = batchpath.getNameCount();
        return result;
    }
}
```
위 코드는 Times 대역을 이용해 DailyBatchLoader가 사용할 일자를 지정 가능하다.
임의 값도 비슷하다. 임의 값을 제공하는 라이브러리를 직접 사용하지 말고 별도로 분리한 타입을 사용해 대역으로
처리해서 테스트를 가능하게 만들 수 있다.

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

public class DailyLoaderTest {
    private Times mockTimes = Mockito.mock(Times.class);
    private final DailyBatchLoader loader = new DailyBatchLoader();

    @BeforeEach
    void setUp() {
        loader.setBasePath("src/test/resources");
        loader.setTimes(mockTimes);
    }

    @Test
    void loadCount() {
        given(mockTimes.today())
                .willReturn(LocalDate.of(2024, 9, 15));
        int ret = loader.load();
        assertEquals(3, ret);
    }
}
```

외부 라이브러리는 직접 사용하지 말고 감싸서 사용하자.

아래 예시는 로그인 성공 결과를 확인하는 코드다.

```java
public LoginResult login(String id, String pw) {
    int res = 0;
    boolean authorized = AuthUtil.authorize(authKey);
    if (authorized) {
        res = AuthUtil.authenticate(id, pw);
    } else {
        res = -1;
    }
    
    if (res == -1) {
        return LoginResult.badAuthKey();
    }
    
    if (res == 1) {
        Customer c = customerRepository.findOne(id);
        return LoginResult.authenticated(c);
    } else {
        return LoginResult.fail(res);
    }
}
```
위 코드에서 AuthUtil 클래스 안에 외부 라이브러리가 포함되어 있다고 가정하자.
AuthUtil에서 제공하는 authorize, authenticate 메서드는 정적 메서드로 그 결과에 대해 컨트롤 하기가 힘들고
각각을 대역으로 
이 경우에 AuthUtil을 감싸는 AuthService라는 타입을 만든다면 테스트에서 이를 대역으로 대체할 수 있다.

```java
public class AuthService {
    private String authKey = "apiKey";
    
    public int authenticate(String id, String pw) {
        boolean authorized = AuthUtil.authorize(authKey);
        if (authorized) {
            return AuthUtil.authenticate(id, pw);
        } else {
            return -1;
        }
    }
}
```
이제 AuthService를 주입받은 LoginService를 살펴보자
```java
public class LoginService {
    private AuthService authService = new AuthService();
    
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public LoginResult login(String id, String pw) {
        int res = authService.authenticate(id, pw);
        if (res == -1) {
            return LoginResult.badAuthKey();
        }

        if (res == 1) {
            Customer c = customerRepository.findOne(id);
            return LoginResult.authenticated(c);
        } else {
            return LoginResult.fail(res);
        }
    }
}
```
이렇게 AuthService를 대역으로 대체하면 authenticate 결과에 따라 LoginService가 올바르게 동작하는지
검증하는 코드를 만들 수 있다.
final 클래스, 메서드에서도 이와 동일한 기법을 적용할 수 있다.








