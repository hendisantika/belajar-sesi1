package id.autochem.basics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * Materi: review OOP (4 pilar), interface vs abstract class, equals/hashCode/toString.
 * Tipe-tipe di sini sengaja dibuat sederhana khusus untuk contoh; model domain sebenarnya ada di package {@code domain}.
 */
public final class OopDemo {

    private OopDemo() {
    }

    // Abstraction: kontrak perilaku (interface boleh punya default method)
    interface Describable {
        String describe();

        default String headline() {
            return "[" + describe() + "]";
        }
    }

    // Abstraction + Encapsulation: basis kode bersama, field private
    abstract static class BaseAccount implements Describable {
        private final String code;
        private final String name;

        protected BaseAccount(String code, String name) {
            this.code = code;
            this.name = name;
        }

        abstract String normalBalance();

        public String getCode() {
            return code;
        }

        @Override
        public String describe() {
            return code + " " + name + " (saldo normal " + normalBalance() + ")";
        }
    }

    // Inheritance: extends satu induk, super(...) memanggil constructor induk
    static final class AssetAccount extends BaseAccount {
        AssetAccount(String code, String name) {
            super(code, name);
        }

        @Override   // dicek compiler: salah nama method langsung error
        String normalBalance() {
            return "DEBIT";
        }
    }

    static final class RevenueAccount extends BaseAccount {
        RevenueAccount(String code, String name) {
            super(code, name);
        }

        @Override
        String normalBalance() {
            return "KREDIT";
        }
    }

    // Class biasa TANPA equals/hashCode -> dibandingkan berdasarkan referensi
    static final class AccountCodeNoEquals {
        private final String value;

        AccountCodeNoEquals(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    // record: equals, hashCode, toString dibuat otomatis berdasarkan isi
    record AccountCode(String value) {
    }

    public static void run() {
        IO.println("\n=== 4. Review OOP ===");
        polymorphism();
        equalsAndHashCode();
    }

    private static void polymorphism() {
        IO.println("-- Polymorphism: List<BaseAccount> berisi Asset & Revenue");
        List<BaseAccount> accounts = List.of(
                new AssetAccount("1101", "Kas"),
                new RevenueAccount("4101", "Pendapatan Penjualan"));
        for (BaseAccount account : accounts) {
            // method yang dipanggil sesuai objek aslinya
            IO.println("  " + account.headline());
        }
    }

    private static void equalsAndHashCode() {
        IO.println("-- equals() & hashCode()");
        String s1 = new String("1101");
        String s2 = new String("1101");
        IO.println("\"1101\" == \"1101\"      -> " + (s1 == s2) + " (beda objek)");
        IO.println("\"1101\".equals(\"1101\") -> " + s1.equals(s2) + " (isi sama)");

        Set<AccountCodeNoEquals> broken = new HashSet<>();
        broken.add(new AccountCodeNoEquals("1101"));
        broken.add(new AccountCodeNoEquals("1101"));
        IO.println("HashSet tanpa equals/hashCode -> size " + broken.size() + " " + broken + "  <- jawaban Q4");

        Set<AccountCode> correct = new HashSet<>();
        correct.add(new AccountCode("1101"));
        correct.add(new AccountCode("1101"));
        IO.println("HashSet dengan record         -> size " + correct.size() + " " + correct);
    }
}
