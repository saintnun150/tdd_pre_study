package chap03_expiryDateCalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpiryDateCalculatorTest {

    // condition
    // 1. 서비스 이용하려면 매달 1만원 선불 납부. 서비스 만료일은 납부일 기준 한 달 뒤
    // 2. 2개월 이상 요금을 납부 가능
    // 3. 10만 원 납부 시 서비스 1년 제공


    private void assertExpiryDate(LocalDate billingDate, int payAmount, LocalDate exceptExpiryDate) {
        ExpiryDateCalculator calculator = new ExpiryDateCalculator();
        LocalDate realExpiryDate = calculator.calculateExpiryDate(billingDate, payAmount);
        assertEquals(exceptExpiryDate, realExpiryDate);
    }

    private void assertExpiryDate(PayData payData, LocalDate exceptExpiryDate) {
        ExpiryDateCalculator calculator = new ExpiryDateCalculator();
        LocalDate realExpiryDate = calculator.calculateExpiryDate(payData);
        assertEquals(exceptExpiryDate, realExpiryDate);
    }

    // 새로운 테스트
    @DisplayName("만원 납부 시 한달 뒤 만료일")
    @Test
    void pay_10000_won() {
        // 정해진 값 리턴
        LocalDate billingDate = LocalDate.of(2019, 3, 1);
        int payAmount = 10_000;

        ExpiryDateCalculator calculator = new ExpiryDateCalculator();
        LocalDate expiryDate = calculator.calculateExpiryDate(billingDate, payAmount);

        assertEquals(LocalDate.of(2019, 4, 1), expiryDate);

        // 비교적 단순한 예는 바로 구현하자
        LocalDate billingDate2 = LocalDate.of(2019, 5, 5);
        int payAmount2 = 10_000;

        ExpiryDateCalculator calculator2 = new ExpiryDateCalculator();
        LocalDate expiryDate2 = calculator2.calculateExpiryDate(billingDate2, payAmount2);
        assertEquals(LocalDate.of(2019, 6, 5), expiryDate2);

        // 위 검증 테스트 코드를 보면 중복 부분이 많지만 무엇을 했는지도 코드에서 보여주기 때문에
        // 마냥 중복 코드를 제거하는 것만이 정답은 아니다.
    }

    @DisplayName("만원 납부 시 한달 뒤 만료일_중복제거")
    @Test
    void pay_10000_won_remove_duplicate() {


        assertExpiryDate(PayData.builder()
                                .billingDate((LocalDate.of(2019, 3, 1)))
                                .payAmount(10_000)
                                .build(),
                         LocalDate.of(2019, 4, 1));

        assertExpiryDate(PayData.builder()
                                .billingDate((LocalDate.of(2019, 5, 5)))
                                .payAmount(10_000)
                                .build(),
                         LocalDate.of(2019, 6, 5));
    }

    // 예외 상황 처리 1
    @DisplayName("납부일과_한 달 뒤 일자가 같지 않음")
    @Test
    void do_not_matched_billing_date_after_expiry_date() {
        // 2019-01-31, 10,000원 -> 2019-02-28
        // 2019-05-31, 10,000원 -> 2019-06-30
        // 2020-01-31, 10,000원 -> 2019-02-29
        // 넘어가는 달의 일자가 맞지 않음

        assertExpiryDate(PayData.builder()
                                .billingDate((LocalDate.of(2019, 1, 31)))
                                .payAmount(10_000)
                                .build(),
                         LocalDate.of(2019, 2, 28));

        assertExpiryDate(PayData.builder()
                                .billingDate((LocalDate.of(2019,5,31)))
                                .payAmount(10_000)
                                .build(),
                         LocalDate.of(2019,6,30));

        assertExpiryDate(PayData.builder()
                                .billingDate((LocalDate.of(2020,1,31)))
                                .payAmount(10_000)
                                .build(),
                         LocalDate.of(2020,2,29));

        // 날짜 라이브러리에서 자동으로 각 월에 따라 날짜 계산을 알아서 해줌
        // plusMonths
    }

    // 예외 상황 처리 2
    @DisplayName("첫납부일과 만료일의일자가 같지 않은 경우 만원 납부")
    @Test
    void do_not_matched_first_billing_date_after_expiry_date() {

        // 쉬운 조건상황
        // n만원 -> n달뒤 만료일 설정

        // 예외상황
        // 첫 납부 2019-01-31 -> 만료되는 2029-02-28에 1만 원 납부 -> 다음 만료일 2019-03-31
        // 첫 납부 2019-01-30 -> 만료되는 2029-02-28에 1만 원 납부 -> 다음 만료일 2019-03-30
        // 첫 납부 2019-05-31 -> 만료되는 2029-06-30에 1만 원 납부 -> 다음 만료일 2019-07-31

        // 쉬운 조건상황 vs 예외상황 테스트 우선순위 선택시
        // 첫 번째 테스트 조건이 첫 납부일을 기준으로 만료일에 대한 코드를 작성했기 때문에
        // 비교적 현재 조건을 만족시키고 새로운 예외상황까지 처리할 수 있는 후자를 선택하자.

        assertExpiryDate(PayData.builder()
                                .firstBillingDate(LocalDate.of(2019, 1, 31))
                                .billingDate((LocalDate.of(2019, 2, 28)))
                                .payAmount(10_000)
                                .build(),
                         LocalDate.of(2019, 3, 31));


        PayData payData2 = PayData.builder()
                                  .firstBillingDate(LocalDate.of(2019, 1, 30))
                                  .billingDate(LocalDate.of(2019, 2, 28))
                                  .payAmount(10_000)
                                  .build();
        assertExpiryDate(payData2, LocalDate.of(2019, 3, 30));

        PayData payData3 = PayData.builder()
                                  .firstBillingDate(LocalDate.of(2019, 5, 31))
                                  .billingDate(LocalDate.of(2019, 6, 30))
                                  .payAmount(10_000)
                                  .build();
        assertExpiryDate(payData3, LocalDate.of(2019, 7, 31));
    }

    // 새로운 테스트 1
    @DisplayName("지불하는 서비스 이용료가 달라질 때 만료일이 달라짐")
    @Test
    void expiryDate_if_change_billing_amount() {
        // n만원 -> n달뒤 만료일 설정
        assertExpiryDate(
                PayData.builder()
                       .billingDate(LocalDate.of(2019, 3, 1))
                       .payAmount(20_000)
                       .build(),
                LocalDate.of(2019, 5, 1));

        assertExpiryDate(
                PayData.builder()
                       .billingDate(LocalDate.of(2019, 3, 1))
                       .payAmount(30_000)
                       .build(),
                LocalDate.of(2019, 6, 1));

        assertExpiryDate(
                PayData.builder()
                       .billingDate(LocalDate.of(2021, 7, 15))
                       .payAmount(50_000)
                       .build(),
                LocalDate.of(2021, 12, 15));
    }

    // 예외 상황 처리 1
    @DisplayName("지불하는 서비스 이용료가 달라질 때 만료일이 달라짐+첫 납부일과 만료일자가 다를 때")
    @Test
    void expiryDate_if_change_billing_amount_plus_not_match_billing_date() {
        assertExpiryDate(
                PayData.builder()
                       .firstBillingDate(LocalDate.of(2019, 1, 31))
                       .billingDate(LocalDate.of(2019, 2, 28))
                       .payAmount(20_000)
                       .build(),
                LocalDate.of(2019, 4, 30));

        assertExpiryDate(
                PayData.builder()
                       .firstBillingDate(LocalDate.of(2019, 3, 31))
                       .billingDate(LocalDate.of(2019, 4, 30))
                       .payAmount(30_000)
                       .build(),
                LocalDate.of(2019, 7, 31));
    }



}
