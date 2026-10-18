package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.controller.OrderAllocationController;
import pt.ipp.isep.dei.ESINF.domain.*;
import pt.ipp.isep.dei.ESINF.domain.constants.AllocationMode;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.*;

/**
 * User Interface for USEI02 - Order Eligibility and Allocations.
 * Allows users to create partial or strict allocations and see the order lines eligibility.
 */
public class OrderAllocationUI implements Runnable {
    private static OrderAllocationController controller;
    private static Scanner scanner;

    /**
     * Constructor receives the existing OrderAllocationController.
     */
    public OrderAllocationUI() {
        controller = new OrderAllocationController();
        scanner = new Scanner(System.in);
    }

    /**
     * Runs the order allocation interface.
     */
    @Override
    public void run() {
        System.out.println(Utils.createHeader("USEI02: Order Eligibility & Allocation"));

        List<Order> orders = controller.getAllOrders();

        if (orders.isEmpty()) {
            System.out.println("No orders available.");
            return;
        }

        // Display sorted orders
        List<Order> sortedOrders = controller.getSortedOrders(orders);
        displayOrders(sortedOrders);

        // Displays Order Lines by Order Hierarchy
        displayOrderLines(sortedOrders);

        // Select allocation mode
        AllocationMode mode = selectAllocationMode();

        List<Box> allBoxes = controller.getAllBoxes();

        if (allBoxes.isEmpty()) {
            System.out.println("No boxes available.");
            return;
        }

        // Process orders
        System.out.println("\nProcessing orders in " + mode + " mode...\n");
        OrderEligibilityResult result = controller.processOrders(mode);

        // Display results
        displayResults(result);
    }

    /**
     * Displays sorted orders.
     */
    private void displayOrders(List<Order> orders) {
        System.out.println("Available Orders:");
        System.out.println("-".repeat(70));
        System.out.printf("%-10s| %-9s| %-20s%n", "OrderID", "Priority", "Due Date");
        System.out.println("-".repeat(70));

        for (Order order : orders) {
            System.out.printf("%-10s| %-9d| %-20s%n", order.getOrderId(), order.getPriority(), order.getDueDate());
        }
        System.out.println("-".repeat(70));
    }

    /**
     * Displays sorted order lines for each order.
     */
    private void displayOrderLines(List<Order> sortedOrders) {
        System.out.println("\nOrder Lines:");
        System.out.println("-".repeat(70));

        for (Order order : sortedOrders) {
            List<OrderLine> orderLines = controller.getOrderLinesForOrder(order.getOrderId());

            if (orderLines.isEmpty()) {
                System.out.printf("Order %s has no lines.%n%n", order.getOrderId());
                continue;
            }

            List<OrderLine> sortedLines = controller.getSortedOrderLines(orderLines);

            System.out.printf("Order: %s (Priority: %d, Due: %s)%n", order.getOrderId(), order.getPriority(), order.getDueDate());
            System.out.println("-".repeat(70));
            System.out.printf("%-10s| %-12s| %-10s%n", "Line No", "SKU", "Quantity");
            System.out.println("-".repeat(70));

            for (OrderLine line : sortedLines) {
                System.out.printf("%-10d| %-12s| %-10d%n", line.getLineNo(), line.getSku(), line.getQty());
            }
            System.out.println("-".repeat(70) + "\n");
        }
    }

    private AllocationMode selectAllocationMode() {
        System.out.println("\nAllocation Modes:");
        System.out.println("1 - STRICT (default - all or nothing)");
        System.out.println("2 - PARTIAL (accept partial fulfillment)");
        System.out.print("\nSelect mode (press Enter for STRICT): ");

        String input = scanner.nextLine().trim();

        if (input.isEmpty() || input.equals("1")) {
            return AllocationMode.STRICT;
        } else if (input.equals("2")) {
            return AllocationMode.PARTIAL;
        } else {
            System.out.println("Invalid choice. Using STRICT mode.");
            return AllocationMode.STRICT;
        }
    }

    /**
     * Displays results of US implementation.
     */
    private void displayResults(OrderEligibilityResult result) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("ORDER ELIGIBILITY REPORT");
        System.out.println("=".repeat(70));
        System.out.printf("%-10s| %-5s| %-10s| %-11s| %-11s| %s%n",
                "OrderID", "Line", "SKU", "Requested", "Allocated", "Status");
        System.out.println("-".repeat(70));

        for (LineEligibility line : result.getEligibilityList()) {
            System.out.println(line);
        }

        System.out.println("\n\n\n" + "=".repeat(70));
        System.out.println("ALLOCATION DETAILS");
        System.out.println("=".repeat(70));
        System.out.printf("%-10s| %-5s| %-10s| %-4s| %-10s| %-5s| %-5s| %s%n",
                "OrderID", "Line", "SKU", "Qty", "BoxID", "Aisle", "Bay", "WarehouseID");
        System.out.println("-".repeat(70));

        if (result.getAllocationList().isEmpty()) {
            System.out.println("No allocations made (all orders UNDISPATCHABLE)");
        } else {
            for (Allocation alloc : result.getAllocationList()) {
                System.out.printf("%-10s| %-5d| %-10s| %-4d| %-10s| %-5d| %-5d| %d%n", alloc.getOrderId(), alloc.getLineNo(), alloc.getSku(), alloc.getQty(), alloc.getBoxId(), alloc.getAisle(), alloc.getBay(), alloc.getWarehouseIdNumber());
            }
        }
        System.out.println("=".repeat(70) + "\n");
    }
}
