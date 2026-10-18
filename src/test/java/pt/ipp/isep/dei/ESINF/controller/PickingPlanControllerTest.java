package pt.ipp.isep.dei.ESINF.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.domain.*;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.PickingPlanService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Final optimized test class for PickingPlanController
 * Covers all heuristics, overflow policies, input validations, and integration with USEI02.
 */
class PickingPlanControllerTest {

    private PickingPlanController controller;
    private WarehouseRepository warehouseRepository;

    @BeforeEach
    void setUp() {
        controller = new PickingPlanController();
        warehouseRepository = Repositories.getInstance().getWarehouseRepository();
        setupTestData();
    }

    private void setupTestData() {
        warehouseRepository.getItems().clear();
        warehouseRepository.addItem(new Item("SKU1", "Item1", "Category1", "kg", 0.0, 1.0));
        warehouseRepository.addItem(new Item("SKU2", "Item2", "Category2", "kg", 0.0, 2.0));
        warehouseRepository.addItem(new Item("SKU3", "Item3", "Category3", "kg", 0.0, 0.5));
        warehouseRepository.addItem(new Item("SKU4", "Item4", "Category4", "kg", 0.0, 3.0));
    }

    // ------------------------------------------------------------
    // BASIC VALIDATION TESTS
    // ------------------------------------------------------------

    @Test
    @DisplayName("Test 1: Handle empty allocations list")
    void testCreatePickingPlanWithEmptyAllocations() {
        List<Allocation> emptyAllocations = new ArrayList<>();
        PickingPlanController.setAllocations(emptyAllocations);

        assertThrows(IllegalStateException.class, () -> {
            controller.createPickingPlan(
                    PickingPlanService.PackingHeuristic.FIRST_FIT,
                    10.0,
                    PickingPlanService.OverflowPolicy.DEFER,
                    emptyAllocations,
                    warehouseRepository.getItems()
            );
        });
    }

    @Test
    @DisplayName("Test 2: Validate allocations availability")
    void testHasAllocations() {
        List<Allocation> allocations = createTestAllocations();
        PickingPlanController.setAllocations(allocations);
        assertTrue(controller.hasAllocations());
    }

    @Test
    @DisplayName("Test 3: Handle no allocations")
    void testHasNoAllocations() {
        PickingPlanController.setAllocations(new ArrayList<>());
        assertFalse(controller.hasAllocations());
    }

    // ------------------------------------------------------------
    // INPUT VALIDATION TESTS
    // ------------------------------------------------------------

    @Test
    @DisplayName("Test 4: Null heuristic should throw exception")
    void testNullHeuristicThrowsException() {
        List<Allocation> allocations = createTestAllocations();
        assertThrows(IllegalArgumentException.class, () -> {
            controller.createPickingPlan(
                    null,
                    10.0,
                    PickingPlanService.OverflowPolicy.DEFER,
                    allocations,
                    warehouseRepository.getItems()
            );
        });
    }

    @Test
    @DisplayName("Test 5: Null overflow policy should throw exception")
    void testNullOverflowPolicyThrowsException() {
        List<Allocation> allocations = createTestAllocations();
        assertThrows(IllegalArgumentException.class, () -> {
            controller.createPickingPlan(
                    PickingPlanService.PackingHeuristic.FIRST_FIT,
                    10.0,
                    null,
                    allocations,
                    warehouseRepository.getItems()
            );
        });
    }

