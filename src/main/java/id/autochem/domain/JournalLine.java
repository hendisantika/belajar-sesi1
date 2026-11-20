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
 * Satu baris jurnal: satu akun dengan nominal di sisi debit <b>atau</b> kredit.
 * <p>
 * Dibuat sebagai {@code record} sehingga immutable dan equals/hashCode/toString dibuat otomatis.
 * Validasi dilakukan di compact constructor.
 */
public record JournalLine(String accountCode, BigDecimal debit, BigDecimal credit) {

    public JournalLine {
        if (accountCode == null || accountCode.isBlank()) {
            throw new IllegalArgumentException("Kode akun pada baris jurnal wajib diisi");
        }
        debit = Money.normalize(debit == null ? BigDecimal.ZERO : debit);
        credit = Money.normalize(credit == null ? BigDecimal.ZERO : credit);
        if (debit.signum() < 0 || credit.signum() < 0) {
            throw new IllegalArgumentException("Nominal debit/kredit tidak boleh negatif");
        }
        if ((debit.signum() > 0) == (credit.signum() > 0)) {
            throw new IllegalArgumentException(
                    "Baris jurnal akun %s harus berisi debit ATAU kredit (tepat satu sisi)".formatted(accountCode));
        }
    }

    public static JournalLine debit(String accountCode, BigDecimal amount) {
        return new JournalLine(accountCode, amount, BigDecimal.ZERO);
    }

    public static JournalLine credit(String accountCode, BigDecimal amount) {
        return new JournalLine(accountCode, BigDecimal.ZERO, amount);
    }

    public boolean isDebit() {
        return debit.signum() > 0;
    }

    /** Nominal baris ini, apa pun sisinya. */
    public BigDecimal amount() {
        return isDebit() ? debit : credit;
    }
}
