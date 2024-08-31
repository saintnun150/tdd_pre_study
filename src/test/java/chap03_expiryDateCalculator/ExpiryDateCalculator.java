package chap03_expiryDateCalculator;

import java.time.LocalDate;

public class ExpiryDateCalculator {

    public LocalDate calculateExpiryDate(LocalDate billingDate, int payAmount) {
        return billingDate.plusMonths(1);
    }

    public LocalDate calculateExpiryDate(PayData payData) {
        int addToMonths = 1;
        if (payData.getFirstBillingDate() != null) {
            LocalDate candidateExp = payData.getBillingDate().plusMonths(addToMonths);
            if (payData.getFirstBillingDate().getDayOfMonth() != candidateExp.getDayOfMonth()) {
               return candidateExp.withDayOfMonth(payData.getFirstBillingDate().getDayOfMonth());
            }
        }

        return payData.getBillingDate().plusMonths(addToMonths);
    }
}
