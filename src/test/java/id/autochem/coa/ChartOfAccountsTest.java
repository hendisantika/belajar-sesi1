package id.autochem.coa;

import id.autochem.domain.Account;
import id.autochem.domain.AccountType;
import id.autochem.domain.JournalEntry;
import id.autochem.domain.Money;
import id.autochem.domain.UnbalancedJournalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

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
class ChartOfAccountsTest {

    private static final LocalDate DATE = LocalDate.of(2026, 11, 21);

    private ChartOfAccounts coa;

    @BeforeEach
    void setUp() {
        coa = new ChartOfAccounts();
        coa.add(Account.of("1101", "Kas"));
        coa.add(Account.of("2105", "PPN Keluaran"));
        coa.add(Account.of("4101", "Pendapatan Penjualan"));
        coa.add(new Account("5199", "Beban Lama", AccountType.EXPENSE, false));
    }

    @Test
    void savesAccountsInInsertionOrder() {
        coa.add(Account.of("1102", "Bank"));
        assertEquals(List.of("1101", "2105", "4101", "5199", "1102"),
                coa.findAll().stream().map(Account::getCode).toList());
    }

    @Test
    void looksUpAccountByCode() {
        assertEquals("Kas", coa.findByCode("1101").orElseThrow().getName());
        assertTrue(coa.findByCode("9999").isEmpty());
        assertThrows(AccountNotFoundException.class, () -> coa.getByCode("9999"));
    }

    @Test
    void rejectsDuplicateCode() {
        assertThrows(DuplicateAccountCodeException.class, () -> coa.add(Account.of("1101", "Kas Kecil")));
        assertFalse(coa.tryAdd(Account.of("1101", "Kas Kecil")));
        assertEquals(4, coa.size());
        assertEquals("Kas", coa.getByCode("1101").getName());
    }

    @Test
    void filtersActiveAccountsWithStream() {
        assertEquals(List.of("Kas", "PPN Keluaran", "Pendapatan Penjualan"), coa.findActiveNames());
        assertEquals(3, coa.findActive().size());
    }

    @Test
    void groupsAccountsByType() {
        var grouped = coa.groupByType();
        assertEquals(1, grouped.get(AccountType.ASSET).size());
        assertEquals(1, grouped.get(AccountType.EXPENSE).size());
        assertFalse(grouped.containsKey(AccountType.EQUITY));
    }

    @Test
    void postsBalancedJournalToAccountBalances() {
        JournalEntry entry = new JournalEntry("JU-0001", DATE, "Penjualan tunai")
                .debit("1101", Money.of("1110000"))
                .credit("4101", Money.of("1000000"))
                .credit("2105", Money.of("110000"));

        coa.post(entry);

        assertEquals(Money.of("1110000"), coa.getByCode("1101").getBalance());
        assertEquals(Money.of("1000000"), coa.getByCode("4101").getBalance());
        assertEquals(Money.of("110000"), coa.getByCode("2105").getBalance());
    }

    @Test
    void rejectsUnbalancedJournalWithoutChangingBalances() {
        JournalEntry entry = new JournalEntry("JU-0002", DATE, "Tidak seimbang")
                .debit("1101", Money.of("1110000"))
                .credit("4101", Money.of("1000000"));

        assertThrows(UnbalancedJournalException.class, () -> coa.post(entry));
        assertEquals(Money.ZERO, coa.getByCode("1101").getBalance());
    }

    @Test
    void rejectsJournalWithUnknownOrInactiveAccount() {
        JournalEntry unknown = new JournalEntry("JU-0003", DATE, "Tidak terdaftar")
                .debit("5999", Money.of("1000"))
                .credit("1101", Money.of("1000"));
        JournalEntry inactive = new JournalEntry("JU-0004", DATE, "Nonaktif")
                .debit("5199", Money.of("1000"))
                .credit("1101", Money.of("1000"));

        assertThrows(AccountNotFoundException.class, () -> coa.validate(unknown));
        assertThrows(IllegalStateException.class, () -> coa.validate(inactive));
    }
}
