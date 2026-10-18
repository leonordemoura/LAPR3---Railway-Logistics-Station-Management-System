package pt.ipp.isep.dei.ESINF.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.domain.*;
import pt.ipp.isep.dei.ESINF.services.PickingPlanService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PickingPlanServiceImpl
 * Tests the core business logic for packing heuristics and overflow policies
 */
class PickingPlanServiceImplTest {

    private PickingPlanServiceImpl service;
    private List<Allocation> testAllocations;
    private Map<String, Item> testItems;

    @BeforeEach
    void setUp() {
        service = new PickingPlanServiceImpl();
        setupTestData();
    }

    private void setupTestData() {
        // Setup test allocations
        testAllocations = new ArrayList<>();
        testAllocations.add(new Allocation("ORD1", 1, "SKU1", 5, "BOX1", "W1", 1, 1));
        testAllocations.add(new Allocation("ORD1", 2, "SKU2", 3, "BOX2", "W1", 1, 2));
        testAllocations.add(new Allocation("ORD2", 1, "SKU3", 8, "BOX3", "W1", 2, 1));
        testAllocations.add(new Allocation("ORD2", 2, "SKU4", 2, "BOX4", "W1", 2, 2));

        // Setup test items with weights
        testItems = new HashMap<>();
        testItems.put("SKU1", new Item("SKU1", "Item1", "Category1", "kg", 0.0, 1.0));
        testItems.put("SKU2", new Item("SKU2", "Item2", "Category2", "kg", 0.0, 2.0));
        testItems.put("SKU3", new Item("SKU3", "Item3", "Category3", "kg", 0.0, 0.5));
        testItems.put("SKU4", new Item("SKU4", "Item4", "Category4", "kg", 0.0, 3.0));
    }

