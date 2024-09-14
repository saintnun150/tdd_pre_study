package chap07_test_double.autoDebitRegist;

import java.time.LocalDateTime;

public class AutoDebitInfo {
    private String userId;
    private String cardNumber;
    private LocalDateTime debitDate;

    public AutoDebitInfo(String userId, String cardNumber, LocalDateTime debitDate) {
        this.userId = userId;
        this.cardNumber = cardNumber;
        this.debitDate = debitDate;
    }

    public void changeCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
