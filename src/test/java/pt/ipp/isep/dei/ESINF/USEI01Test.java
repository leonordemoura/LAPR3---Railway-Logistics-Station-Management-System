package pt.ipp.isep.dei.ESINF;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ipp.isep.dei.ESINF.domain.*;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.InventoryService;
import pt.ipp.isep.dei.ESINF.services.impl.InventoryServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for US01 - Unloading operations with FEFO/FIFO logic
 * 
 * Acceptance Criteria:
 * 1. Stock initialization/unloading wagons - boxes ordered by:
 *    (a) expiry date (earliest first; null last)
 *    (b) receivedAt (oldest first)
 *    (c) boxId ASC (tie-break)
 * 2. Dispatch operation consumes from front, supports partial dispatch across multiple bays
 * 3. Relocation updates location only, inserts in correct FEFO position
 */
public class USEI01Test {

    @TempDir
    Path tempDir; // para nao ter que criar ficheiros CSV reais

    private WarehouseRepository repository;
    private InventoryService inventoryService;
    private CSVImporter importer;
    private String warehouseId = "W1";
    private int aisle = 1;
    private int bay1 = 1;
    private int bay2 = 2;

    @BeforeEach
    public void setUp() {
        repository = new WarehouseRepository();
        inventoryService = new InventoryServiceImpl(repository);
        importer = new CSVImporter(repository);

        Warehouse warehouse = new Warehouse(warehouseId, "Test Warehouse", null);
        repository.addWarehouse(warehouse);
        
        Aisle aisleObj = new Aisle(aisle, warehouseId);
        repository.addAisle(warehouseId, aisleObj);
        
        repository.addBay(warehouseId, aisle, bay1, 10);
        repository.addBay(warehouseId, aisle, bay2, 10);
        
        // Add a test item
        Item item = new Item("SKU001", "Test Item", "Category", "unit", 1.0, 1.0);
        repository.addItem(item);
    }

    //AC1
    @Test
    public void test_AC1d_load_boxes_from_wagon_csv() throws Exception {
        // Simular conteúdo do CSV (wagon.csv)
        Path wagonsFile = createWagonsCSV(
                "WagonId,BoxId,SKU,Quantity,ExpiryDate,ReceivedAt",
                "W1,BOX001,SKU001,100,2025-12-31,2025-01-15T10:30:00"
        );

        importer.importWagons(wagonsFile.toString());

        assertTrue(repository.boxIdExists("BOX001"));

        List<Box> boxesInBay = repository.getBoxesInBay("W1", 1, 1);

        boolean boxFound = boxesInBay.get(0).getBoxId().equals("BOX001");

        assertTrue(boxFound, "Box should be in warehouse W1, aisle 1, bay 1");
    }


    // AC1: Test FEFO/FIFO ordering during unloading
    @Test
    public void test_AC1a_boxes_ordered_by_expiry_date() {
        //different expiry dates
        Date date1 = new Date(2025,9,12);
        Date date2 = new Date(2025,9,13); // +1 day
        Date date3 = new Date(2026,9,12); // +1 year
        
        Box box1 = new Box("B001", "W001", "SKU001", 10, date3, date1);
        Box box2 = new Box("B002", "W001", "SKU001", 10, date1, date1);
        Box box3 = new Box("B003", "W001", "SKU001", 10, date2, date1);
        
        // Unload boxes
        inventoryService.unloadBox(box1, warehouseId, aisle, bay1);
        inventoryService.unloadBox(box2, warehouseId, aisle, bay1);
        inventoryService.unloadBox(box3, warehouseId, aisle, bay1);
        
        // Get boxes in order
        List<Box> boxes = repository.getBoxesInBay(warehouseId, aisle, bay1);
        
        // Verify order: earliest expiry first
        assertEquals("B002", boxes.get(0).getBoxId(), "Earliest expiry should be first");
        assertEquals("B003", boxes.get(1).getBoxId(), "Next expiry should be second");
        assertEquals("B001", boxes.get(2).getBoxId(), "Latest expiry should be last");
    }

    @Test
    public void test_AC1a_null_expiry_last() {
        Date date1 = new Date(2025,9,12);
        Date date2 = new Date(2025,9,13); // +1 day
        
        Box box1 = new Box("B001", "W001", "SKU001", 10, null, date1);
        Box box2 = new Box("B002", "W001", "SKU001", 10, date2, date1);
        Box box3 = new Box("B003", "W001", "SKU001", 10, date1, date1);
        
        inventoryService.unloadBox(box1, warehouseId, aisle, bay1);
        inventoryService.unloadBox(box2, warehouseId, aisle, bay1);
        inventoryService.unloadBox(box3, warehouseId, aisle, bay1);
        
        List<Box> boxes = repository.getBoxesInBay(warehouseId, aisle, bay1);
        
        // Boxes with expiry dates should come first
        assertEquals("B003", boxes.get(0).getBoxId(), "Box with earliest expiry first");
        assertEquals("B002", boxes.get(1).getBoxId(), "Box with next expiry second");
        assertEquals("B001", boxes.get(2).getBoxId(), "Box with null expiry should be last");
    }

