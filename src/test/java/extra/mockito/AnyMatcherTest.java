package extra.mockito;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class AnyMatcherTest {
    @DisplayName("임의의 값에 일치하도록 설정")
    @Test
    void anyMatchTest() {
        GameNumGen genMock = mock(GameNumGen.class);
        given(genMock.generate(any()))
                .willReturn("456");

        String num = genMock.generate(GameLevel.EASY);
        assertEquals("456", num);

        String num2 = genMock.generate(GameLevel.NORMAL);
        assertEquals("456", num2);

    }

    @DisplayName("스텁을 설정할 메서드의 인자가 두 개 이상일 때")
    @Test
    void overTwoParamsAnyMatchTest() {
        List<String> mockList = mock(List.class);
        given(mockList.set(anyInt(), "123")).willReturn("456");
        String old = mockList.set(5, "123");
    }

    @DisplayName("스텁을 설정할 메서드의 인자가 두 개 이상인데 임의 값 일치와 정확한 값 일치가 필요할 때")
    @Test
    void mixAnyAndEq() {
        List<String> mockList = mock(List.class);
        given(mockList.set(anyInt(), eq("123"))).willReturn("456");
        String old = mockList.set(5, "123");
        assertEquals("456", old);
    }
}
