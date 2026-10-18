package pt.ipp.isep.dei.ESINF.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void testConstructorAndGetters() {
        Item item = new Item("SKU001", "Apple", "Fruit", "kg", 0.002, 1.2);

        assertEquals("SKU001", item.getSku());
        assertEquals("Apple", item.getName());
        assertEquals("Fruit", item.getCategory());
        assertEquals("kg", item.getUnit());
        assertEquals(0.002, item.getVolume(), 0.0001);
        assertEquals(1.2, item.getUnitWeight(), 0.0001);
    }

    @Test
    void testDifferentValuesStoredIndependently() {
        Item item1 = new Item("SKU001", "Apple", "Fruit", "kg", 0.002, 1.2);
        Item item2 = new Item("SKU002", "Laptop", "Electronics", "unit", 0.01, 2.5);

        assertNotEquals(item1.getSku(), item2.getSku());
        assertNotEquals(item1.getCategory(), item2.getCategory());
        assertNotEquals(item1.getUnitWeight(), item2.getUnitWeight());
    }

    @Test
    void testZeroAndNegativeValuesAllowedButCheckPrecision() {
        Item zeroItem = new Item("ZERO", "Void", "Test", "unit", 0.0, 0.0);
        Item negItem = new Item("NEG", "Invalid", "Test", "unit", -0.5, -1.0);

        assertEquals(0.0, zeroItem.getVolume());
        assertEquals(0.0, zeroItem.getUnitWeight());
        assertEquals(-0.5, negItem.getVolume());
        assertEquals(-1.0, negItem.getUnitWeight());
    }

    @Test
    void testToStringNotImplementedButCheckFieldAccess() {
        Item item = new Item("SKU999", "TestItem", "TestCat", "pcs", 1.5, 3.3);

        assertAll("All getters should work correctly",
                () -> assertEquals("SKU999", item.getSku()),
                () -> assertEquals("TestItem", item.getName()),
                () -> assertEquals("TestCat", item.getCategory()),
                () -> assertEquals("pcs", item.getUnit()),
                () -> assertEquals(1.5, item.getVolume()),
                () -> assertEquals(3.3, item.getUnitWeight())
        );
    }
}
