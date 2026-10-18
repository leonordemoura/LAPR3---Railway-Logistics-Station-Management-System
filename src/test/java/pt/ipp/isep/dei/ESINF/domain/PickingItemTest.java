package pt.ipp.isep.dei.ESINF.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PickingItem domain class
 * Tests the business logic and calculations of picking items
 */
class PickingItemTest {

    @Test
    @DisplayName("Test 1: Calculate weight per unit correctly")
    void testGetWeightPerUnit() {
        // Arrange
        PickingItem item = new PickingItem("ORD1", 1, 1, 1, "BOX1", "SKU1", 5, 10.0);

        // Act
        double weightPerUnit = item.getWeightPerUnit();

        // Assert
        assertEquals(2.0, weightPerUnit, 0.01); // 10.0 / 5 = 2.0
    }

    @Test
    @DisplayName("Test 2: Handle zero quantity")
    void testGetWeightPerUnitWithZeroQuantity() {
        // Arrange
        PickingItem item = new PickingItem("ORD1", 1, 1, 1, "BOX1", "SKU1", 0, 10.0);

        // Act
        double weightPerUnit = item.getWeightPerUnit();

        // Assert
        assertEquals(0.0, weightPerUnit, 0.01); // Should return 0 when quantity is 0
    }

    @Test
    @DisplayName("Test 3: Handle zero weight")
    void testGetWeightPerUnitWithZeroWeight() {
        // Arrange
        PickingItem item = new PickingItem("ORD1", 1, 1, 1, "BOX1", "SKU1", 5, 0.0);

        // Act
        double weightPerUnit = item.getWeightPerUnit();

        // Assert
        assertEquals(0.0, weightPerUnit, 0.01); // 0.0 / 5 = 0.0
    }

    @Test
    @DisplayName("Test 4: Handle negative quantity")
    void testGetWeightPerUnitWithNegativeQuantity() {
        // Arrange
        PickingItem item = new PickingItem("ORD1", 1, 1, 1, "BOX1", "SKU1", -5, 10.0);

        // Act
        double weightPerUnit = item.getWeightPerUnit();

        // Assert
        assertEquals(0.0, weightPerUnit, 0.01); // Should return 0 when quantity is negative
    }

    @Test
    @DisplayName("Test 5: Handle negative weight")
    void testGetWeightPerUnitWithNegativeWeight() {
        // Arrange
        PickingItem item = new PickingItem("ORD1", 1, 1, 1, "BOX1", "SKU1", 5, -10.0);

        // Act
        double weightPerUnit = item.getWeightPerUnit();

        // Assert
        assertEquals(-2.0, weightPerUnit, 0.01); // -10.0 / 5 = -2.0
    }

    @Test
    @DisplayName("Test 6: Handle decimal weight calculation")
    void testPickingItemWithDecimalWeight() {
        // Arrange
        PickingItem item = new PickingItem("ORD1", 1, 1, 1, "BOX1", "SKU1", 3, 7.5);

        // Act
        double weightPerUnit = item.getWeightPerUnit();

        // Assert
        assertEquals(2.5, weightPerUnit, 0.01); // 7.5 / 3 = 2.5
    }

    @Test
    @DisplayName("Test 7: Handle very small weight")
    void testPickingItemWithVerySmallWeight() {
        // Arrange
        PickingItem item = new PickingItem("ORD1", 1, 1, 1, "BOX1", "SKU1", 1000, 0.001);

        // Act
        double weightPerUnit = item.getWeightPerUnit();

        // Assert
        assertEquals(0.000001, weightPerUnit, 0.0000001); // 0.001 / 1000 = 0.000001
    }

    @Test
    @DisplayName("Test 8: Verify toString format")
    void testToString() {
        // Arrange
        PickingItem item = new PickingItem("ORD1", 1, 5, 3, "BOX123", "SKU001", 10, 15.5);

        // Act
        String result = item.toString();

        // Assert
        assertNotNull(result);
        assertEquals("ORD1-1 SKU001 x10 (15,50kg) @ 5-3 [BOX123]", result);
    }

    @Test
    @DisplayName("Test 9: Handle zero values in toString")
    void testToStringWithZeroValues() {
        // Arrange
        PickingItem item = new PickingItem("ORD1", 0, 0, 0, "BOX1", "SKU1", 0, 0.0);

        // Act
        String result = item.toString();

        // Assert
        assertNotNull(result);
        assertEquals("ORD1-0 SKU1 x0 (0,00kg) @ 0-0 [BOX1]", result);
    }

    @Test
    @DisplayName("Test 10: Handle negative values in toString")
    void testToStringWithNegativeValues() {
        // Arrange
        PickingItem item = new PickingItem("ORD1", -1, -5, -3, "BOX1", "SKU1", -10, -15.5);

        // Act
        String result = item.toString();

        // Assert
        assertNotNull(result);
        assertEquals("ORD1--1 SKU1 x-10 (-15,50kg) @ -5--3 [BOX1]", result);
    }
}
