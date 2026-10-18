package pt.ipp.isep.dei.ESINF.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.domain.constants.Goods;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class BoxTest {

    private Box perishableBox;
    private Box nonPerishableBox;
    private Date expiry;
    private Date received;

    @BeforeEach
    void setUp() {
        expiry = new Date(9,10,2025);
        received = new Date(8,10,2025);

        perishableBox = new Box("B001", "W1", "SKU001", 10, expiry, received);
        nonPerishableBox = new Box("B002", "W2", "SKU002", 5, null, received);
    }

    // --- BASIC GETTERS/SETTERS TESTS ---

    @Test
    void testGettersAndSetters() {
        Box box = new Box("B003", "W3", "SKU003", 20, expiry, received);
        box.setBoxId("NEWID");
        box.setWagonId("NEW_WAGON");
        box.setSku("NEW_SKU");
        box.setQty(15);
        box.setQtyAvailable(10);
        box.setExpiryDate(null);
        box.setReceivedAt(new Date(0));

        assertEquals("NEWID", box.getBoxId());
        assertEquals("NEW_WAGON", box.getWagonId());
        assertEquals("NEW_SKU", box.getSku());
        assertEquals(15, box.getQty());
        assertEquals(10, box.getQtyAvailable());
        assertNull(box.getExpiryDate());
        assertEquals(new Date(0), box.getReceivedAt());
    }

    // --- TYPE AND STATE CHECKS ---

    @Test
    void testGetType_Perishable() {
        assertEquals(Goods.PERISHABLE, perishableBox.getType());
        assertTrue(perishableBox.isPerishable());
    }

    @Test
    void testGetType_NonPerishable() {
        assertEquals(Goods.NON_PERISHABLE, nonPerishableBox.getType());
        assertFalse(nonPerishableBox.isPerishable());
    }

    @Test
    void testIsUnloadedAlwaysTrue() {
        assertTrue(perishableBox.isUnloaded());
        assertTrue(nonPerishableBox.isUnloaded());
    }

    // --- QUANTITY REDUCTION ---

    @Test
    void testReduceQuantity_Valid() {
        perishableBox.reduceQuantity(4);
        assertEquals(6, perishableBox.getQtyAvailable());
    }

    @Test
    void testReduceQuantity_ThrowsWhenTooMuch() {
        assertThrows(IllegalArgumentException.class, () -> perishableBox.reduceQuantity(20));
    }

    @Test
    void testReduceQuantity_AlsoUpdatesQtyForAllocation() {
        perishableBox.reduceQuantity(3);
        assertEquals(perishableBox.getQtyAvailable(), perishableBox.getQtyForAllocation());
    }

    // --- ALLOCATION LOGIC ---

    @Test
    void testAllocate_LessThanAvailable() {
        int allocated = perishableBox.allocate(4);
        assertEquals(4, allocated);
        assertEquals(6, perishableBox.getQtyForAllocation());
    }

    @Test
    void testAllocate_MoreThanAvailable() {
        int allocated = perishableBox.allocate(20);
        assertEquals(10, allocated); // only 10 available
        assertEquals(0, perishableBox.getQtyForAllocation());
    }

    @Test
    void testReallocate_ReturnsQuantityToAllocation() {
        perishableBox.allocate(5);
        perishableBox.reallocate(3);
        assertEquals(8, perishableBox.getQtyForAllocation());
    }

    @Test
    void testHasAvailableQty_TrueAndFalse() {
        assertTrue(perishableBox.hasAvailableQty());
        perishableBox.reduceQuantity(10);
        assertFalse(perishableBox.hasAvailableQty());
    }

    @Test
    void testHasAllocatedQty() {
        assertFalse(perishableBox.hasAllocatedQty());
        perishableBox.allocate(5);
        assertTrue(perishableBox.hasAllocatedQty());
    }

    @Test
    void testIsEmpty() {
        assertFalse(perishableBox.isEmpty());
        perishableBox.reduceQuantity(10);
        assertTrue(perishableBox.isEmpty());
    }
}
