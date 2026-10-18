package pt.ipp.isep.dei.ESINF;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import pt.ipp.isep.dei.ESINF.controller.PickingPlanController;
import pt.ipp.isep.dei.ESINF.domain.*;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.PickingPlanService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class for PickingPlanController
 * Tests the complete workflow from USEI02 allocations to USEI03 picking plans
 */
class PickingPlanIntegrationTest {

    private PickingPlanController controller;
    private WarehouseRepository warehouseRepository;

    @BeforeEach
    void setUp() {
        controller = new PickingPlanController();
        warehouseRepository = Repositories.getInstance().getWarehouseRepository();
        setupTestWarehouse();
    }

    private void setupTestWarehouse() {
        // Clear existing data
        warehouseRepository.getItems().clear();
        
        // Setup warehouse with various items
        warehouseRepository.addItem(new Item("SKU001", "Heavy Product", "Category1", "kg", 0.0, 2.5));
        warehouseRepository.addItem(new Item("SKU002", "Medium Product", "Category2", "kg", 0.0, 1.0));
        warehouseRepository.addItem(new Item("SKU003", "Light Product", "Category3", "kg", 0.0, 0.3));
        warehouseRepository.addItem(new Item("SKU004", "Very Heavy Product", "Category4", "kg", 0.0, 5.0));
        warehouseRepository.addItem(new Item("SKU005", "Standard Product", "Category5", "kg", 0.0, 1.5));
    }

    @Test
    @DisplayName("Integration Test 1: Complete workflow from allocations to picking plan")
    void testCompleteWorkflow() {
        // Arrange - Simulate USEI02 output
        List<Allocation> allocations = createRealisticAllocations();
        PickingPlanController.setAllocations(allocations);
        
        // Act - USEI03 processing
        boolean hasAllocations = controller.hasAllocations();
        assertTrue(hasAllocations, "Should have allocations from USEI02");
        
        String summary = controller.getAllocationsSummary();
        assertNotNull(summary, "Should provide allocation summary");
        
        // Create picking plan with different heuristics
        PickingPlan ffPlan = controller.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT,
                15.0,
                PickingPlanService.OverflowPolicy.DEFER,
                allocations,
                warehouseRepository.getItems()
        );
        
