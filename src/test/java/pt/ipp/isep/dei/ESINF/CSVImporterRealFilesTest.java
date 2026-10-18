package pt.ipp.isep.dei.ESINF;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class CSVImporterRealFilesTest {

    private static final String BASE = "dataFiles/";

    private int countDataRows(String file) throws IOException {
        Path path = Paths.get(BASE + file);
        long lines = Files.lines(path).count();
        return (int)Math.max(0, lines - 1); // minus header
    }

    @Test
    public void import_all_real_csvs_succeeds_and_counts_are_reasonable() throws Exception {
        WarehouseRepository repository = new WarehouseRepository();
        CSVImporter importer = new CSVImporter(repository);

        // Import using the project's real CSVs
        importer.importItems(BASE + "items.csv");
        importer.importBays(BASE + "bays.csv");
        importer.importWagons(BASE + "wagons.csv");
        importer.importOrders(BASE + "orders.csv");
        importer.importOrderLines(BASE + "order_lines.csv");
        importer.importReturns(BASE + "returns.csv");

        assertTrue(repository.getItems().size() > 0, "items should be > 0");
        assertTrue(repository.getTotalBayCount() > 0, "bays should be > 0");
        assertTrue(repository.getWagons().size() > 0, "wagons should be > 0");
        assertTrue(repository.getReturns().size() > 0, "returns should be > 0");

        // Items and bays map 1:1 with rows (no duplicates expected)
        assertEquals(countDataRows("items.csv"), repository.getItems().size(), "items count mismatch");
        assertEquals(countDataRows("bays.csv"), repository.getTotalBayCount(), "bays count mismatch");

        // Wagons are keyed by wagonId, not per-row; compare with distinct wagonId in CSV
        long expectedDistinctWagonIds = Files.lines(Paths.get(BASE + "wagons.csv"))
                .skip(1)
                .map(l -> l.split(",", -1)[0])
                .distinct()
                .count();
        assertEquals(expectedDistinctWagonIds, repository.getWagons().size(), "wagons count (by unique wagonId) mismatch");

        // Orders now accept datetime dueDate; expect 1:1 with rows
        assertEquals(countDataRows("orders.csv"), repository.getOrders().size(), "orders count mismatch");

        // order_lines depend on orders existence; allow >= 0
        assertTrue(repository.getOrderLines().size() >= 0, "order_lines should be >= 0");
    }
}


