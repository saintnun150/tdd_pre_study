# 테스트 코드 구성 및 방법론

## 1. 기능 테스트 상황
기능은 주어진 상황에 따라 다르게 동작한다. 이를 효과적으로 테스트하기 위해 다양한 상황을 가정하여 테스트 코드를 작성한다.

## 2. 테스트 코드 구성 요소: `given`, `when`, `then`
테스트 코드는 일반적으로 아래 세 가지 요소로 구성된다:

- **given**: 상황 설정
- **when**: 실행
- **then**: 결과 확인

아래는 `@BeforeEach` 메서드를 사용하여 사전 상황을 설정하는 예시이다:

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

private BbGame game;

@BeforeEach
void givenGame() {
    game = new BbGame("456");
}

@Test
void exactMatch() {
    // when
    Score score = game.guess("456");
    // then
    assertEquals(3, score.strikes());
    assertEquals(0, score.balls());
}

```
실행 결과(`then`)는 보통 리턴 값을 통해 확인하지만, 리턴값이 없거나 예외가 발생하는 경우에도 테스트는 적절하게 구성되어야 한다.

> **주의**:  
> 반드시 `given`, `when`, `then` 구조를 따라야 하는 것은 아니다. 상황에 따라 유연하게 사용할 수 있다.

## 3. 외부 상황과 외부 결과
파일 리소스와 같은 외부 요인을 사용하는 경우, 테스트 리소스는 반드시 버전 관리되어야 한다.
그렇지 않다면 테스트가 불안정해질 수 있다. 또는 명시적으로 외부 파일을 생성하도록 처리해라.

```java
@Test
    void dataFileSumTest2() {
        // 외부 상황을 명시적으로 구성
        givenDataFile("resource/datafile.txt", "1", "2", "3");
        File file = new File("resource/datafile.txt");
       long sum = MathUtils.sum(file);
        assertEquals(10L, sum);
        
    }

    private void givenDataFile(String path, String... lines) {
        try {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            Files.write(filePath, Arrays.asList(lines));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
```

## 4. 외부 상태가 테스트 결과에 영향을 주지 않게 하기
테스트는 언제 실행하든 동일한 결과를 보장해야 한다.  
특히 **DB 데이터 삽입 테스트**에서 이러한 문제가 자주 발생한다.

예를 들어, 이전 테스트에서 삽입된 데이터가 남아있으면 **중복 오류**가 발생할 수 있다.  
이를 방지하기 위해 외부 상태를 테스트 전후로 **원래 상태로 롤백**하거나, 테스트 데이터를 **고유한 값으로 설정**하는 방법을 사용할 수 있다.


## 5. 외부 상태와 테스트의 어려움
DB나 외부 API와 같은 외부 요인은 100% 통제할 수 없으므로, 이를 테스트하는 데 어려움이 따를 수 있다.   
이러한 외부 요인은 **대역(mock)** 을 사용하여 처리한 후, 나중에 실제 요인으로 대체하는 방법을 사용할 수 있다.


