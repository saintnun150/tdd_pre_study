package extra.mockito;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

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

    @DisplayName("모의 객체 익셉션 발생")
    @Test
    void mockThrowTest() {
        GameNumGen genMock = mock(GameNumGen.class);
        given(genMock.generate(null))
                .willThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> genMock.generate(null));
    }

    @DisplayName("인자 매칭 처리")
    @Test
    void matchedGivenParams() {
        GameNumGen genMock = mock(GameNumGen.class);
        given(genMock.generate(GameLevel.EASY)).willReturn("123");
        String num = genMock.generate(GameLevel.NORMAL);
        assertEquals("123", num);
    }
}
