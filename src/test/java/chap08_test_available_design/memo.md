# 테스트 가능한 설계와 테스트가 어려운 코드 개선 방안

## 테스트가 어려운 코드

### 1. 하드코딩된 상수
코드 내에 경로, 주소, 설정값 등이 하드코딩되어 있으면, 테스트 시에 이를 변경하기 어려워 테스트를 복잡하게 만듭니다. 변경 가능한 인자들이 테스트에 종속되면 유지보수가 어려워집니다.

### 2. 의존 객체의 직접 생성
테스트 대상 코드에서 의존성을 갖는 객체(예: Dao 객체)를 직접 `new` 키워드로 생성하면, 해당 객체의 기능을 전부 구현해야 하므로 테스트 준비에 시간이 많이 소요됩니다.

### 3. 정적 메서드 사용
정적 메서드를 사용하는 클래스(예: `xxUtil` 클래스)를 의존할 때, 해당 메서드의 내부 구현이 변경되면 테스트 결과에 영향을 미칩니다. 이러한 의존성을 처리하기 어렵기 때문에 테스트 작성이 복잡해집니다.

### 4. 실행 시점에 따라 달라지는 결과
`LocalDate.now()`처럼 실행 시점에 따라 다른 결과를 반환하는 메서드를 사용할 경우, 테스트를 반복 실행할 때마다 결과가 달라질 수 있습니다. 이로 인해 테스트의 신뢰성이 떨어지며, 예기치 못한 오류가 발생할 수 있습니다.

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

### 5. 역할이 섞인 코드
역할이 명확히 분리되지 않은 코드는 테스트하기 어려울 수 있습니다. 예를 들어, 만료일을 확인하는 코드에 데이터베이스 접근 코드가 포함되어 있다면, 단순한 날짜 비교 로직을 테스트하려고 해도 DB 의존성을 처리해야 합니다.

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

### 그 외 테스트가 어려운 코드 사례
- 메서드 중간에 소켓 통신 코드가 포함된 경우
- 콘솔 입출력 코드가 포함된 경우
- 의존 객체가 `final`로 선언된 경우
- 테스트 대상의 소스 코드가 외부 라이브러리에 속하는 경우

---

## 테스트 가능한 설계 방안

### 1. 하드코딩된 상수 개선
생성자나 메서드 파라미터로 변경 가능한 값으로 설정하여 테스트 시 쉽게 수정할 수 있도록 만듭니다.

### 2. 의존 객체 주입 방식 사용
의존 객체를 직접 생성하는 대신, 주입 방식(생성자 주입, setter 주입)을 사용하여 의존성을 분리합니다. 이렇게 하면, 테스트 시 Mock 객체 등으로 쉽게 교체할 수 있습니다.

```java
public class ItemChecker {
    private ItemDao itemDao;
    
    public ItemChecker(ItemDao itemDao) {
        this.itemDao = itemDao;
    }
}
```

```java
public class ItemChecker {
    private ItemDao itemDao = new ItemDao();

    public void setItemDao(ItemDao itemDao) {
        this.itemDao = itemDao;
    }
}
```

### 3. 역할 분리
테스트 대상이 여러 역할을 수행하지 않도록 분리하여, 테스트 시 특정 기능에 집중할 수 있도록 개선합니다.

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
        expired = true;
    }
    item.setExpired(expired);
    return item;
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

### 4. 시간 의존성 제거
시간 또는 임의 값을 반환하는 기능을 분리하여 테스트 시점에 따라 달라지는 동작을 제어할 수 있도록 합니다.
Times 대역을 이용해 DailyBatchLoader가 사용할 일자를 지정 가능하다.

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

        int result = batchpath.getNameCount();
        return result;
    }
}
```

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

### 5. 외부 라이브러리 감싸기
외부 라이브러리나 정적 메서드를 직접 사용하지 않고 이를 감싸는 클래스를 만들어, 테스트 시 Mock으로 대체할 수 있도록 설계합니다.

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

`AuthService`를 주입받는 `LoginService`를 통해 의존성을 대역(Mock)으로 교체하여 테스트를 용이하게 만듭니다.

---

### 결론
위와 같은 설계를 통해 테스트가 어려운 코드를 개선할 수 있으며, 의존성 주입 및 역할 분리, 외부 라이브러리 감싸기 등의 기법을 사용하면 코드의 테스트 가능성을 크게 높일 수 있습니다.