package chap07_test_double.userRegist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRegisterTest {
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

    @DisplayName("약한 암호면 가입 실패")
    @Test
    void test1() {
        // 약한 암호로 응답하도록 설정
        stubPasswordChecker.setWeak(true);
        assertThrows(WeakPasswordException.class, () -> {
            userRegister.register("id", "pw", "email");
        });
    }

    @DisplayName("이미 같은 ID가 존재하면 가입 실패")
    @Test
    void test2() {
        fakeRepository.save(new User("id", "pw1", "lowell@lowell.com"));
        assertThrows(DuplicateIdException.class, () -> {
            userRegister.register("id", "pw2", "lowell2@lowell.com");
        });
    }

    @DisplayName("같은 ID가 없으면 가입 성공")
    @Test
    void test3() {
        userRegister.register("id", "pw", "email");
        User savedUser = fakeRepository.findById("id");
        assertEquals("id", savedUser.getId());
        assertEquals("email", savedUser.getEmail());
    }

    @DisplayName("가입 완료 후 메일 전송")
    @Test
    void test4() {
        userRegister.register("id", "pw", "lowell@lowell.com");
        assertTrue(spyEmailNotifier.isNotified());
        assertEquals("lowell@lowell.com", spyEmailNotifier.getEmail());
    }

}