        PickingPlan ffdPlan = controller.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT_DECREASING,
                15.0,
                PickingPlanService.OverflowPolicy.DEFER,
                allocations,
                warehouseRepository.getItems()
        );
        
        PickingPlan bfdPlan = controller.createPickingPlan(
                PickingPlanService.PackingHeuristic.BEST_FIT_DECREASING,
                15.0,
                PickingPlanService.OverflowPolicy.DEFER,
                allocations,
                warehouseRepository.getItems()
        );

        // Assert
        assertNotNull(ffPlan, "First Fit plan should be created");
        assertNotNull(ffdPlan, "First Fit Decreasing plan should be created");
        assertNotNull(bfdPlan, "Best Fit Decreasing plan should be created");
        
        // All plans should handle the same allocations
        assertTrue(ffPlan.getTotalTrolleys() > 0, "Should have at least one trolley");
        assertTrue(ffdPlan.getTotalTrolleys() > 0, "Should have at least one trolley");
        assertTrue(bfdPlan.getTotalTrolleys() > 0, "Should have at least one trolley");
        
        // Verify plan completeness
        verifyPickingPlanCompleteness(ffPlan);
        verifyPickingPlanCompleteness(ffdPlan);
        verifyPickingPlanCompleteness(bfdPlan);
    }

    @Test
    @DisplayName("Integration Test 2: Test with SPLIT overflow policy")
    void testSplitOverflowPolicy() {
        // Arrange
        List<Allocation> allocations = createRealisticAllocations();
        PickingPlanController.setAllocations(allocations);
        
        // Act - Use small capacity to trigger splitting
        PickingPlan plan = controller.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT,
                8.0, // Small capacity to force splitting
                PickingPlanService.OverflowPolicy.SPLIT,
                allocations,
                warehouseRepository.getItems()
        );
        
        List<String> events = controller.getEvents();

        // Assert
        assertNotNull(plan, "Plan should be created");
        assertTrue(plan.getTotalTrolleys() > 0, "Should have trolleys");
        
        // Check for partial allocation events (may or may not occur)
        boolean hasPartialAllocations = events.stream()
                .anyMatch(event -> event.contains("partial allocation"));
        
        // This is informational - partial allocations may or may not occur
        System.out.println("Has partial allocations: " + hasPartialAllocations);
        
        // This may or may not be true depending on the specific weights
        System.out.println("Events with SPLIT policy: " + events);
    }

    @Test
    @DisplayName("Integration Test 3: Test with DEFER overflow policy")
    void testDeferOverflowPolicy() {
        // Arrange
        List<Allocation> allocations = createRealisticAllocations();
        PickingPlanController.setAllocations(allocations);
        
        // Act - Use very small capacity to trigger deferring
        PickingPlan plan = controller.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT,
                3.0, // Very small capacity to force deferring
                PickingPlanService.OverflowPolicy.DEFER,
                allocations,
                warehouseRepository.getItems()
        );
        
        List<String> events = controller.getEvents();

        // Assert
        assertNotNull(plan, "Plan should be created");
        assertTrue(plan.getTotalTrolleys() > 0, "Should have trolleys");
        
        // Check for skipped items events (may or may not occur)
        boolean hasSkippedItems = events.stream()
                .anyMatch(event -> event.contains("skipped due to capacity"));
        
        // This is informational - skipped items may or may not occur
        System.out.println("Has skipped items: " + hasSkippedItems);
        
        // This may or may not be true depending on the specific weights
        System.out.println("Events with DEFER policy: " + events);
    }

    @Test
    @DisplayName("Integration Test 4: Test trolley utilization efficiency")
    void testTrolleyUtilizationEfficiency() {
        // Arrange
        List<Allocation> allocations = createRealisticAllocations();
        PickingPlanController.setAllocations(allocations);
        
        // Act
        PickingPlan plan = controller.createPickingPlan(
                PickingPlanService.PackingHeuristic.BEST_FIT_DECREASING,
                12.0,
                PickingPlanService.OverflowPolicy.DEFER,
                allocations,
                warehouseRepository.getItems()
        );

        // Assert
        assertNotNull(plan, "Plan should be created");
        assertTrue(plan.getTotalTrolleys() > 0, "Should have trolleys");
        
        // Verify utilization is reasonable
        double avgUtilization = plan.getAverageUtilization();
        assertTrue(avgUtilization > 0, "Should have positive utilization");
        assertTrue(avgUtilization <= 100, "Utilization should not exceed 100%");
        
        // Verify each trolley respects capacity
        for (Trolley trolley : plan.getTrolleys()) {
            assertTrue(trolley.getUsedWeight() <= trolley.getCapacity() + 0.01, 
                      "Trolley should not exceed capacity");
            assertTrue(trolley.getUtilization() >= 0, 
                      "Trolley utilization should be non-negative");
        }
        
        System.out.println("Plan efficiency: " + plan.getTotalTrolleys() + " trolleys, " + 
                         String.format("%.1f", avgUtilization) + "% average utilization");
    }

    @Test
    @DisplayName("Integration Test 5: Test with empty allocations")
    void testEmptyAllocations() {
        // Arrange
        List<Allocation> emptyAllocations = new ArrayList<>();
        PickingPlanController.setAllocations(emptyAllocations);

        // Act
        boolean hasAllocations = controller.hasAllocations();
        String summary = controller.getAllocationsSummary();

        // Assert (parte do summary e hasAllocations)
        assertFalse(hasAllocations, "Should not have allocations");
        assertTrue(summary.contains("No allocations available"), "Summary should indicate no allocations");

        // O importante: agora testar se a exceção é lançada!
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
    @DisplayName("Integration Test 6: Test picking plan information completeness")
    void testPickingPlanInformationCompleteness() {
        // Arrange
        List<Allocation> allocations = createRealisticAllocations();
        PickingPlanController.setAllocations(allocations);
        
        // Act
        PickingPlan plan = controller.createPickingPlan(
                PickingPlanService.PackingHeuristic.FIRST_FIT,
                15.0,
                PickingPlanService.OverflowPolicy.DEFER,
                allocations,
                warehouseRepository.getItems()
        );

        // Assert
        assertNotNull(plan, "Plan should be created");
        
        // Verify plan metadata
        assertNotNull(plan.getPlanId(), "Plan should have an ID");
        assertNotNull(plan.getStrategy(), "Plan should have a strategy");
        assertTrue(plan.getTotalTrolleys() > 0, "Should have trolleys");
        
        // Verify each trolley has complete information
        for (Trolley trolley : plan.getTrolleys()) {
            assertNotNull(trolley.getTrolleyId(), "Trolley should have an ID");
            assertTrue(trolley.getCapacity() > 0, "Trolley should have positive capacity");
            assertNotNull(trolley.getItems(), "Trolley should have items list");
            
            // Verify each picking item has all required information for pickers
            for (PickingItem item : trolley.getItems()) {
                assertNotNull(item.getOrderId(), "Item should have order ID");
                assertTrue(item.getLineNo() > 0, "Item should have positive line number");
                assertTrue(item.getAisle() > 0, "Item should have positive aisle");
                assertTrue(item.getBay() > 0, "Item should have positive bay");
                assertNotNull(item.getBoxId(), "Item should have box ID");
                assertNotNull(item.getSku(), "Item should have SKU");
                assertTrue(item.getQuantity() > 0, "Item should have positive quantity");
                assertTrue(item.getWeight() >= 0, "Item should have non-negative weight");
            }
        }
        
        System.out.println("=== Picking Plan Information ===");
        System.out.println("Plan ID: " + plan.getPlanId());
        System.out.println("Strategy: " + plan.getStrategy());
        System.out.println("Total Trolleys: " + plan.getTotalTrolleys());
        System.out.println("Average Utilization: " + String.format("%.1f", plan.getAverageUtilization()) + "%");
        
        for (int i = 0; i < plan.getTrolleys().size(); i++) {
            Trolley trolley = plan.getTrolleys().get(i);
            System.out.println("Trolley " + (i + 1) + ": " + trolley.getTrolleyId() + 
                             " (" + String.format("%.1f", trolley.getUtilization()) + "% full, " + 
                             trolley.getItems().size() + " items)");
        }
    }

    private List<Allocation> createRealisticAllocations() {
        List<Allocation> allocations = new ArrayList<>();
        
        // Order 1 - Mixed items
        allocations.add(new Allocation("ORD001", 1, "SKU001", 2, "BOX001", "W1", 1, 1));
        allocations.add(new Allocation("ORD001", 2, "SKU002", 5, "BOX002", "W1", 1, 2));
        allocations.add(new Allocation("ORD001", 3, "SKU003", 10, "BOX003", "W1", 1, 3));
        
        // Order 2 - Heavy items
        allocations.add(new Allocation("ORD002", 1, "SKU004", 1, "BOX004", "W1", 2, 1));
        allocations.add(new Allocation("ORD002", 2, "SKU001", 3, "BOX005", "W1", 2, 2));
        
        // Order 3 - Light items
        allocations.add(new Allocation("ORD003", 1, "SKU003", 15, "BOX006", "W1", 3, 1));
        allocations.add(new Allocation("ORD003", 2, "SKU002", 4, "BOX007", "W1", 3, 2));
        
        // Order 4 - Mixed items
        allocations.add(new Allocation("ORD004", 1, "SKU005", 6, "BOX008", "W1", 4, 1));
        allocations.add(new Allocation("ORD004", 2, "SKU001", 1, "BOX009", "W1", 4, 2));
        
        return allocations;
    }

    private void verifyPickingPlanCompleteness(PickingPlan plan) {
        assertNotNull(plan, "Plan should not be null");
        assertNotNull(plan.getPlanId(), "Plan should have an ID");
        assertNotNull(plan.getStrategy(), "Plan should have a strategy");
        assertNotNull(plan.getTrolleys(), "Plan should have trolleys");
        
        for (Trolley trolley : plan.getTrolleys()) {
            assertNotNull(trolley.getTrolleyId(), "Trolley should have an ID");
            assertTrue(trolley.getCapacity() > 0, "Trolley should have positive capacity");
            assertNotNull(trolley.getItems(), "Trolley should have items");
            
            for (PickingItem item : trolley.getItems()) {
                assertNotNull(item.getOrderId(), "Item should have order ID");
                assertTrue(item.getLineNo() > 0, "Item should have positive line number");
                assertTrue(item.getAisle() > 0, "Item should have positive aisle");
                assertTrue(item.getBay() > 0, "Item should have positive bay");
                assertNotNull(item.getBoxId(), "Item should have box ID");
                assertNotNull(item.getSku(), "Item should have SKU");
                assertTrue(item.getQuantity() > 0, "Item should have positive quantity");
                assertTrue(item.getWeight() >= 0, "Item should have non-negative weight");
            }
        }
    }
}
