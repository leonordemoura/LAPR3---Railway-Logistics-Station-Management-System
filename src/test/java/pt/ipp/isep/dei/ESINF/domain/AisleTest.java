package pt.ipp.isep.dei.ESINF.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AisleTest {

    private Aisle aisle;

    @BeforeEach
    void setUp() {
        aisle = new Aisle(1, "W1");
    }

    @Test
    void testConstructorInitializesFields() {
        assertEquals(1, aisle.getAisleNumber());
        assertEquals("W1", aisle.getWarehouseId());
        assertTrue(aisle.getBays().isEmpty(), "Aisle should start with no bays");
    }

    @Test
    void testAddBay_AddsBaySuccessfully() {
        Bay bay = new Bay(1, 100);

        aisle.addBay(bay);

        assertEquals(1, aisle.getBays().size());
        assertTrue(aisle.getBays().containsKey(1));
        assertEquals(bay, aisle.getBays().get(1));
    }

    @Test
    void testAddMultipleBays_AllStoredCorrectly() {
        Bay bay1 = new Bay(1, 50);
        Bay bay2 = new Bay(2, 75);

        aisle.addBay(bay1);
        aisle.addBay(bay2);

        Map<Integer, Bay> bays = aisle.getBays();
        assertEquals(2, bays.size());
        assertEquals(bay1, bays.get(1));
        assertEquals(bay2, bays.get(2));
    }

    @Test
    void testGetBay_ReturnsCorrectBay() {
        Bay bay = new Bay(3, 40);
        aisle.addBay(bay);

        Bay result = aisle.getBay(3);

        assertNotNull(result);
        assertEquals(bay, result);
    }

    @Test
    void testGetBay_ReturnsNullForNonexistentBay() {
        assertNull(aisle.getBay(999), "Should return null if bay doesn't exist");
    }

    @Test
    void testAddBay_OverwritesIfSameNumber() {
        Bay bay1 = new Bay(1, 50);
        Bay bay2 = new Bay(1, 75); // mesma key

        aisle.addBay(bay1);
        aisle.addBay(bay2); // substitui o anterior

        assertEquals(1, aisle.getBays().size());
        assertEquals(bay2, aisle.getBay(1), "New bay should replace old bay with same number");
    }
}
