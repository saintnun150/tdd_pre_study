package extra.mockito;

import chap07_test_double.userRegist.MemoryUserRepository;
import chap07_test_double.userRegist.SpyEmailNotifier;
import chap07_test_double.userRegist.StubWeakPasswordChecker;
import chap07_test_double.userRegist.UserRegister;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.then;

public class UserRegistArgumentCaptorTest {
    private UserRegister userRegister;
    private StubWeakPasswordChecker stubPasswordChecker = new StubWeakPasswordChecker();
    private MemoryUserRepository fakeRepository = new MemoryUserRepository();
    private SpyEmailNotifier spyEmailNotifier = new SpyEmailNotifier();

    @BeforeEach
    void setUp() {
        userRegister = new UserRegister(stubPasswordChecker,
                                        fakeRepository,
                                        spyEmailNotifier
        );
    }

    @DisplayName("가입 완료 후 메일 전송")
    @Test
    void test4() {
        userRegister.register("id", "pw", "lowell@lowell.com");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        then(spyEmailNotifier)
                .should()
                .sendRegisterEmail(captor.capture());

        String realEmail = captor.getValue();
        assertEquals("lowell@lowell.com", realEmail);

    }

}
