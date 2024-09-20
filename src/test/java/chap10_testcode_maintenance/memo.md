# 테스트 코드와 유지보수

테스트 코드도 제품 코드와 동일하게 유지보수 대상이다.

깨진 유리창 이론    

사소한 무질서를 방치하면 큰 문제로 이어질 가능성이 높다.   
-> 실패한 테스트가 점점 늘어나면 나중에는 고칠 수 없다.    
-> 실패가 발생하면 그 즉시 수정하자.


## 주의 사항

### 변수나 필드를 사용해 기댓값 표현하지 않기

값을 확인하기 위해 계속해서 변수나 필드값을 확인해야하므로 불편하다.   
-> 테스트 코드 안에서 직접 선언하자.

```java
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Test
void dateFormat() {
    LocalDate date = LocalDate.of(2024, 9, 20);
    String dateStr = formatDate(date);
    assertEquals(date.getYear() + "년" +
                         date.getMonthValue() + "월" +
                         date.getDayOfMonth() + "일", dateStr);
}

@Test
void dateFormat() {
    LocalDate date = LocalDate.of(2024, 9, 20);
    String dateStr = formatDate(date);
    assertEquals("2024년 9월 20일", dateStr);
}

class Test {
    private List<Integer> answers = Arrays.asList(1, 2, 3, 4);

    @Test
    void answerTest() {
        SurveyAnswer surveyAnswer = repository.findAnswers();
        assertEquals(answers.get(0), surveyAnswer.getAnswers().get(0));
    }
}

class Test {
    @Test
    void answerTest() {
        private List<Integer> answers = Arrays.asList(1, 2, 3, 4);
        SurveyAnswer surveyAnswer = repository.findAnswers();
        assertEquals(1, surveyAnswer.getAnswers().get(0));
    }
}

```

### 정확하게 일치하는 값으로 모의 객체 설정하지 않기


### 과도하게 구현 검증하지 않기

### 셋업(@BeforeEach)을 이용해서 중복된 상황을 설정하기 않기    
-> 각 테스트 코드마다 공통 데이터가 존재해야하는데 테스트 메서드 중 일부는 그 공통 데이터의 값을 검증한다면??    

셋업 메서드에서 설정한 데이터 값이 달라지면 데이터 값을 검증하는 테스트 메서드는 실패로 바뀔 수 있다.   
-> 테스트 메서드의 목적이나 설계가 상황 구성에 따라 달라진다면 각각의 메서드 안에서 상황 구성을 해야한다.   


#### 통합 테스트에서 데이터 공유 주의하기

#### 통합 테스트의 상황 설정을 위한 보조 클래스 사용하기


### 실행 환경이 다르다고 실패하지 않기

### 실행 시점이 다르다고 실패하지 않기

#### 랜덤하게 실패하지 않기


### 필요하지 않은 값은 설정하기 않기

#### 단위 테스트를 위한 객체 생성 보조 클래스


### 조건부로 검증하지 않기

### 통합 테스트는 필요하지 않은 범위까지 연동하지 않기

### 더 이상 쓸모 없는 테스트 코드

메서드 사용법 테스트, 테스트 커버리지 수치 등을 위해 필요한 코드는 삭제하자. 


