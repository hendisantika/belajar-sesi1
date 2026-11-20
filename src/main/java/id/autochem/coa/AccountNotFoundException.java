package id.autochem.coa;

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
 * Dilempar bila kode akun tidak ditemukan di COA.
 */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String code) {
        super("Akun dengan kode %s tidak ditemukan".formatted(code));
    }
}
