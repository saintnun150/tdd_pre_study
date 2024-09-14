package chap07_test_double.autoDebitRegist;

public class AutoDebitReq {
    private String userId;
    private String cardNumber;

    public AutoDebitReq(String userId, String number) {
        this.userId = userId;
        this.cardNumber = number;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getUserId() {
        return userId;
    }

}
