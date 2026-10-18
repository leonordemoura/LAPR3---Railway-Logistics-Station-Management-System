package pt.ipp.isep.dei.ESINF.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Trolley domain class
 * Tests the business logic and calculations of trolleys
 */
class TrolleyTest {

    private Trolley trolley;
    private List<PickingItem> testItems;

    @BeforeEach
    void setUp() {
        setupTestItems();
    }

    private void setupTestItems() {
        testItems = new ArrayList<>();
        testItems.add(new PickingItem("ORD1", 1, 1, 1, "BOX1", "SKU1", 5, 5.0));
        testItems.add(new PickingItem("ORD1", 2, 1, 2, "BOX2", "SKU2", 3, 3.0));
        testItems.add(new PickingItem("ORD2", 1, 2, 1, "BOX3", "SKU3", 2, 2.0));
    }

    @Test
    @DisplayName("Test 1: Calculate utilization correctly")
    void testGetUtilization() {
        // Arrange
        trolley = new Trolley("T1", 10.0, 7.5, testItems);

        // Act
        double utilization = trolley.getUtilization();

        // Assert
        assertEquals(75.0, utilization, 0.01); // (7.5 / 10.0) * 100
    }

    @Test
    @DisplayName("Test 2: Handle zero capacity")
    void testGetUtilizationWithZeroCapacity() {
        // Arrange
        trolley = new Trolley("T1", 0.0, 5.0, testItems);

        // Act
        double utilization = trolley.getUtilization();

        // Assert
        assertEquals(0.0, utilization, 0.01); // Should return 0 when capacity is 0
    }

    @Test
    @DisplayName("Test 3: Handle full capacity")
    void testGetUtilizationWithFullCapacity() {
        // Arrange
        trolley = new Trolley("T1", 10.0, 10.0, testItems);

        // Act
        double utilization = trolley.getUtilization();

        // Assert
        assertEquals(100.0, utilization, 0.01); // (10.0 / 10.0) * 100
    }

    @Test
    @DisplayName("Test 4: Handle over capacity")
    void testGetUtilizationWithOverCapacity() {
        // Arrange
        trolley = new Trolley("T1", 10.0, 12.0, testItems);

        // Act
        double utilization = trolley.getUtilization();

        // Assert
        assertEquals(120.0, utilization, 0.01); // (12.0 / 10.0) * 100
    }

    @Test
    @DisplayName("Test 5: Update used weight and recalculate utilization")
    void testSetUsedWeight() {
        // Arrange
        trolley = new Trolley("T1", 10.0, 5.0, testItems);

        // Act
        trolley.setUsedWeight(8.0);

        // Assert
        assertEquals(8.0, trolley.getUsedWeight(), 0.01);
        assertEquals(80.0, trolley.getUtilization(), 0.01);
    }

    @Test
    @DisplayName("Test 6: Handle negative weight")
    void testSetUsedWeightToNegative() {
        // Arrange
        trolley = new Trolley("T1", 10.0, 5.0, testItems);

        // Act
        trolley.setUsedWeight(-2.0);

        // Assert
        assertEquals(-2.0, trolley.getUsedWeight(), 0.01);
        assertEquals(-20.0, trolley.getUtilization(), 0.01);
    }

    @Test
    @DisplayName("Test 7: Verify toString format")
    void testToString() {
        // Arrange
        trolley = new Trolley("T1", 10.0, 7.5, testItems);

        // Act
        String result = trolley.toString();

        // Assert
        assertNotNull(result);
        assertEquals("Trolley{id=T1, used=7,50/10,00 (75,0%), items=3}", result);
    }

    @Test
    @DisplayName("Test 8: Handle empty items list")
    void testToStringWithEmptyItems() {
        // Arrange
        List<PickingItem> emptyItems = new ArrayList<>();
        trolley = new Trolley("T1", 10.0, 0.0, emptyItems);

        // Act
        String result = trolley.toString();

        // Assert
        assertNotNull(result);
        assertEquals("Trolley{id=T1, used=0,00/10,00 (0,0%), items=0}", result);
    }
}
