package id.autochem.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
class JournalEntryTest {

    private static final LocalDate DATE = LocalDate.of(2026, 11, 21);

    @Test
    void cashSaleWithVatIsBalanced() {
        var sales = Money.of("1000000");
        var vat = Money.vatOf(sales);
        JournalEntry entry = new JournalEntry("JU-0001", DATE, "Penjualan tunai")
                .debit("1101", sales.add(vat))
                .credit("4101", sales)
                .credit("2105", vat);

        assertEquals(Money.of("1110000"), entry.totalDebit());
        assertEquals(Money.of("1110000"), entry.totalCredit());
        assertEquals(Money.ZERO, entry.difference());
        assertTrue(entry.isBalanced());
        assertDoesNotThrow(entry::ensureBalanced);
    }

    @Test
    void missingVatLineIsUnbalanced() {
        JournalEntry entry = new JournalEntry("JU-0002", DATE, "PPN lupa dicatat")
                .debit("1101", Money.of("1110000"))
                .credit("4101", Money.of("1000000"));

        assertFalse(entry.isBalanced());
        var error = assertThrows(UnbalancedJournalException.class, entry::ensureBalanced);
        assertEquals(Money.of("110000"), error.getDifference());
    }

    @Test
    void emptyOrSingleLineJournalIsNotBalanced() {
        JournalEntry entry = new JournalEntry("JU-0003", DATE, "Kosong");
        assertFalse(entry.isBalanced());
        entry.debit("1101", Money.of("1000"));
        assertFalse(entry.isBalanced());
    }

    @Test
    void decimalsAreSummedExactly() {
        JournalEntry entry = new JournalEntry("JU-0004", DATE, "Pecahan");
        for (int i = 0; i < 10; i++) {
            entry.debit("5101", Money.of("0.10"));
        }
        entry.credit("1101", Money.of("1.00"));
        assertTrue(entry.isBalanced());
    }

    @Test
    void linesCannotBeModifiedFromOutside() {
        JournalEntry entry = new JournalEntry("JU-0005", DATE, "Read-only")
                .debit("1101", Money.of("1000"))
                .credit("3101", Money.of("1000"));
        assertThrows(UnsupportedOperationException.class,
                () -> entry.getLines().add(JournalLine.debit("1101", Money.of("1"))));
    }

    @Test
    void lineMustHaveExactlyOneSide() {
        assertThrows(IllegalArgumentException.class,
                () -> new JournalLine("1101", Money.of("1000"), Money.of("1000")));
        assertThrows(IllegalArgumentException.class,
                () -> new JournalLine("1101", Money.ZERO, Money.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> JournalLine.debit("1101", Money.of("-5")));
    }
}
