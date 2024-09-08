package chap05_junit5;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("@DisplayName 테스트")
public class DisplayNameTest {

    @DisplayName("값 같은지 비교")
    @Test
    void testName1() {

    }

    @DisplayName("익셉션 발생 여부 테스트")
    @Test
    void testName2() {
    }

    @Disabled
    @DisplayName("테스트 실행안하게 처리")
    @Test
    void testName3() {
    }
}
