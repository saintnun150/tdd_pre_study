package chap02_240828;

import org.junit.jupiter.api.Test;
import org.lowell.chap02_240828.Calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorTest {

    @Test
    void plus() {
        int result = Calculator.plus(1, 2);

        // error AssertionFailedError
        assertEquals(3, result);
        assertEquals(5, Calculator.plus(4, 1));
    }
}
