package chap02_calculator.passwordStrengthMeter;

public class PasswordStrengthMeter {
    public PasswordStrength meter(String pwd) {
        if (pwd.length() < 8) {
            return PasswordStrength.NORMAL;
        }
        return PasswordStrength.STRONG;
    }
}
