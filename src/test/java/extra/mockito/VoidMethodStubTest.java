package extra.mockito;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

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
