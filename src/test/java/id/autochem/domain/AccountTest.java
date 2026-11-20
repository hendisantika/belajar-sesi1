package id.autochem.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

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
class AccountTest {

    @ParameterizedTest
    @CsvSource({"1101, ASSET", "2105, LIABILITY", "3101, EQUITY", "4101, REVENUE", "5101, EXPENSE"})
    void typeIsInferredFromFirstDigit(String code, AccountType expected) {
        assertEquals(expected, Account.of(code, "Akun " + code).getType());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "110", "11011", "6101", "A101"})
    void rejectsInvalidCode(String code) {
        assertThrows(IllegalArgumentException.class, () -> Account.of(code, "Akun"));
    }

    @Test
    void rejectsTypeThatDoesNotMatchCode() {
        assertThrows(IllegalArgumentException.class,
                () -> new Account("1101", "Kas", AccountType.REVENUE, true));
    }

    @Test
    void debitIncreasesDebitNormalAccount() {
        Account cash = Account.of("1101", "Kas");
        cash.debit(Money.of("1000"));
        cash.credit(Money.of("300"));
        assertEquals(Money.of("700"), cash.getBalance());
    }

    @Test
    void creditIncreasesCreditNormalAccount() {
        Account revenue = Account.of("4101", "Pendapatan");
        revenue.credit(Money.of("1000"));
        revenue.debit(Money.of("100"));
        assertEquals(Money.of("900"), revenue.getBalance());
    }

    @Test
    void rejectsNonPositiveAmount() {
        Account cash = Account.of("1101", "Kas");
        assertThrows(IllegalArgumentException.class, () -> cash.debit(Money.ZERO));
        assertThrows(IllegalArgumentException.class, () -> cash.credit(Money.of("-1")));
    }

    @Test
    void equalityIsBasedOnCode() {
        assertEquals(Account.of("1101", "Kas"), Account.of("1101", "Kas Kecil"));
        assertEquals(Account.of("1101", "Kas").hashCode(), Account.of("1101", "Kas Kecil").hashCode());
    }

    @Test
    void canBeDeactivated() {
        Account cash = Account.of("1101", "Kas");
        assertTrue(cash.isActive());
        cash.deactivate();
        assertFalse(cash.isActive());
    }
}
