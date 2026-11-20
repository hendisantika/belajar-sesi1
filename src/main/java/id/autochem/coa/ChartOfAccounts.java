package id.autochem.coa;

import id.autochem.domain.Account;
import id.autochem.domain.AccountType;
import id.autochem.domain.JournalEntry;
import id.autochem.domain.JournalLine;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
 * Chart of Accounts (COA) in-memory.
 * <p>
 * Tiap struktur data dipilih sesuai kebutuhannya:
 * <ul>
 *     <li>{@code List<Account>} menjaga <b>urutan</b> akun sesuai urutan input.</li>
 *     <li>{@code Set<String>} menjamin <b>keunikan</b> kode akun: {@code add()} mengembalikan {@code false} bila duplikat.</li>
 *     <li>{@code Map<String, Account>} untuk <b>lookup</b> cepat berdasarkan kode (O(1)).</li>
 * </ul>
 * Semua koleksi memakai generic type, tanpa raw type.
 */
public class ChartOfAccounts {

    private final List<Account> accounts = new ArrayList<>();
    private final Set<String> codes = new HashSet<>();
    private final Map<String, Account> accountsByCode = new HashMap<>();

    /**
     * Menyimpan akun baru.
     *
     * @throws DuplicateAccountCodeException bila kode akun sudah ada
     */
    public Account add(Account account) {
        if (!codes.add(account.getCode())) {
            throw new DuplicateAccountCodeException(account.getCode());
        }
        accounts.add(account);
        accountsByCode.put(account.getCode(), account);
        return account;
    }

    /** Versi tanpa exception: {@code true} bila tersimpan, {@code false} bila kode duplikat. */
    public boolean tryAdd(Account account) {
        if (codes.contains(account.getCode())) {
            return false;
        }
        add(account);
        return true;
    }

    public boolean contains(String code) {
        return codes.contains(code);
    }

    /** Lookup aman: hasilnya Optional, bukan null. */
    public Optional<Account> findByCode(String code) {
        return Optional.ofNullable(accountsByCode.get(code));
    }

    /** @throws AccountNotFoundException bila kode tidak ditemukan */
    public Account getByCode(String code) {
        return findByCode(code).orElseThrow(() -> new AccountNotFoundException(code));
    }

    /** Semua akun sesuai urutan input (read-only). */
    public List<Account> findAll() {
        return List.copyOf(accounts);
    }

    public int size() {
        return accounts.size();
    }

    // ---------------------------------------------------------------- Stream API

    /** {@code filter}: hanya akun aktif. ≈ Laravel {@code ->where('active', true)}. */
    public List<Account> findActive() {
        return accounts.stream()
                .filter(Account::isActive)
                .toList();
    }

    /** {@code filter} + {@code map}: nama akun aktif. ≈ Laravel {@code ->where(...)->pluck('name')}. */
    public List<String> findActiveNames() {
        return accounts.stream()
                .filter(Account::isActive)
                .map(Account::getName)
                .toList();
    }

    public List<Account> findByType(AccountType type) {
        return accounts.stream()
                .filter(account -> account.getType() == type)
                .toList();
    }

    /** Pencarian nama (case-insensitive) di antara akun aktif. */
    public List<Account> searchActiveByName(String keyword) {
        String lower = keyword.toLowerCase();
        return accounts.stream()
                .filter(Account::isActive)
                .filter(account -> account.getName().toLowerCase().contains(lower))
                .toList();
    }

    /** {@code groupingBy}: akun per tipe. ≈ Laravel {@code ->groupBy('type')}, tapi hasilnya Map bertipe. */
    public Map<AccountType, List<Account>> groupByType() {
        return accounts.stream()
                .collect(Collectors.groupingBy(
                        Account::getType,
                        () -> new EnumMap<>(AccountType.class),
                        Collectors.toList()));
    }

    // ---------------------------------------------------------------- Jurnal

    /**
     * Validasi jurnal terhadap COA: setiap akun harus terdaftar &amp; aktif, lalu Σ debit = Σ kredit.
     *
     * @throws AccountNotFoundException             bila ada kode akun yang tidak terdaftar
     * @throws IllegalStateException                bila ada akun nonaktif
     * @throws id.autochem.domain.UnbalancedJournalException bila jurnal tidak seimbang
     */
    public void validate(JournalEntry entry) {
        for (JournalLine line : entry.getLines()) {
            Account account = getByCode(line.accountCode());
            if (!account.isActive()) {
                throw new IllegalStateException("Akun %s (%s) nonaktif".formatted(account.getCode(), account.getName()));
            }
        }
        entry.ensureBalanced();
    }

    /** Validasi lalu terapkan setiap baris ke saldo akun lewat method bisnis {@code debit/credit}. */
    public void post(JournalEntry entry) {
        validate(entry);
        for (JournalLine line : entry.getLines()) {
            Account account = getByCode(line.accountCode());
            if (line.isDebit()) {
                account.debit(line.debit());
            } else {
                account.credit(line.credit());
            }
        }
    }
}
