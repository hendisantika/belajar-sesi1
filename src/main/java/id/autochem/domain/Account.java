package id.autochem.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Pattern;

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
 * Akun pada Chart of Accounts.
 * <p>
 * Contoh <b>enkapsulasi</b>: semua field private. Saldo ({@code balance}) tidak bisa diubah langsung,
 * hanya lewat method bisnis {@link #debit(BigDecimal)} dan {@link #credit(BigDecimal)} yang paham aturan saldo normal.
 * Identitas akun adalah kode-nya, sehingga {@link #equals(Object)} &amp; {@link #hashCode()} hanya memakai {@code code}.
 */
public final class Account {

    private static final Pattern CODE_PATTERN = Pattern.compile("[1-5]\\d{3}");

    private final String code;
    private final String name;
    private final AccountType type;
    private boolean active;
    private BigDecimal balance;

    public Account(String code, String name, AccountType type, boolean active) {
        if (code == null || !CODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException("Kode akun harus 4 digit diawali 1-5: " + code);
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nama akun wajib diisi");
        }
        Objects.requireNonNull(type, "Tipe akun wajib diisi");
        if (AccountType.fromCode(code) != type) {
            throw new IllegalArgumentException(
                    "Kode %s tidak sesuai dengan tipe %s".formatted(code, type.getLabel()));
        }
        this.code = code;
        this.name = name.strip();
        this.type = type;
        this.active = active;
        this.balance = Money.ZERO;
    }

    /** Static factory: tipe akun disimpulkan dari digit pertama kode. */
    public static Account of(String code, String name) {
        return new Account(code, name, AccountType.fromCode(code), true);
    }

    /** Menambah sisi debit. Saldo naik untuk akun bersaldo normal debit, turun untuk akun bersaldo normal kredit. */
    public void debit(BigDecimal amount) {
        BigDecimal value = requirePositive(amount);
        balance = type.getNormalBalance() == NormalBalance.DEBIT ? balance.add(value) : balance.subtract(value);
    }

    /** Menambah sisi kredit. Saldo naik untuk akun bersaldo normal kredit, turun untuk akun bersaldo normal debit. */
    public void credit(BigDecimal amount) {
        BigDecimal value = requirePositive(amount);
        balance = type.getNormalBalance() == NormalBalance.CREDIT ? balance.add(value) : balance.subtract(value);
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    private static BigDecimal requirePositive(BigDecimal amount) {
        BigDecimal value = Money.normalize(amount);
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("Nominal harus lebih besar dari nol: " + amount);
        }
        return value;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public AccountType getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Account other && code.equals(other.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return "%s %-25s %-10s %s".formatted(code, name, type.getLabel(), active ? "aktif" : "nonaktif");
    }
}
