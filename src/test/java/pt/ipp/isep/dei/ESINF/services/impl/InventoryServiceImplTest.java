package pt.ipp.isep.dei.ESINF.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.domain.*;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.InventoryService;
import pt.ipp.isep.dei.ESINF.services.impl.InventoryServiceImpl;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryServiceImplTest {

    private WarehouseRepository repository;
    private InventoryService inventoryService;
    private String warehouseId = "WH1";
    private int aisleNum = 1;
    private int bayNum = 1;

    @BeforeEach
    public void setUp() {
        repository = new WarehouseRepository();
        inventoryService = new InventoryServiceImpl(repository);

        Warehouse warehouse = new Warehouse(warehouseId, "Test Warehouse", null);
        repository.addWarehouse(warehouse);

        repository.addAisle(warehouseId, new Aisle(aisleNum, warehouseId));
        repository.addBay(warehouseId, aisleNum, bayNum, 10);
    }

    @Test
    public void testUnloadBox_addsBoxToBay() {
        Date date1 = new Date(2025, 9, 12);
        Date date2 = new Date(2025, 9, 13);
        Box box = new Box("B001", "WAGON1", "SKU1", 5, date1, date2);
        boolean added = inventoryService.unloadBox(box, warehouseId, aisleNum, bayNum);
        assertTrue(added, "Box should be added successfully");

        List<Box> boxes = inventoryService.getBoxesInBay(warehouseId, aisleNum, bayNum);
        assertEquals(1, boxes.size(), "Bay should contain one box");
        assertEquals("B001", boxes.get(0).getBoxId());
    }

    @Test
    public void testUnloadBox_fefoFifoOrdering() {
        Date date1 = new Date(2025, 9, 12);
        Date date3 = new Date(2025, 9, 11);
        Date date2 = new Date(2025, 9, 13);
        Box box1 = new Box("B001", "W1", "SKU1", 5, date1, date2);
        Box box2 = new Box("B002", "W1", "SKU1", 5, date3, date2);
        Box box3 = new Box("B003", "W1", "SKU1", 5, null, date2);

        inventoryService.unloadBox(box1, warehouseId, aisleNum, bayNum);
        inventoryService.unloadBox(box2, warehouseId, aisleNum, bayNum);
        inventoryService.unloadBox(box3, warehouseId, aisleNum, bayNum);

        List<Box> boxes = inventoryService.getBoxesInBay(warehouseId, aisleNum, bayNum);

        // FEFO/FIFO: earliest expiry first, then receivedAt, then boxId
        assertEquals("B002", boxes.get(0).getBoxId(), "Earliest expiry first");
        assertEquals("B001", boxes.get(1).getBoxId(), "Next expiry");
        assertEquals("B003", boxes.get(2).getBoxId(), "Null expiry last");
    }

    @Test
    public void testDispatch_singleBox() {
        Date date1 = new Date(2025, 9, 12);
        Date date2 = new Date(2025, 9, 13);
        Box box = new Box("B001", "W1", "SKU1", 10, date1, date2);
        inventoryService.unloadBox(box, warehouseId, aisleNum, bayNum);

        int dispatched = inventoryService.dispatch("SKU1", 5, warehouseId);
        assertEquals(5, dispatched, "Should dispatch requested quantity");

        List<Box> boxes = inventoryService.getBoxesInBay(warehouseId, aisleNum, bayNum);
        assertEquals(1, boxes.size());
        assertEquals(5, boxes.get(0).getQtyAvailable(), "Remaining quantity should be 5");
    }

    @Test
    public void testDispatch_multipleBoxesAcrossBays() {
        Date date1 = new Date(2025, 9, 12);
        Date date2 = new Date(2025, 9, 13);
        Date date3 = new Date(2025, 9, 11);
        repository.addBay(warehouseId, aisleNum, 2, 10);
        Box box1 = new Box("B001", "W1", "SKU1", 5, date1, date2);
        Box box2 = new Box("B002", "W1", "SKU1", 7, date3, date2);

        inventoryService.unloadBox(box1, warehouseId, aisleNum, bayNum);
        inventoryService.unloadBox(box2, warehouseId, aisleNum, 2);

        int dispatched = inventoryService.dispatch("SKU1", 10, warehouseId);
        assertEquals(10, dispatched, "Should dispatch total of 10 units");

        List<Box> boxesBay1 = inventoryService.getBoxesInBay(warehouseId, aisleNum, bayNum);
        List<Box> boxesBay2 = inventoryService.getBoxesInBay(warehouseId, aisleNum, 2);


        assertTrue(boxesBay1.isEmpty(), "bay should be empty");
        assertEquals(2, boxesBay2.get(0).getQtyAvailable(), "Bay2 box should have 2 remaining");
    }

    @Test
    public void testRelocate_boxMovesToNewBay() {
        Date date1 = new Date(2025, 9, 12);
        Date date2 = new Date(2025, 9, 13);
        Date date3 = new Date(2025, 9, 11);
        repository.addBay(warehouseId, aisleNum, 2, 10);
        Box box = new Box("B001", "W1", "SKU1", 5, date1, date2);
        inventoryService.unloadBox(box, warehouseId, aisleNum, bayNum);

        inventoryService.relocate("B001", warehouseId, aisleNum, 2);

        List<Box> oldBayBoxes = inventoryService.getBoxesInBay(warehouseId, aisleNum, bayNum);
        List<Box> newBayBoxes = inventoryService.getBoxesInBay(warehouseId, aisleNum, 2);

        assertEquals(0, oldBayBoxes.size(), "Old bay should be empty");
        assertEquals(1, newBayBoxes.size(), "New bay should contain the box");
        assertEquals("B001", newBayBoxes.get(0).getBoxId());
    }

    @Test
    public void testDispatch_emptyBayReturnsZero() {
        int dispatched = inventoryService.dispatch("SKU1", 5, warehouseId);
        assertEquals(0, dispatched, "Dispatch from empty bay should return 0");
    }
}
