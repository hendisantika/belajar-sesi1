package id.autochem.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

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
 * Utilitas nominal uang berbasis {@link BigDecimal}.
 * <ul>
 *     <li>Selalu dibuat dari String / long, bukan dari double.</li>
 *     <li>Scale 2 digit dan {@link RoundingMode#HALF_UP} selalu eksplisit.</li>
 * </ul>
 */
public final class Money {

    public static final int SCALE = 2;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(SCALE, ROUNDING);
    /** Tarif PPN 11%. */
    public static final BigDecimal VAT_RATE = new BigDecimal("0.11");

    private static final Locale INDONESIA = Locale.of("id", "ID");

    private Money() {
        // utility class, tidak boleh di-instansiasi
    }

    /** Membuat nominal dari String, mis. {@code Money.of("1000000")}. */
    public static BigDecimal of(String amount) {
        return normalize(new BigDecimal(amount));
    }

    /** Membuat nominal dari long, mis. {@code Money.of(1_000_000L)}. */
    public static BigDecimal of(long amount) {
        return normalize(BigDecimal.valueOf(amount));
    }

    /** Menyeragamkan scale menjadi 2 digit dengan pembulatan HALF_UP. */
    public static BigDecimal normalize(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Nominal tidak boleh null");
        }
        return amount.setScale(SCALE, ROUNDING);
    }

    /** Menghitung PPN 11% dari DPP (dasar pengenaan pajak). */
    public static BigDecimal vatOf(BigDecimal base) {
        return normalize(base.multiply(VAT_RATE));
    }

    /** Membandingkan nilai dengan compareTo, bukan equals (1.0 vs 1.00). */
    public static boolean isEqual(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) == 0;
    }

    /** Format Rupiah, mis. {@code Rp1.110.000,00}. */
    public static String format(BigDecimal amount) {
        return String.format(INDONESIA, "Rp%,.2f", amount);
    }
}
