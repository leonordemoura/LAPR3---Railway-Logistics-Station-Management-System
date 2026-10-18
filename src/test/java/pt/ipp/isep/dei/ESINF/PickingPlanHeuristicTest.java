package pt.ipp.isep.dei.ESINF;

import org.junit.jupiter.api.*;
import pt.ipp.isep.dei.ESINF.domain.*;
import pt.ipp.isep.dei.ESINF.services.PickingPlanService;
import pt.ipp.isep.dei.ESINF.services.impl.PickingPlanServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Combined test: compares heuristics and policies with explicit best/worst case.
 */
public class PickingPlanHeuristicTest {

    private PickingPlanServiceImpl service;
    private List<Allocation> allocations;
    private Map<String, Item> items;

    @BeforeEach
    void setUp() {
        service = new PickingPlanServiceImpl();
        allocations = new ArrayList<>();
        items = new HashMap<>();

        // Define items with varying weights to create best/worst case scenarios
        items.put("SKU1", new Item("SKU1", "Light Item", "A", "unit", 0.0, 3.0));   // 3kg
        items.put("SKU2", new Item("SKU2", "Medium Item", "B", "unit", 0.0, 5.0));  // 5kg
        items.put("SKU3", new Item("SKU3", "Heavy Item", "C", "unit", 0.0, 7.0));   // 7kg
        items.put("SKU4", new Item("SKU4", "Very Heavy", "D", "unit", 0.0, 9.0));   // 9kg
        items.put("SKU5", new Item("SKU5", "Ultra Heavy", "E", "unit", 0.0, 14.0)); // 14kg

        // Allocations designed to stress trolley capacity (15kg)
        allocations.add(new Allocation("ORD1", 1, "SKU1", 2, "BOX1", "W1", 1, 1)); // 6kg
        allocations.add(new Allocation("ORD1", 2, "SKU2", 1, "BOX2", "W1", 1, 2)); // 5kg
        allocations.add(new Allocation("ORD2", 1, "SKU3", 1, "BOX3", "W1", 2, 1)); // 7kg
        allocations.add(new Allocation("ORD2", 2, "SKU4", 1, "BOX4", "W1", 2, 2)); // 9kg
        allocations.add(new Allocation("ORD3", 1, "SKU5", 1, "BOX5", "W1", 3, 1)); // 14kg
    }

    // ---------------------- HELPER ----------------------
    private void runTest(PickingPlanServiceImpl service,
                         PickingPlanService.PackingHeuristic heuristic,
                         PickingPlanService.OverflowPolicy policy,
                         double trolleyCapacity,
                         double expectedTotalWeight) {

        PickingPlan plan = service.createPickingPlan(heuristic, trolleyCapacity, policy, allocations, items);

        // 1. Visual inspection
        System.out.printf("\n=== %s + %s ===\n", heuristic, policy);
        plan.getTrolleys().forEach(System.out::println);

        // 2. Assertions
        assertNotNull(plan);
        assertEquals(expectedTotalWeight, plan.getTrolleys().stream().mapToDouble(Trolley::getUsedWeight).sum(), 0.01);
        plan.getTrolleys().forEach(t -> assertTrue(t.getUsedWeight() <= trolleyCapacity + 0.01));
    }

    // ---------------------- BEST/WORST CASE ----------------------
    @Test
    @DisplayName("Compare heuristics with SPLIT policy")
    void testSplitPolicy() {
        // SPLIT → all items should eventually be packed
        runTest(service, PickingPlanService.PackingHeuristic.FIRST_FIT, PickingPlanService.OverflowPolicy.SPLIT, 15.0, 41.0);
        runTest(service, PickingPlanService.PackingHeuristic.FIRST_FIT_DECREASING, PickingPlanService.OverflowPolicy.SPLIT, 15.0, 41.0);
        runTest(service, PickingPlanService.PackingHeuristic.BEST_FIT_DECREASING, PickingPlanService.OverflowPolicy.SPLIT, 15.0, 41.0);
    }

    @Test
    @DisplayName("Compare heuristics with DEFER policy")
    void testDeferPolicy() {
        // DEFER → overweight items may be deferred
        runTest(service, PickingPlanService.PackingHeuristic.FIRST_FIT, PickingPlanService.OverflowPolicy.DEFER, 15.0, 41.0);
        runTest(service, PickingPlanService.PackingHeuristic.FIRST_FIT_DECREASING, PickingPlanService.OverflowPolicy.DEFER, 15.0, 41.0);
        runTest(service, PickingPlanService.PackingHeuristic.BEST_FIT_DECREASING, PickingPlanService.OverflowPolicy.DEFER, 15.0, 41.0);
    }

    @Test
    @DisplayName("Ensure service handles empty allocations gracefully")
    void testEmptyAllocationsThrows() {
        assertThrows(IllegalStateException.class, () ->
                service.createPickingPlan(PickingPlanService.PackingHeuristic.FIRST_FIT, 15.0,
                        PickingPlanService.OverflowPolicy.SPLIT, new ArrayList<>(), items));
    }

}
