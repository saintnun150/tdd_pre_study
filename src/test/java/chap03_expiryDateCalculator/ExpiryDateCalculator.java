package chap03_expiryDateCalculator;

import java.time.LocalDate;
import java.time.YearMonth;

public class ExpiryDateCalculator {

    public LocalDate calculateExpiryDate(LocalDate billingDate, int payAmount) {
        return billingDate.plusMonths(1);
    }

    public LocalDate calculateExpiryDate(PayData payData) {
        int addToMonths = payData.getPayAmount() / 10_000;
        if (payData.getFirstBillingDate() != null) {
            return expiryDateUsingFirstBillingDate(payData, addToMonths);
        } else {
            return payData.getBillingDate().plusMonths(addToMonths);
        }
    }

    private LocalDate expiryDateUsingFirstBillingDate(PayData payData, int addToMonths) {
        LocalDate candidateExp = payData.getBillingDate().plusMonths(addToMonths);
        // 후보 만료일이 속한 달의 마지막 날 < 첫 납부일의 일자
        if (!isSameDayOfMonth(payData.getFirstBillingDate(), candidateExp)) {
            final int dayOfFirstBillingDate = payData.getFirstBillingDate().getDayOfMonth();
            final int dayOfCandidateDate = lastDayOfMonth(candidateExp);
            if (dayOfCandidateDate < dayOfFirstBillingDate) {
                return candidateExp.withDayOfMonth(dayOfCandidateDate);
            }
            return candidateExp.withDayOfMonth(dayOfFirstBillingDate);
        } else {
            return candidateExp;
        }
    }

    private boolean isSameDayOfMonth(LocalDate date1, LocalDate date2) {
        return date1.getDayOfMonth() == date2.getDayOfMonth();
    }
    private int lastDayOfMonth(LocalDate date) {
        return YearMonth.from(date).lengthOfMonth();
    }
}
