package chap05_junit5;

import chap05_junit5.service.AuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AssertionsTest {

    //@DisplayName("test name")
    @Test
    void equals() {
        LocalDate d1 = LocalDate.now();
        LocalDate d2 = LocalDate.now();
        assertEquals(d1, d2);

    }

    //@DisplayName("test name")
    @Test
    void fail() {
        try {
            AuthService authService = new AuthService();
            authService.authentication(null, null);
            Assertions.fail();
        } catch (IllegalArgumentException e) {
//            throw new RuntimeException(e);
        }
    }

    @Test
    void failForException() {
        assertThrows(IllegalArgumentException.class, () -> {
            AuthService authService = new AuthService();
            authService.authentication(null, null);
        });
    }

    @Test
    void 익셉션을_이용한_추가검사() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            AuthService authService = new AuthService();
            authService.authentication(null, null);
        });
        assertTrue(thrown.getMessage().contains("id"));
    }

    //@DisplayName("test name")
    @Test
    void assert메서드_검증실패는_다음코드를_실행하지_않음() {
        assertEquals(3, 5/2); // 실패
        assertEquals(4, 2*2); // 실행하지 않음
    }

    @Test
    void assert메서드_일단_모든_검증을_실행하고_그중에_실패한_것을_확인할_때() {
        assertAll(
                () -> assertEquals(3, 5 / 2),
                () -> assertEquals(4, 2 * 2),
                () -> assertEquals(6, 11 / 2)
        );

    }

}