    @Test
    public void test_AC1b_boxes_ordered_by_receivedAt_when_expiry_equal() {
        Date date1 = new Date(2025,9,12);
        Date date2 = new Date(2025,9,13); // +1 day
        
        Box box1 = new Box("B001_OLD", "W001", "SKU001", 10, date2, date1);
        Box box2 = new Box("B002_NEW", "W001", "SKU001", 10, date2, date2);
        
        inventoryService.unloadBox(box1, warehouseId, aisle, bay1);
        inventoryService.unloadBox(box2, warehouseId, aisle, bay1);
        
        List<Box> boxes = repository.getBoxesInBay(warehouseId, aisle, bay1);
        
        // When expiry dates are equal, oldest receivedAt should come first
        assertEquals("B001_OLD", boxes.get(0).getBoxId(), "Older receivedAt should be first");
        assertEquals("B002_NEW", boxes.get(1).getBoxId(), "Newer receivedAt should be second");
    }

    @Test
    public void test_AC1c_boxId_tiebreaker() {
        Date date1 = new Date(2025,9,12);
        Date date2 = new Date(2025,9,13); // +1 day
        
        // All boxes have same expiry and receivedAt
        Box box1 = new Box("B003", "W001", "SKU001", 10, date2, date1);
        Box box2 = new Box("B001", "W001", "SKU001", 10, date2, date1);
        Box box3 = new Box("B002", "W001", "SKU001", 10, date2, date1);
        
        inventoryService.unloadBox(box1, warehouseId, aisle, bay1);
        inventoryService.unloadBox(box2, warehouseId, aisle, bay1);
        inventoryService.unloadBox(box3, warehouseId, aisle, bay1);
        
        List<Box> boxes = repository.getBoxesInBay(warehouseId, aisle, bay1);
        
        // Should be ordered by boxId ascending
        assertEquals("B001", boxes.get(0).getBoxId(), "First boxId alphabetically");
        assertEquals("B002", boxes.get(1).getBoxId(), "Second boxId alphabetically");
        assertEquals("B003", boxes.get(2).getBoxId(), "Third boxId alphabetically");
    }

    //AC2
    @Test
    public void test_AC2_dispatch_consumes_from_front() {
        Date date1 = new Date(2025,9,12);
        Date date2 = new Date(2025,9,13); // +1 day
        Date date3 = new Date(2026,9,12); // +1 year

        Box box1 = new Box("B001", "W001", "SKU001", 10, date1, date1);
        Box box2 = new Box("B002", "W001", "SKU001", 10, date2, date1);
        Box box3 = new Box("B003", "W001", "SKU001", 10, date3, date1);

        inventoryService.unloadBox(box1, warehouseId, aisle, bay1);
        inventoryService.unloadBox(box2, warehouseId, aisle, bay1);
        inventoryService.unloadBox(box3, warehouseId, aisle, bay1);

        // Dispatch 10 units
        int dispatched = inventoryService.dispatch("SKU001", 10, warehouseId);
        assertEquals(10, dispatched, "Should dispatch 10 units");

        // Verify box1 foi consumida
        List<Box> boxes = repository.getBoxesInBay(warehouseId, aisle, bay1);
        assertFalse(boxes.isEmpty());
        assertEquals("B002", boxes.get(0).getBoxId(), "Next box in front should be B002");
    }

    @Test
    public void test_AC2_partial_dispatch_across_multiple_bays() {
        Date date1 = new Date(2025,9,12);

        // Setup: 2 boxes, 5 unidades em cada
        Box box1Bay1 = new Box("B001", "W001", "SKU001", 5, date1, date1);
        Box box2Bay1 = new Box("B002", "W001", "SKU001", 5, date1, date1);

        inventoryService.unloadBox(box1Bay1, warehouseId, aisle, bay1);
        inventoryService.unloadBox(box2Bay1, warehouseId, aisle, bay1);

        // Dispatch 8 unidades (precisa consumir 5 da primeira e 3 da segunda)
        int dispatchedQty = inventoryService.dispatch("SKU001", 8, warehouseId);

        // Verifica que realmente despachou 8
        assertEquals(8, dispatchedQty, "Should dispatch exactly 8 units");

        // Verifica estado atual das boxes
        List<Box> boxes = repository.getBoxesInBay(warehouseId, aisle, bay1);

        assertFalse(boxes.isEmpty());
        //apagou a caixa vazia
        assertEquals("B002", boxes.get(0).getBoxId(), "Next box in front should be B002");
        //tirou 3 unidades da box 2
        assertEquals(2, boxes.get(0).getQtyAvailable(), "BO002 should have 2 available units");
    }

