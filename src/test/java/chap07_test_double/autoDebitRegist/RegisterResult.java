package chap07_test_double.autoDebitRegist;

public class RegisterResult {
    private CardValidity cardValidity;

    public RegisterResult(CardValidity validity) {
        this.cardValidity = validity;
    }

    public static RegisterResult error(CardValidity validity) {
        return new RegisterResult(validity);
    }

    public static RegisterResult success() {
        return new RegisterResult(CardValidity.VALID);
    }

    public CardValidity getValidity() {
        return cardValidity;
    }
}
