package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.ESINF.controller.PickingPlanController;
import pt.ipp.isep.dei.ESINF.domain.Allocation;
import pt.ipp.isep.dei.ESINF.domain.Item;
import pt.ipp.isep.dei.ESINF.domain.PickingPlan;
import pt.ipp.isep.dei.ESINF.domain.Trolley;
import pt.ipp.isep.dei.ESINF.services.PickingPlanService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * User Interface for USEI03 - Picking Plans.
 * Allows users to create picking plans using different packing heuristics.
 */
public class PickingPlanUI implements Runnable {
    
    private final PickingPlanController controller;
    private final Scanner scanner;
    private final double DEFAULT_TROLLEY_CAPACITY = 1000.0;

    public PickingPlanUI() {
        this.controller = new PickingPlanController();
        this.scanner = new Scanner(System.in);
    }
    
    @Override
    public void run() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("USEI03: Picking Plans");
        System.out.println("=".repeat(70) + "\n");
        
        // Check if allocations are available
        if (!controller.hasAllocations()) {
            System.out.println("No allocations available.");
            System.out.println("Please run Order Eligibility & Allocation (USEI02) first.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Display allocations summary
        System.out.println(controller.getAllocationsSummary());
        System.out.println();
        
        // Get allocations and items
        List<Allocation> allocations = controller.getAllocations();
        Map<String, Item> skuToItem = controller.getAllItems();
        
        // Select packing heuristic
        PickingPlanService.PackingHeuristic heuristic = selectPackingHeuristic();
        
        // Get trolley capacity
        double trolleyCapacity = getTrolleyCapacity();
        
        // Select overflow policy
        PickingPlanService.OverflowPolicy overflowPolicy = selectOverflowPolicy();
        
        // Create picking plan
        System.out.println("\nCreating picking plan...");
        PickingPlan plan = controller.createPickingPlan(heuristic, trolleyCapacity, overflowPolicy, allocations, skuToItem);

        // Display results
        displayPickingPlan(plan);
        
        // Display events if any
        List<String> events = controller.getEvents();
        if (!events.isEmpty()) {
            displayEvents(events);
        }
        
        System.out.println("\nReturning to menu...");
    }
    
    private PickingPlanService.PackingHeuristic selectPackingHeuristic() {
        System.out.println("Select Packing Heuristic:");
        System.out.println("1 - First Fit (FF)");
        System.out.println("2 - First Fit Decreasing (FFD)");
        System.out.println("3 - Best Fit Decreasing (BFD)");
        System.out.print("Enter choice (1-3): ");
        
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        return PickingPlanService.PackingHeuristic.FIRST_FIT;
                    case 2:
                        return PickingPlanService.PackingHeuristic.FIRST_FIT_DECREASING;
                    case 3:
                        return PickingPlanService.PackingHeuristic.BEST_FIT_DECREASING;
                    default:
                        System.out.print("Invalid choice. Please enter 1, 2, or 3: ");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number (1-3): ");
            }
        }
    }
    
    private double getTrolleyCapacity() {
        System.out.print("Enter trolley capacity in kg (default: " + DEFAULT_TROLLEY_CAPACITY + "): ");
        String input = scanner.nextLine().trim();
        
        if (input.isEmpty()) {
            return DEFAULT_TROLLEY_CAPACITY;
        }
        
        try {
            double capacity = Double.parseDouble(input);
            if (capacity <= 0) {
                System.out.println("Invalid capacity. Using default: " + DEFAULT_TROLLEY_CAPACITY + " kg");
                return DEFAULT_TROLLEY_CAPACITY;
            }
            return capacity;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using default: " + DEFAULT_TROLLEY_CAPACITY + " kg");
            return DEFAULT_TROLLEY_CAPACITY;
        }
    }
    
    private PickingPlanService.OverflowPolicy selectOverflowPolicy() {
        System.out.println("Select Overflow Policy:");
        System.out.println("1 - Split (put part in current trolley, remainder in next)");
        System.out.println("2 - Defer (move entire allocation to next trolley)");
        System.out.print("Enter choice (1-2, default: 1): ");
        
        String input = scanner.nextLine().trim();
        if (input.isEmpty() || input.equals("1")) {
            return PickingPlanService.OverflowPolicy.SPLIT;
        }
        
        try {
            int choice = Integer.parseInt(input);
            return (choice == 2) ? PickingPlanService.OverflowPolicy.DEFER : PickingPlanService.OverflowPolicy.SPLIT;
        } catch (NumberFormatException e) {
            return PickingPlanService.OverflowPolicy.SPLIT;
        }
    }

    private void displayPickingPlan(PickingPlan plan) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("PICKING PLAN RESULTS");
        System.out.println("=".repeat(70));
        System.out.printf("Plan ID: %s%n", plan.getPlanId());
        System.out.printf("Strategy: %s%n", plan.getStrategy());
        System.out.printf("Total Trolleys: %d%n", plan.getTotalTrolleys());
        System.out.printf("Average Utilization: %.1f%%%n", plan.getAverageUtilization());
        System.out.println();

        List<Trolley> trolleys = plan.getTrolleys();
        for (int i = 0; i < trolleys.size(); i++) {
            Trolley trolley = trolleys.get(i);
            System.out.printf("Trolley %d (T%d): %.1f%% full (%.2f/%.2f kg)%n",
                           i + 1, i + 1, trolley.getUtilization(), trolley.getUsedWeight(), trolley.getCapacity());

            // Display picking items for this trolley
            if (!trolley.getItems().isEmpty()) {
                System.out.println("  Picking Items:");
                System.out.println("  Order ID | Line | Aisle | Bay |  Box ID  |   SKU    | Qty |  Weight |");
                System.out.println("  " + "-".repeat(70));

                for (var item : trolley.getItems()) {
                    System.out.printf("  %-8s | %4d | %5d | %3d | %-6s | %-8s | %3d | %.2f kg%n",
                                   item.getOrderId(), item.getLineNo(), item.getAisle(),
                                   item.getBay(), item.getBoxId(), item.getSku(),
                                   item.getQuantity(), item.getWeight());
                }
            } else {
                System.out.println("  (Empty trolley)");
            }
            System.out.println();
        }
    }

    private void displayEvents(List<String> events) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("PACKING EVENTS");
        System.out.println("=".repeat(70));
        
        for (String event : events) {
            System.out.println("• " + event);
        }
        System.out.println();
    }
}
