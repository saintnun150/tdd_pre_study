package chap02_calculator.passwordStrengthMeter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswordStrengthMeterTest {

    // 첫 번째 테스트: 가장 쉽거나 예외적인 케이스
    @DisplayName("모든_조건_충족")
    @Test
    void meetsAllCriteria_Then_Strong() {
        PasswordStrengthMeter meter = new PasswordStrengthMeter();
        PasswordStrength result = meter.meter("dhwnsdud12@A");
        assertEquals(PasswordStrength.STRONG, result);

        PasswordStrength result2 = meter.meter("dhwnsdud!A");
        assertEquals(PasswordStrength.STRONG, result2);
    }

    @DisplayName("길이 8자 미만 + 나머지 조건 충족")
    @Test
    void meetsOtherCriteria_except_for_length_Then_Normal() {
        PasswordStrengthMeter meter = new PasswordStrengthMeter();
        PasswordStrength result = meter.meter("abc!A@");
        assertEquals(PasswordStrength.NORMAL, result);

        PasswordStrength result2 = meter.meter("Abc!a^");
        assertEquals(PasswordStrength.NORMAL, result2);
    }


}
