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
 * Dilempar bila kode akun sudah terdaftar di COA.
 */
public class DuplicateAccountCodeException extends RuntimeException {

    public DuplicateAccountCodeException(String code) {
        super("Kode akun %s sudah terdaftar".formatted(code));
    }
}
