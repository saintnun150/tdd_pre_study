package chap07_test_double.userRegist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

public class UserRegisterMockTest {
    private UserRegister userRegister;
    private WeakPasswordChecker mockPasswordChecker = Mockito.mock(WeakPasswordChecker.class);
    private MemoryUserRepository fakeRepository = new MemoryUserRepository();
    private EmailNotifier mockEmailNotifier = Mockito.mock(EmailNotifier.class);

    @BeforeEach
    void setUp() {
        userRegister = new UserRegister(mockPasswordChecker,
                                        fakeRepository,
                                        mockEmailNotifier);
    }

    @DisplayName("약한 암호면 가입 실패")
    @Test
    void test1() {
        // pw 인자를 사용해 모의 객체의 checkPasswordWeak 메서드를 호출하면 결과로 true를 리턴
        given(mockPasswordChecker.checkPasswordWeak("pw"))
                .willReturn(true);
        assertThrows(WeakPasswordException.class, () -> {
            userRegister.register("id", "pw", "email");
        });
    }

    @DisplayName("회원 가입시 암호 검사 수행 여부")
    @Test
    void test2() {
        userRegister.register("id", "pw", "email");

        then(mockPasswordChecker)
                .should()
                .checkPasswordWeak(anyString());
    }

    @DisplayName("가입 완료 후 메일 전송")
    @Test
    void test4() {
        userRegister.register("id", "pw", "lowell@lowell.com");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        then(mockEmailNotifier)
                .should()
                .sendRegisterEmail(captor.capture());
        // 모의 객체 메서드의 인자가 captor에 담긴다.
        // sendRegisterEmail에서는 email 인자

        String realEmail = captor.getValue();
        assertEquals("lowell@lowell.com", realEmail);
    }

}
