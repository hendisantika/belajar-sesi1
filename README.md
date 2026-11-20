# belajar-sesi1

Kode penunjang **Sesi 01 – Core Java: Type Safety, Collections & Financial Basics**
(Enterprise Java & Spring Boot Engineering – Modul 01).

Deliverable: *mini Java console app untuk COA in-memory + generic collections + pengecekan jurnal dengan BigDecimal*.

## Prasyarat

- Java 25
- Maven 3.9+

## Menjalankan

```bash
# Tanpa build tool (multi-file source launcher, Java 22+)
java src/main/java/id/autochem/Main.java            # demo materi + hands-on
java src/main/java/id/autochem/Main.java materi     # hanya demo materi
java src/main/java/id/autochem/Main.java hands-on   # hanya 3 skenario hands-on

# Dengan Maven
mvn compile exec:java
mvn compile exec:java -Dexec.args=hands-on
mvn test
```

Kompilasi memakai `-Xlint:rawtypes,unchecked -Werror`, jadi build langsung gagal bila ada raw type
(kriteria sesi: *program dikompilasi tanpa raw type*).

## Struktur

```
src/main/java/id/autochem
├── Main.java                   # console app: demo materi + 3 skenario hands-on
├── basics/                     # demo per topik materi
│   ├── TypeSafetyDemo.java     # 1. primitive vs reference, casting, compile-time check, generics, Optional
│   ├── CollectionsDemo.java    # 2-3. List/Set/Map (PHP array vs Java), List.of, Stream API (filter, map, groupingBy, lazy)
│   ├── OopDemo.java            # 4. 4 pilar OOP, interface vs abstract class, equals/hashCode, record
│   └── BigDecimalDemo.java     # 5. double vs BigDecimal, compareTo vs equals, scale & RoundingMode, PPN 11%
├── domain/                     # model domain akuntansi
│   ├── AccountType.java        # 1xxx Aset … 5xxx Beban + saldo normal
│   ├── NormalBalance.java
│   ├── Account.java            # enkapsulasi: saldo hanya berubah lewat debit()/credit()
│   ├── JournalLine.java        # record, tepat satu sisi debit ATAU kredit
│   ├── JournalEntry.java       # isBalanced(): Σ debit = Σ kredit
│   ├── Money.java              # BigDecimal scale 2, HALF_UP, format Rupiah
│   └── UnbalancedJournalException.java
└── coa/
    ├── ChartOfAccounts.java    # List (urutan) + Set (unik) + Map (lookup) + Stream API
    ├── DuplicateAccountCodeException.java
    └── AccountNotFoundException.java
```

## Pemetaan ke materi

| Materi | Kode |
|---|---|
| Static typing, primitive/reference, casting | `basics/TypeSafetyDemo` |
| `List<Account>`, `Set<String>`, `Map<String, Account>` tanpa raw type | `coa/ChartOfAccounts`, `basics/CollectionsDemo` |
| Lambda & Stream API (`filter`, `map`, `groupingBy`) | `ChartOfAccounts#findActiveNames`, `#groupByType` |
| OOP: `Account`, `JournalEntry`, `JournalLine` | `domain/*`, `basics/OopDemo` |
| BigDecimal & debit = kredit | `domain/Money`, `JournalEntry#isBalanced`, `basics/BigDecimalDemo` |
| Cek pemahaman Q1–Q4 | Q1 `Account#debit/credit`, Q2 `Set<String> codes`, Q3 `Map<String, Account>`, Q4 `OopDemo#equalsAndHashCode` |

## Skenario hands-on

1. **Simpan & lookup akun.** Akun disimpan sesuai urutan input, lalu dicari berdasarkan kode lewat `Map` dan `Optional`.
2. **Tolak kode duplikat.** `Set.add()` mengembalikan `false`, lalu dilempar `DuplicateAccountCodeException`.
3. **Cek jurnal.** Contoh penjualan tunai Rp1.000.000 + PPN 11% yang seimbang dan bisa diposting, lalu contoh jurnal yang tidak seimbang, akun yang tidak terdaftar, dan akun nonaktif yang semuanya ditolak.
