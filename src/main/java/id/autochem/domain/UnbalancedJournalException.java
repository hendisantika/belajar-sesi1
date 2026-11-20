package id.autochem.domain;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * Project : belajar-sesi1
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Link : s.id/hendisantika
 * Date: 21/11/26
 * Time: 02.32
 */
/**
 * Dilempar bila total debit tidak sama dengan total kredit.
 */
public class UnbalancedJournalException extends RuntimeException {

    private final BigDecimal difference;

    public UnbalancedJournalException(String journalNumber, BigDecimal totalDebit, BigDecimal totalCredit) {
        super("Jurnal %s tidak seimbang: debit %s, kredit %s, selisih %s".formatted(
                journalNumber, Money.format(totalDebit), Money.format(totalCredit),
                Money.format(totalDebit.subtract(totalCredit))));
        this.difference = totalDebit.subtract(totalCredit);
    }

    public BigDecimal getDifference() {
        return difference;
    }
}