    // -------------------------------------------------------------------------
    //  HEURISTICS × OVERFLOW POLICY (6 COMBINAÇÕES PRINCIPAIS)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Test 1: FIRST_FIT + DEFER")
    void testFirstFitWithDeferPolicy() {
        double trolleyCapacity = 10.0;
        PickingPlan plan = service.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT,
                trolleyCapacity,
                PickingPlanService.OverflowPolicy.DEFER,
                testAllocations,
                testItems
        );
        assertNotNull(plan);
        assertEquals("FIRST_FIT", plan.getStrategy());
        assertTrue(plan.getTotalTrolleys() > 0);
    }

    @Test
    @DisplayName("Test 2: FIRST_FIT + SPLIT")
    void testFirstFitWithSplitPolicy() {
        double trolleyCapacity = 5.0;
        PickingPlan plan = service.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT,
                trolleyCapacity,
                PickingPlanService.OverflowPolicy.SPLIT,
                testAllocations,
                testItems
        );
        assertNotNull(plan);
        assertEquals("FIRST_FIT", plan.getStrategy());
        assertTrue(service.getEvents().stream().anyMatch(e -> e.contains("partial allocation")));
    }

    @Test
    @DisplayName("Test 3: FIRST_FIT_DECREASING + DEFER")
    void testFirstFitDecreasingWithDeferPolicy() {
        double trolleyCapacity = 10.0;
        PickingPlan plan = service.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT_DECREASING,
                trolleyCapacity,
                PickingPlanService.OverflowPolicy.DEFER,
                testAllocations,
                testItems
        );
        assertNotNull(plan);
        assertEquals("FIRST_FIT_DECREASING", plan.getStrategy());
        assertTrue(plan.getTotalTrolleys() > 0);
    }

    @Test
    @DisplayName("Test 4: FIRST_FIT_DECREASING + SPLIT")
    void testFirstFitDecreasingWithSplitPolicy() {
        double trolleyCapacity = 5.0;
        PickingPlan plan = service.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT_DECREASING,
                trolleyCapacity,
                PickingPlanService.OverflowPolicy.SPLIT,
                testAllocations,
                testItems
        );
        assertNotNull(plan);
        assertEquals("FIRST_FIT_DECREASING", plan.getStrategy());
        assertTrue(service.getEvents().stream().anyMatch(e -> e.contains("partial allocation")));
    }

    @Test
    @DisplayName("Test 5: BEST_FIT_DECREASING + DEFER")
    void testBestFitDecreasingWithDeferPolicy() {
        double trolleyCapacity = 10.0;
        PickingPlan plan = service.createPickingPlan(
                PickingPlanService.PackingHeuristic.BEST_FIT_DECREASING,
                trolleyCapacity,
                PickingPlanService.OverflowPolicy.DEFER,
                testAllocations,
                testItems
        );
        assertNotNull(plan);
        assertEquals("BEST_FIT_DECREASING", plan.getStrategy());
        assertTrue(plan.getTotalTrolleys() > 0);
    }

    @Test
    @DisplayName("Test 6: BEST_FIT_DECREASING + SPLIT")
    void testBestFitDecreasingWithSplitPolicy() {
        double trolleyCapacity = 5.0;
        PickingPlan plan = service.createPickingPlan(
                PickingPlanService.PackingHeuristic.BEST_FIT_DECREASING,
                trolleyCapacity,
                PickingPlanService.OverflowPolicy.SPLIT,
                testAllocations,
                testItems
        );
        assertNotNull(plan);
        assertEquals("BEST_FIT_DECREASING", plan.getStrategy());
        assertTrue(service.getEvents().stream().anyMatch(e -> e.contains("partial allocation")));
    }

    // -------------------------------------------------------------------------
    //  CASOS COMPLEMENTARES / EDGE CASES
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Test 7: DEFER overflow policy triggers 'skipped due to capacity'")
    void testDeferOverflowPolicyEvents() {
        double trolleyCapacity = 2.0;
        service.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT,
                trolleyCapacity,
                PickingPlanService.OverflowPolicy.DEFER,
                testAllocations,
                testItems
        );
        assertTrue(service.getEvents().stream().anyMatch(e -> e.contains("skipped due to capacity")));
    }

    @Test
    @DisplayName("Test 8: Empty allocations list")
    void testEmptyAllocations() {
        List<Allocation> emptyAllocations = new ArrayList<>();
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            service.createPickingPlan(
                    PickingPlanService.PackingHeuristic.FIRST_FIT,
                    10.0,
                    PickingPlanService.OverflowPolicy.DEFER,
                    emptyAllocations,
                    testItems
            );
        });
        assertTrue(ex.getMessage().contains("No allocations available"));
    }

    @Test
    @DisplayName("Test 9: High capacity scenario - single trolley")
    void testHighCapacityScenario() {
        double trolleyCapacity = 100.0;
        PickingPlan plan = service.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT,
                trolleyCapacity,
                PickingPlanService.OverflowPolicy.DEFER,
                testAllocations,
                testItems
        );
        assertEquals(1, plan.getTotalTrolleys());
    }

    @Test
    @DisplayName("Test 10: Events logging functionality")
    void testEventsLogging() {
        double trolleyCapacity = 3.0;
        service.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT,
                trolleyCapacity,
                PickingPlanService.OverflowPolicy.SPLIT,
                testAllocations,
                testItems
        );
        assertNotNull(service.getEvents());
        assertFalse(service.getEvents().isEmpty());
    }

    @Test
    @DisplayName("Test 11: Trolley capacity validation")
    void testTrolleyCapacityValidation() {
        double trolleyCapacity = 10.0;
        PickingPlan plan = service.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT,
                trolleyCapacity,
                PickingPlanService.OverflowPolicy.DEFER,
                testAllocations,
                testItems
        );
        for (Trolley trolley : plan.getTrolleys()) {
            assertTrue(trolley.getUsedWeight() <= trolleyCapacity + 0.01);
        }
    }

    @Test
    @DisplayName("Test 12: Picking plan completeness")
    void testPickingPlanCompleteness() {
        double trolleyCapacity = 10.0;
        PickingPlan plan = service.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT,
                trolleyCapacity,
                PickingPlanService.OverflowPolicy.DEFER,
                testAllocations,
                testItems
        );

        assertNotNull(plan.getPlanId());
        assertNotNull(plan.getStrategy());
        for (Trolley trolley : plan.getTrolleys()) {
            assertTrue(trolley.getCapacity() > 0);
            for (PickingItem item : trolley.getItems()) {
                assertNotNull(item.getOrderId());
                assertTrue(item.getQuantity() > 0);
            }
        }
    }

    @Test
    @DisplayName("Test 13: Compare heuristics efficiency")
    void testHeuristicsComparison() {
        double trolleyCapacity = 8.0;

        PickingPlan ffPlan = service.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT, trolleyCapacity,
                PickingPlanService.OverflowPolicy.DEFER, testAllocations, testItems);

        PickingPlan ffdPlan = service.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT_DECREASING, trolleyCapacity,
                PickingPlanService.OverflowPolicy.DEFER, testAllocations, testItems);

        PickingPlan bfdPlan = service.createPickingPlan(
                PickingPlanService.PackingHeuristic.BEST_FIT_DECREASING, trolleyCapacity,
                PickingPlanService.OverflowPolicy.DEFER, testAllocations, testItems);

        assertTrue(ffPlan.getTotalTrolleys() > 0);
        assertTrue(ffdPlan.getTotalTrolleys() > 0);
        assertTrue(bfdPlan.getTotalTrolleys() > 0);
    }

    // -------------------------------------------------------------------------
    //  VALIDATION TESTS (REGRAS DE NEGÓCIO)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Test 14: Null heuristic")
    void testNullHeuristic() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.createPickingPlan(null, 10.0, PickingPlanService.OverflowPolicy.DEFER, testAllocations, testItems);
        });
        assertTrue(ex.getMessage().contains("Heuristic cannot be null"));
    }

    @Test
    @DisplayName("Test 15: Negative capacity")
    void testNegativeCapacity() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.createPickingPlan(PickingPlanService.PackingHeuristic.FIRST_FIT, -5.0,
                    PickingPlanService.OverflowPolicy.DEFER, testAllocations, testItems);
        });
        assertTrue(ex.getMessage().contains("Trolley capacity must be positive"));
    }

    @Test
    @DisplayName("Test 16: Zero capacity")
    void testZeroCapacity() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.createPickingPlan(PickingPlanService.PackingHeuristic.FIRST_FIT, 0.0,
                    PickingPlanService.OverflowPolicy.DEFER, testAllocations, testItems);
        });
        assertTrue(ex.getMessage().contains("Trolley capacity must be positive"));
    }

    @Test
    @DisplayName("Test 17: Null overflow policy")
    void testNullOverflowPolicy() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.createPickingPlan(PickingPlanService.PackingHeuristic.FIRST_FIT, 10.0, null, testAllocations, testItems);
        });
        assertTrue(ex.getMessage().contains("Overflow policy cannot be null"));
    }

    @Test
    @DisplayName("Test 18: Null allocations list")
    void testNullAllocationsList() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.createPickingPlan(PickingPlanService.PackingHeuristic.FIRST_FIT, 10.0,
                    PickingPlanService.OverflowPolicy.DEFER, null, testItems);
        });
        assertTrue(ex.getMessage().contains("Allocations cannot be null"));
    }

    @Test
    @DisplayName("Test 19: Empty allocations list throws exception")
    void testEmptyAllocationsListThrowsException() {
        List<Allocation> emptyAllocations = new ArrayList<>();
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            service.createPickingPlan(PickingPlanService.PackingHeuristic.FIRST_FIT, 10.0,
                    PickingPlanService.OverflowPolicy.DEFER, emptyAllocations, testItems);
        });
        assertTrue(ex.getMessage().contains("No allocations available"));
    }

    @Test
    @DisplayName("Test 20: Null items map")
    void testNullItemsMap() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.createPickingPlan(PickingPlanService.PackingHeuristic.FIRST_FIT, 10.0,
                    PickingPlanService.OverflowPolicy.DEFER, testAllocations, null);
        });
        assertTrue(ex.getMessage().contains("Items map cannot be null"));
    }
}
