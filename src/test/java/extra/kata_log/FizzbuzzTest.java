package extra.kata_log;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FizzbuzzTest {
//    1~100까지의 각 숫자를 한 줄씩 출력하는 프로그램을 작성하세요.
//    일반적으로 숫자 자체를 인쇄합니다.
//    3의 배수인 경우 Fizz숫자 대신 인쇄하세요.
//    5의 배수인 경우 Buzz숫자 대신 인쇄하세요.
//    3과 5의 배수인 숫자의 경우 FizzBuzz 숫자 대신 다음을 인쇄합니다.

    //@DisplayName("test name")
    @Test
    void testFizz() {
        String actual = NumberPrinter.print(3);
        Assertions.assertEquals("Fizz", actual);
    }

    @Test
    void testBuzz() {
        String actual = NumberPrinter.print(5);
        Assertions.assertEquals("Buzz", actual);
    }

    @Test
    void testFizzBuzz() {
        String actual = NumberPrinter.print(0);
        Assertions.assertEquals("FizzBuzz", actual);
    }

    @Test
    void testElse() {
        String actual = NumberPrinter.print(2);
        Assertions.assertEquals("2", actual);
    }

    //@DisplayName("test name")
    @Test
    void fizzbuzz() {
//        NumberPrinter printer = new NumberPrinter();
        for (int i = 1; i < 101; i++) {
            System.out.println("i = " + NumberPrinter.print(i));

        }
    }

    public class NumberPrinter {
        public static String print(int number) {
            StringBuilder answer = new StringBuilder();
            if (number % 3 == 0 && number % 5 == 0) {
                answer.append("FizzBuzz");
            } else if (number % 3 == 0) {
                answer.append("Fizz");
            } else if (number % 5 == 0) {
                answer.append("Buzz");
            } else {
                answer.append(number);
            }
            return answer.toString();
        }
    }
}
