package chap02_calculator.passwordStrengthMeter;

import org.junit.platform.commons.util.StringUtils;

public class PasswordStrengthMeter {
    public PasswordStrength meter(String pwd) {
        if (StringUtils.isBlank(pwd)) {
            return PasswordStrength.INVALID;
        }

        int meetCnts = 0;
        boolean lengthEnough = pwd.length() >= 8;
        boolean containsNum = isContainsNum(pwd);
        boolean containsUpperCase = isContainsUpperCase(pwd);

        if (lengthEnough) {
            meetCnts++;
        }
        if (containsNum) {
            meetCnts++;
        }
        if (containsUpperCase) {
            meetCnts++;
        }
        if (meetCnts == 1) {
            return PasswordStrength.WEAK;
        }
        if (meetCnts == 2) {
            return PasswordStrength.NORMAL;
        }
        return PasswordStrength.STRONG;
    }

    private boolean isContainsNum(String pwd) {
        char[] pwdCharArray = pwd.toCharArray();
        for (char c : pwdCharArray) {
            if (c >= '0' && c <= '9') {
                return true;
            }
        }
        return false;
    }

    private boolean isContainsUpperCase(String pwd) {
        char[] pwdCharArray = pwd.toCharArray();
        for (char c: pwdCharArray) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }
}
