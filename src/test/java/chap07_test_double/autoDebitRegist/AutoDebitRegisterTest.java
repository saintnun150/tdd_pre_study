package chap07_test_double.autoDebitRegist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AutoDebitRegisterTest {
    private AutoDebitRegister register;

    @BeforeEach
    void setUp() {
        CardNumberValidator validator = new CardNumberValidator();
        AutoDebitInfoRepository repository = new AutoDebitInfoRepository();
        register = new AutoDebitRegister(validator, repository);
    }

    @DisplayName("유효한 카드 검사")
    @Test
    void test1() {
        AutoDebitReq req = new AutoDebitReq("user1", "123123123123");
        RegisterResult result = this.register.register(req);
        assertEquals(CardValidity.VALID, result.getValidity());
    }

    @DisplayName("도난 카드 검사")
    @Test
    void test2() {
        AutoDebitReq req = new AutoDebitReq("user1", "222222222222");
        RegisterResult result = this.register.register(req);
        assertEquals(CardValidity.THEFT, result.getValidity());
    }
}
