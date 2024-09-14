package chap07_test_double.userRegist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.then;

public class UserRegisterMockOvercaseTest {
    private UserRegister userRegister;
    private MemoryUserRepository fakeRepository = new MemoryUserRepository();
    private UserRepository mockRepository = Mockito.mock(UserRepository.class);

//    @BeforeEach
//    void setUp() {
//        userRegister = new UserRegister(mockPasswordChecker,
//                                        mockRepository,
//                                        mockEmailNotifier);
//    }

    @DisplayName("같은 ID가 없으면 가입 성공")
    @Test
    void test1() {
        userRegister.register("id", "pw", "email");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        then(mockRepository)
                .should()
                .save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals("id", savedUser.getId());
        assertEquals("email", savedUser.getEmail());
    }

    @DisplayName("같은 ID가 없으면 가입 성공")
    @Test
    void test2() {
        userRegister.register("id", "pw", "email");
        User savedUser = fakeRepository.findById("id");
        assertEquals("id", savedUser.getId());
        assertEquals("email", savedUser.getEmail());

    }


}
