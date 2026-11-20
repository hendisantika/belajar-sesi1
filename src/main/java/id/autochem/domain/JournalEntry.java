package id.autochem.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

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
 * Jurnal umum yang terdiri dari banyak {@link JournalLine}.
 * <p>
 * Aturan bisnis "Σ debit = Σ kredit" ada di dalam objek ini ({@link #isBalanced()}),
 * bukan tersebar di luar.
 */
public final class JournalEntry {

    private final String number;
    private final LocalDate date;
    private final String description;
    private final List<JournalLine> lines = new ArrayList<>();

    public JournalEntry(String number, LocalDate date, String description) {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Nomor jurnal wajib diisi");
        }
        this.number = number;
        this.date = Objects.requireNonNull(date, "Tanggal jurnal wajib diisi");
        this.description = description;
    }

    public JournalEntry addLine(JournalLine line) {
        lines.add(Objects.requireNonNull(line, "Baris jurnal tidak boleh null"));
        return this;
    }

    /** Fluent helper: {@code entry.debit("1101", Money.of("1110000"))}. */
    public JournalEntry debit(String accountCode, BigDecimal amount) {
        return addLine(JournalLine.debit(accountCode, amount));
    }

    public JournalEntry credit(String accountCode, BigDecimal amount) {
        return addLine(JournalLine.credit(accountCode, amount));
    }

    public BigDecimal totalDebit() {
        return sum(JournalLine::debit);
    }

    public BigDecimal totalCredit() {
        return sum(JournalLine::credit);
    }

    /** Selisih debit - kredit. Nol berarti seimbang. */
    public BigDecimal difference() {
        return totalDebit().subtract(totalCredit());
    }

    /** Seimbang bila minimal 2 baris, ada nominal, dan Σ debit = Σ kredit (dibandingkan dengan compareTo). */
    public boolean isBalanced() {
        return lines.size() >= 2
                && totalDebit().signum() > 0
                && totalDebit().compareTo(totalCredit()) == 0;
    }

    /** @throws UnbalancedJournalException bila jurnal tidak seimbang */
    public void ensureBalanced() {
        if (!isBalanced()) {
            throw new UnbalancedJournalException(number, totalDebit(), totalCredit());
        }
    }

    private BigDecimal sum(Function<JournalLine, BigDecimal> side) {
        return lines.stream()
                .map(side)
                .reduce(Money.ZERO, BigDecimal::add);
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    /** View read-only: pemanggil tidak bisa menambah baris tanpa lewat {@link #addLine(JournalLine)}. */
    public List<JournalLine> getLines() {
        return List.copyOf(lines);
    }
}