    @Test
    @DisplayName("Test 6: Null allocations should throw exception")
    void testNullAllocationsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            controller.createPickingPlan(
                    PickingPlanService.PackingHeuristic.FIRST_FIT,
                    10.0,
                    PickingPlanService.OverflowPolicy.DEFER,
                    null,
                    warehouseRepository.getItems()
            );
        });
    }

    @Test
    @DisplayName("Test 7: Null items map should throw exception")
    void testNullItemsMapThrowsException() {
        List<Allocation> allocations = createTestAllocations();
        assertThrows(IllegalArgumentException.class, () -> {
            controller.createPickingPlan(
                    PickingPlanService.PackingHeuristic.FIRST_FIT,
                    10.0,
                    PickingPlanService.OverflowPolicy.DEFER,
                    allocations,
                    null
            );
        });
    }

    // ------------------------------------------------------------
    // FUNCTIONAL TESTS
    // ------------------------------------------------------------

    @Test
    @DisplayName("Test 8: All packing heuristics produce valid plans")
    void testAllPackingHeuristics() {
        List<Allocation> allocations = createTestAllocations();
        PickingPlanController.setAllocations(allocations);
        double trolleyCapacity = 8.0;

        for (PickingPlanService.PackingHeuristic heuristic : PickingPlanService.PackingHeuristic.values()) {
            PickingPlan plan = controller.createPickingPlan(
                    heuristic,
                    trolleyCapacity,
                    PickingPlanService.OverflowPolicy.DEFER,
                    allocations,
                    warehouseRepository.getItems()
            );
            assertNotNull(plan);
            assertEquals(heuristic.name(), plan.getStrategy());
        }
    }

    @Test
    @DisplayName("Test 9: Both overflow policies produce valid plans")
    void testBothOverflowPolicies() {
        List<Allocation> allocations = createTestAllocations();
        PickingPlanController.setAllocations(allocations);
        double trolleyCapacity = 5.0;
        PickingPlanService.PackingHeuristic heuristic = PickingPlanService.PackingHeuristic.FIRST_FIT;

        for (PickingPlanService.OverflowPolicy policy : PickingPlanService.OverflowPolicy.values()) {
            PickingPlan plan = controller.createPickingPlan(
                    heuristic,
                    trolleyCapacity,
                    policy,
                    allocations,
                    warehouseRepository.getItems()
            );
            assertNotNull(plan);
        }
    }

    @Test
    @DisplayName("Test 10: Controller accepts all heuristic × overflow policy combinations")
    void testAllHeuristicOverflowCombinations() {
        List<Allocation> allocations = createTestAllocations();
        PickingPlanController.setAllocations(allocations);
        double trolleyCapacity = 6.0;

        for (PickingPlanService.PackingHeuristic heuristic : PickingPlanService.PackingHeuristic.values()) {
            for (PickingPlanService.OverflowPolicy policy : PickingPlanService.OverflowPolicy.values()) {
                PickingPlan plan = controller.createPickingPlan(
                        heuristic,
                        trolleyCapacity,
                        policy,
                        allocations,
                        warehouseRepository.getItems()
                );
                assertNotNull(plan, "Plan should not be null for " + heuristic + " + " + policy);
            }
        }
    }

    @Test
    @DisplayName("Test 11: Get events from service after small capacity plan")
    void testGetEvents() {
        List<Allocation> allocations = createTestAllocations();
        PickingPlanController.setAllocations(allocations);
        controller.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT,
                3.0,
                PickingPlanService.OverflowPolicy.SPLIT,
                allocations,
                warehouseRepository.getItems()
        );
        List<String> events = controller.getEvents();
        assertNotNull(events);
    }

    @Test
    @DisplayName("Test 12: Last picking plan updates correctly after multiple creations")
    void testLastPickingPlanUpdates() {
        List<Allocation> allocations = createTestAllocations();
        PickingPlanController.setAllocations(allocations);

        PickingPlan firstPlan = controller.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT,
                10.0,
                PickingPlanService.OverflowPolicy.DEFER,
                allocations,
                warehouseRepository.getItems()
        );

        PickingPlan secondPlan = controller.createPickingPlan(
                PickingPlanService.PackingHeuristic.BEST_FIT_DECREASING,
                8.0,
                PickingPlanService.OverflowPolicy.SPLIT,
                allocations,
                warehouseRepository.getItems()
        );

        PickingPlan lastPlan = PickingPlanController.getLastPickingPlan();
        assertEquals(secondPlan.getPlanId(), lastPlan.getPlanId());
    }

    @Test
    @DisplayName("Test 13: Trolley capacity validation")
    void testTrolleyCapacityValidation() {
        List<Allocation> allocations = createTestAllocations();
        PickingPlanController.setAllocations(allocations);
        double trolleyCapacity = 8.0;

        PickingPlan plan = controller.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT,
                trolleyCapacity,
                PickingPlanService.OverflowPolicy.DEFER,
                allocations,
                warehouseRepository.getItems()
        );

        assertNotNull(plan);
        for (Trolley trolley : plan.getTrolleys()) {
            assertTrue(trolley.getUsedWeight() <= trolleyCapacity + 0.01);
            assertEquals(trolleyCapacity, trolley.getCapacity());
        }
    }

    @Test
    @DisplayName("Test 14: Picking plan contains all required fields for pickers")
    void testPickingPlanCompletenessForPickers() {
        List<Allocation> allocations = createTestAllocations();
        PickingPlanController.setAllocations(allocations);
        double trolleyCapacity = 10.0;

        PickingPlan plan = controller.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT,
                trolleyCapacity,
                PickingPlanService.OverflowPolicy.DEFER,
                allocations,
                warehouseRepository.getItems()
        );

        assertNotNull(plan);
        for (Trolley trolley : plan.getTrolleys()) {
            assertNotNull(trolley.getTrolleyId());
            assertTrue(trolley.getCapacity() > 0);
            for (PickingItem item : trolley.getItems()) {
                assertNotNull(item.getOrderId());
                assertTrue(item.getLineNo() > 0);
                assertTrue(item.getAisle() > 0);
                assertTrue(item.getBay() > 0);
                assertNotNull(item.getBoxId());
                assertNotNull(item.getSku());
                assertTrue(item.getQuantity() > 0);
            }
        }
    }

    @Test
    @DisplayName("Test 15: Integration with USEI02 - allocation flow consistency")
    void testIntegrationWithUSEI02() {
        List<Allocation> usei02Allocations = createTestAllocations();
        PickingPlanController.setAllocations(usei02Allocations);

        boolean hasAllocations = controller.hasAllocations();
        String summary = controller.getAllocationsSummary();
        PickingPlan plan = controller.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT,
                10.0,
                PickingPlanService.OverflowPolicy.DEFER,
                usei02Allocations,
                warehouseRepository.getItems()
        );

        assertTrue(hasAllocations);
        assertNotNull(summary);
        assertNotNull(plan);
        assertTrue(plan.getTotalTrolleys() > 0);
    }

    @Test
    @DisplayName("Test 16: Clear controller state")
    void testClearState() {
        List<Allocation> allocations = createTestAllocations();
        PickingPlanController.setAllocations(allocations);
        controller.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT,
                10.0,
                PickingPlanService.OverflowPolicy.DEFER,
                allocations,
                warehouseRepository.getItems()
        );

        PickingPlanController.clearState();

        assertFalse(controller.hasAllocations());
        assertNull(PickingPlanController.getLastPickingPlan());
    }

    // ------------------------------------------------------------
    // HELPER METHOD
    // ------------------------------------------------------------

    private List<Allocation> createTestAllocations() {
        List<Allocation> allocations = new ArrayList<>();
        allocations.add(new Allocation("ORD1", 1, "SKU1", 5, "BOX1", "W1", 1, 1));
        allocations.add(new Allocation("ORD1", 2, "SKU2", 3, "BOX2", "W1", 1, 2));
        allocations.add(new Allocation("ORD2", 1, "SKU3", 8, "BOX3", "W1", 2, 1));
        allocations.add(new Allocation("ORD2", 2, "SKU4", 2, "BOX4", "W1", 2, 2));
        return allocations;
    }
}
