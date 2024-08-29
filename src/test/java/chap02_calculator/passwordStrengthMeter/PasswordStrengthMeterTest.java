package chap02_calculator.passwordStrengthMeter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswordStrengthMeterTest {

    // condition
    // 1. 길이가 8자 이상
    // 2. 0~9 사이 숫자 포함
    // 3. 대문자 포함
    // 3개 충족 => 강함
    // 2개 충족 => 보통
    // 1개 이하 => 약함

    PasswordStrengthMeter meter = new PasswordStrengthMeter();

    private void assertionStrength(String pwd, PasswordStrength strength) {
        PasswordStrength result = meter.meter(pwd);
        assertEquals(strength, result);
    }

    // 첫 번째 테스트: 가장 쉽거나 예외적인 케이스
    @DisplayName("모든_조건_충족")
    @Test
    void meetsAllCriteria_Then_Strong() {
        assertionStrength("dhwnsdud12@A", PasswordStrength.STRONG);
        assertionStrength("dhwnsdud!2A", PasswordStrength.STRONG);
    }

    @DisplayName("길이 8자 미만 + 나머지 조건 충족")
    @Test
    void meetsOtherCriteria_except_for_length_Then_Normal() {
        assertionStrength("abc1!A@", PasswordStrength.NORMAL);
        assertionStrength("Abc2!^", PasswordStrength.NORMAL);
    }

    @DisplayName("숫자를 포함X + 나머지 조건 충족")
    @Test
    void meetsOtherCriteria_except_for_number_Then_Normal() {
        assertionStrength("abc!A@ioPA", PasswordStrength.NORMAL);
    }

    @DisplayName("값이 없는 경우")
    @Test
    void nullParam_Then_Invalid() {
        assertionStrength(null, PasswordStrength.INVALID);
        assertionStrength("", PasswordStrength.INVALID);
    }

    @DisplayName("대문자 포함X + 나머지 조건 충족")
    @Test
    void meetsOtherCriteria_except_for_Uppercase_Then_Normal() {
        assertionStrength("ab1!@iom", PasswordStrength.NORMAL);
    }

    @DisplayName("8자 이상 조건만 충족")
    @Test
    void meetsOnlyLengthCriteria_Then_Weak() {
        assertionStrength("abcdefgh", PasswordStrength.WEAK);
    }

    @DisplayName("숫자 포함 조건만 충족")
    @Test
    void meetsOnlyNumCriteria_Then_Weak() {
        assertionStrength("111111", PasswordStrength.WEAK);
    }
    @DisplayName("대문자만 포함 조건만 충족")
    @Test
    void meetsOnlyUppercaseCriteria_Then_Weak() {
        assertionStrength("ABCDE", PasswordStrength.WEAK);
    }

    @DisplayName("모든 조건 충족 X")
    @Test
    void meetsNoCriteria_Then_Weak() {
        assertionStrength("abc", PasswordStrength.WEAK);
    }





}
