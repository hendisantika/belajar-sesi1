package id.autochem.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * Project : belajar-sesi1
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Link : s.id/hendisantika
 * Date: 21/11/26
 * Time: 02.36
 */
class MoneyTest {

    @Test
    void ofStringNormalizesScaleToTwoDigits() {
        assertEquals(new BigDecimal("1000.00"), Money.of("1000"));
        assertEquals(new BigDecimal("2.35"), Money.of("2.345"));
    }

    @Test
    void vatIsElevenPercent() {
        assertEquals(new BigDecimal("110000.00"), Money.vatOf(Money.of("1000000")));
        assertEquals(new BigDecimal("1.38"), Money.vatOf(Money.of("12.50")));
    }

    @Test
    void compareByValueNotScale() {
        BigDecimal a = new BigDecimal("1.0");
        BigDecimal b = new BigDecimal("1.00");
        assertFalse(a.equals(b));
        assertTrue(Money.isEqual(a, b));
    }

    @Test
    void bigDecimalIsExactWhereDoubleIsNot() {
        double viaDouble = 0.1 + 0.2;
        BigDecimal viaBigDecimal = new BigDecimal("0.1").add(new BigDecimal("0.2"));
        assertFalse(viaDouble == 0.3);
        assertTrue(Money.isEqual(viaBigDecimal, new BigDecimal("0.3")));
    }
}
