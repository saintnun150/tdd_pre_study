package chap07_test_double.autoDebitRegist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AutoDebitRegisterTest {
    private AutoDebitRegister register;
    private StubCardNumberValidator stubValidator;
    private StubAutoDebitInfoRepository stubRepository;

    @BeforeEach
    void setUp() {
        stubValidator = new StubCardNumberValidator();
        stubRepository = new StubAutoDebitInfoRepository();
        register = new AutoDebitRegister(stubValidator, stubRepository);
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

    @DisplayName("stub - 유효하지 않은 카드 검사")
    @Test
    void test3() {
        stubValidator.setInvalidNo("333333333333");
        AutoDebitReq req = new AutoDebitReq("user1", "333333333333");
        RegisterResult result = register.register(req);
        assertEquals(CardValidity.INVALID, result.getValidity());

    }

    @DisplayName("stub - 도난 카드 검사")
    @Test
    void test4() {
        stubValidator.setTheftNo("444444");
        AutoDebitReq req = new AutoDebitReq("user1", "444444");
        RegisterResult result = register.register(req);
        assertEquals(CardValidity.THEFT, result.getValidity());
    }
}
