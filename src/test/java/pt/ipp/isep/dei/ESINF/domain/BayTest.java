package pt.ipp.isep.dei.ESINF.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.domain.Bay;
import pt.ipp.isep.dei.ESINF.domain.Box;
import pt.ipp.isep.dei.ESINF.services.impl.InventoryServiceImpl;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BayTest {

    private Bay bay;

    @BeforeEach
    void setUp() {
        bay = new Bay(1, 5); // bay number 1, capacity 5
    }

    @Test
    void testAddBoxAndOrdering() {
        // Box 1: expires tomorrow
        Box box1 = new Box("B1", "W1", "SKU1", 10, new Date(System.currentTimeMillis() + 86400000), new Date(System.currentTimeMillis()));
        // Box 2: expires today
        Box box2 = new Box("B2", "W1", "SKU2", 5, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()));
        // Box 3: non-perishable
        Box box3 = new Box("B3", "W1", "SKU3", 7, null, new Date(System.currentTimeMillis()));

        bay.addBox(box1);
        bay.addBox(box2);
        bay.addBox(box3);

        List<Box> ordered = bay.getOrderedBoxes();

        // box2 (earliest expiry) should come first
        assertEquals("B2", ordered.get(0).getBoxId());
        // box1 (later expiry) second
        assertEquals("B1", ordered.get(1).getBoxId());
        // box3 (non-perishable) last
        assertEquals("B3", ordered.get(2).getBoxId());
    }

    @Test
    void testGetBoxById() {
        Box box = new Box("B1", "W1", "SKU1", 10, null, new Date());
        bay.addBox(box);

        Box retrieved = bay.getBox("B1");
        assertNotNull(retrieved);
        assertEquals("B1", retrieved.getBoxId());

        // Non-existent
        assertNull(bay.getBox("B2"));
    }

    @Test
    void testRemoveBox() {
        Box box = new Box("B1", "W1", "SKU1", 10, null, new Date());
        bay.addBox(box);

        Box removed = bay.removeBox("B1");
        assertNotNull(removed);
        assertEquals("B1", removed.getBoxId());
        assertNull(bay.getBox("B1"));
    }

    @Test
    void testHasAvailableCapacity() {
        assertTrue(bay.hasAvailableCapacity());

        // Fill the bay
        for (int i = 1; i <= bay.getCapacityBoxes(); i++) {
            bay.addBox(new Box("B" + i, "W1", "SKU" + i, 5, null, new Date()));
        }

        assertFalse(bay.hasAvailableCapacity());
    }

    @Test
    void testGetCurrentBoxCount() {
        assertEquals(0, bay.getCurrentBoxCount());

        bay.addBox(new Box("B1", "W1", "SKU1", 10, null, new Date()));
        assertEquals(1, bay.getCurrentBoxCount());
    }
}