    @Test
    public void test_AC2_empty_bay_remains_in_system() {
        Date date1 = new Date(2025,9,12);
        
        Box box1 = new Box("B001", "W001", "SKU001", 10, date1, date1);
        inventoryService.unloadBox(box1, warehouseId, aisle, bay1);

        int dispatchedQty = inventoryService.dispatch("SKU001", 10, warehouseId);

        assertEquals(10, dispatchedQty, "Should dispatch exactly 10 units");
        
        // Bay should still exist but be empty
        Bay emptyBay = repository.getBay(warehouseId, aisle, bay1);
        assertNotNull(emptyBay, "Empty bay should still exist");
        assertEquals(0, emptyBay.getCurrentBoxCount(), "Bay should be empty");
    }

    @Test
    public void test_dispatch_limited_by_available_quantity() {
        Date date1 = new Date(2025,9,12);

        // Only 10 units available in one box
        Box box1 = new Box("B001", "W001", "SKU001", 10, date1, date1);
        inventoryService.unloadBox(box1, warehouseId, aisle, bay1);

        // Attempt to dispatch 20 units (more than available)
        int dispatchedQty = inventoryService.dispatch("SKU001", 20, warehouseId);

        // Verify that only 10 were dispatched
        assertEquals(10, dispatchedQty, "Should dispatch only the available quantity");

        // Bay should still exist but be empty
        Bay emptyBay = repository.getBay(warehouseId, aisle, bay1);
        assertNotNull(emptyBay, "Empty bay should still exist");
        assertEquals(0, emptyBay.getCurrentBoxCount(), "Bay should be empty");
    }

    // AC3: Test relocation
    @Test
    public void test_AC3_relocation_updates_location() {
        Date date1 = new Date(2025,9,12);
        
        Box box1 = new Box("B001", "W001", "SKU001", 10, date1, date1);
        inventoryService.unloadBox(box1, warehouseId, aisle, bay1);
        
        // Relocate to different bay
        inventoryService.relocate("B001", warehouseId, aisle, bay2);
        
        // Verify box is in new location
        List<Box> boxesBay1 = repository.getBoxesInBay(warehouseId, aisle, bay1);
        List<Box> boxesBay2 = repository.getBoxesInBay(warehouseId, aisle, bay2);
        
        assertEquals(0, boxesBay1.size(), "Bay1 should be empty");
        assertEquals(1, boxesBay2.size(), "Bay2 should have the box");
        assertEquals("B001", boxesBay2.get(0).getBoxId(), "Box should be in Bay2");
    }

    @Test
    public void test_AC3_relocation_inserts_in_correct_fefo_position() {
        Date date1 = new Date(2025,9,12);
        Date date2 = new Date(2025,9,13); // +1 day
        Date date3 = new Date(2026,9,12); // +1 year
        
        // Setup bay2 with boxes
        Box box1 = new Box("B001", "W001", "SKU001", 10, date2, date1);
        Box box2 = new Box("B002", "W001", "SKU001", 10, date3, date1);
        
        inventoryService.unloadBox(box1, warehouseId, aisle, bay2);
        inventoryService.unloadBox(box2, warehouseId, aisle, bay2);
        
        // Create box with earlier expiry in bay1
        Box box3 = new Box("B003", "W001", "SKU001", 10, date1, date1);
        inventoryService.unloadBox(box3, warehouseId, aisle, bay1);
        
        // Relocate box3 to bay2 - should be inserted in correct FEFO position
        inventoryService.relocate("B003", warehouseId, aisle, bay2);
        
        List<Box> boxesBay2 = repository.getBoxesInBay(warehouseId, aisle, bay2);
        
        // B003 (earliest expiry) should be first
        assertEquals("B003", boxesBay2.get(0).getBoxId(), "Relocated box should be in correct FEFO position");
        assertEquals("B001", boxesBay2.get(1).getBoxId(), "Second box");
        assertEquals("B002", boxesBay2.get(2).getBoxId(), "Third box");
    }


    // ==================== HELPER METHODS ====================

    private Path createWagonsCSV(String... lines) throws IOException {
        return createCSVFile("wagons.csv", lines);
    }

    private Path createCSVFile(String filename, String... lines) throws IOException {
        Path file = tempDir.resolve(filename);
        Files.write(file, List.of(lines));
        return file;
    }
}

