package pt.ipp.isep.dei.ESINF.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PickingPlan domain class
 * Tests the business logic and calculations of picking plans
 */
class PickingPlanTest {

    private List<Trolley> testTrolleys;
    private PickingPlan pickingPlan;

    @BeforeEach
    void setUp() {
        setupTestTrolleys();
    }

    private void setupTestTrolleys() {
        testTrolleys = new ArrayList<>();
        
        // Create trolleys with different utilization levels
        List<PickingItem> items1 = new ArrayList<>();
        items1.add(new PickingItem("ORD1", 1, 1, 1, "BOX1", "SKU1", 5, 5.0));
        testTrolleys.add(new Trolley("T1", 10.0, 5.0, items1)); // 50% utilization
        
        List<PickingItem> items2 = new ArrayList<>();
        items2.add(new PickingItem("ORD2", 1, 2, 1, "BOX2", "SKU2", 8, 8.0));
        testTrolleys.add(new Trolley("T2", 10.0, 8.0, items2)); // 80% utilization
        
        List<PickingItem> items3 = new ArrayList<>();
        items3.add(new PickingItem("ORD3", 1, 3, 1, "BOX3", "SKU3", 2, 2.0));
        testTrolleys.add(new Trolley("T3", 10.0, 2.0, items3)); // 20% utilization
    }

    @Test
    @DisplayName("Test 1: Calculate average utilization correctly")
    void testGetAverageUtilization() {
        // Arrange
        pickingPlan = new PickingPlan("PLAN-001", "FIRST_FIT", testTrolleys);

        // Act
        double averageUtilization = pickingPlan.getAverageUtilization();

        // Assert
        // Expected: (50% + 80% + 20%) / 3 = 50%
        assertEquals(50.0, averageUtilization, 0.01);
    }

    @Test
    @DisplayName("Test 2: Handle empty trolley list")
    void testGetAverageUtilizationWithEmptyList() {
        // Arrange
        List<Trolley> emptyTrolleys = new ArrayList<>();
        pickingPlan = new PickingPlan("PLAN-001", "FIRST_FIT", emptyTrolleys);

        // Act
        double averageUtilization = pickingPlan.getAverageUtilization();

        // Assert
        assertEquals(0.0, averageUtilization, 0.01);
    }

    @Test
    @DisplayName("Test 3: Calculate total trolleys correctly")
    void testGetTotalTrolleys() {
        // Arrange
        pickingPlan = new PickingPlan("PLAN-001", "FIRST_FIT", testTrolleys);

        // Act
        int totalTrolleys = pickingPlan.getTotalTrolleys();

        // Assert
        assertEquals(3, totalTrolleys);
    }

    @Test
    @DisplayName("Test 4: Handle single trolley")
    void testGetAverageUtilizationWithSingleTrolley() {
        // Arrange
        List<Trolley> singleTrolley = new ArrayList<>();
        singleTrolley.add(testTrolleys.get(1)); // 80% utilization
        pickingPlan = new PickingPlan("PLAN-001", "FIRST_FIT", singleTrolley);

        // Act
        double averageUtilization = pickingPlan.getAverageUtilization();

        // Assert
        assertEquals(80.0, averageUtilization, 0.01);
    }

    @Test
    @DisplayName("Test 5: Handle high utilization scenario")
    void testPickingPlanWithHighUtilization() {
        // Arrange
        List<Trolley> highUtilTrolleys = new ArrayList<>();
        List<PickingItem> items = new ArrayList<>();
        items.add(new PickingItem("ORD1", 1, 1, 1, "BOX1", "SKU1", 9, 9.0));
        highUtilTrolleys.add(new Trolley("T1", 10.0, 9.0, items)); // 90% utilization
        
        items = new ArrayList<>();
        items.add(new PickingItem("ORD2", 1, 2, 1, "BOX2", "SKU2", 10, 10.0));
        highUtilTrolleys.add(new Trolley("T2", 10.0, 10.0, items)); // 100% utilization
        
        pickingPlan = new PickingPlan("PLAN-001", "FIRST_FIT", highUtilTrolleys);

        // Act
        double averageUtilization = pickingPlan.getAverageUtilization();

        // Assert
        assertEquals(95.0, averageUtilization, 0.01); // (90% + 100%) / 2
    }

    @Test
    @DisplayName("Test 6: Handle low utilization scenario")
    void testPickingPlanWithLowUtilization() {
        // Arrange
        List<Trolley> lowUtilTrolleys = new ArrayList<>();
        List<PickingItem> items = new ArrayList<>();
        items.add(new PickingItem("ORD1", 1, 1, 1, "BOX1", "SKU1", 1, 1.0));
        lowUtilTrolleys.add(new Trolley("T1", 10.0, 1.0, items)); // 10% utilization
        
        items = new ArrayList<>();
        items.add(new PickingItem("ORD2", 1, 2, 1, "BOX2", "SKU2", 2, 2.0));
        lowUtilTrolleys.add(new Trolley("T2", 10.0, 2.0, items)); // 20% utilization
        
        pickingPlan = new PickingPlan("PLAN-001", "FIRST_FIT", lowUtilTrolleys);

        // Act
        double averageUtilization = pickingPlan.getAverageUtilization();

        // Assert
        assertEquals(15.0, averageUtilization, 0.01); // (10% + 20%) / 2
    }

    @Test
    @DisplayName("Test 7: Verify toString format")
    void testToString() {
        // Arrange
        pickingPlan = new PickingPlan("PLAN-001", "FIRST_FIT", testTrolleys);

        // Act
        String result = pickingPlan.toString();

        // Assert
        assertNotNull(result);
        assertEquals("PickingPlan{id=PLAN-001, strategy=FIRST_FIT, trolleys=3, avgUtilization=50,0%}", result);
    }
}
