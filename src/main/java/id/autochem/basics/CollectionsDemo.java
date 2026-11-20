package id.autochem.basics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
 * Materi: Array PHP vs Collections Java, operasi dasar List/Set/Map, dan Stream API dasar.
 */
public final class CollectionsDemo {

    private CollectionsDemo() {
    }

    public static void run() {
        IO.println("\n=== 2. Generics & Collections ===");
        listBasics();
        setBasics();
        mapBasics();
        immutableCollections();
        IO.println("\n=== 3. Lambda & Stream API ===");
        streamBasics();
    }

    private static void listBasics() {
        IO.println("-- List: berurutan, boleh duplikat");
        // PHP: $codes = ['1101', '4101']; $codes[] = '5101';
        List<String> codes = new ArrayList<>();
        codes.add("1101");
        codes.add("4101");
        codes.add("5101");
        codes.add("1101");
        IO.println("codes = " + codes + ", size = " + codes.size() + ", get(1) = " + codes.get(1)
                + ", contains 4101 = " + codes.contains("4101"));
    }

    private static void setBasics() {
        IO.println("-- Set: otomatis membuang duplikat");
        List<String> codes = List.of("1101", "4101", "5101", "1101");
        // PHP: array_unique($codes)
        Set<String> unique = new HashSet<>(codes);
        Set<String> ordered = new LinkedHashSet<>(codes);
        IO.println("HashSet       = " + unique + " (urutan tidak dijamin)");
        IO.println("LinkedHashSet = " + ordered + " (urutan input dijaga)");
        IO.println("add(\"1101\") lagi -> " + ordered.add("1101") + "  <- false berarti duplikat");
    }

    private static void mapBasics() {
        IO.println("-- Map: key -> value");
        // PHP: $names['1101'] = 'Kas';
        Map<String, String> names = new LinkedHashMap<>();
        names.put("1101", "Kas");
        names.put("4101", "Pendapatan Penjualan");
        names.put("2105", "PPN Keluaran");
        IO.println("get(\"1101\") = " + names.get("1101"));
        // PHP: isset($names['9999'])
        IO.println("containsKey(\"9999\") = " + names.containsKey("9999"));
        IO.println("getOrDefault(\"9999\") = " + names.getOrDefault("9999", "(tidak ada)"));
        // PHP: foreach ($names as $k => $v)
        for (var entry : names.entrySet()) {
            IO.println("  " + entry.getKey() + " => " + entry.getValue());
        }
        IO.println("TreeMap (urut key) = " + new TreeMap<>(names));
    }

    private static void immutableCollections() {
        IO.println("-- List.of / Map.of immutable");
        List<String> fixed = List.of("1101", "4101");
        try {
            fixed.add("5101");
        } catch (UnsupportedOperationException e) {
            IO.println("List.of(...).add() -> UnsupportedOperationException");
        }
    }

    private record Item(String code, String type, long amount, boolean active) {
    }

    private static void streamBasics() {
        List<Item> items = List.of(
                new Item("1101", "Aset", 1_110_000, true),
                new Item("1102", "Aset", 500_000, false),
                new Item("4101", "Pendapatan", 1_000_000, true),
                new Item("2105", "Liabilitas", 110_000, true));

        // Laravel: collect($items)->where('active', true)->pluck('code')
        List<String> activeCodes = items.stream()
                .filter(Item::active)
                .map(Item::code)
                .toList();
        IO.println("filter + map (kode aktif)  = " + activeCodes);

        // Laravel: ->sum('amount')
        long total = items.stream().mapToLong(Item::amount).sum();
        IO.println("sum amount                = " + total);

        // Laravel: ->groupBy('type')
        Map<String, List<String>> byType = items.stream()
                .collect(Collectors.groupingBy(Item::type, TreeMap::new,
                        Collectors.mapping(Item::code, Collectors.toList())));
        IO.println("groupingBy type           = " + byType);

        // Lazy: tidak ada yang dieksekusi sampai terminal operation dipanggil
        var pipeline = items.stream().filter(item -> {
            IO.println("  memeriksa " + item.code());
            return item.active();
        });
        IO.println("pipeline dibuat, belum ada yang diperiksa...");
        IO.println("count = " + pipeline.count());
    }
}
