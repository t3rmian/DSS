package io.gitlab.swded.system.model.processing;

import org.junit.Test;

import java.util.Collections;

import static junit.framework.TestCase.assertEquals;

public class CalculatorTest {

    @Test
    public void log2() {
        Calculator calculator = new Calculator(Collections.emptyList());
        assertEquals(Double.NEGATIVE_INFINITY, calculator.log2(0));
        assertEquals(-1.0, calculator.log2(0.5));
        assertEquals(0.0, calculator.log2(1));
        assertEquals(1.0, calculator.log2(2));
    }
}
