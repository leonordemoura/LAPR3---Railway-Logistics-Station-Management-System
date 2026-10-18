package pt.ipp.isep.dei.ESINF.controller;

import pt.ipp.isep.dei.ESINF.domain.*;
import pt.ipp.isep.dei.ESINF.domain.constants.AllocationMode;
import pt.ipp.isep.dei.ESINF.services.OrderAllocationService;
import pt.ipp.isep.dei.ESINF.services.impl.OrderAllocationImpl;

import java.util.*;

/**
 * Controller for USEI02 - Order Eligibility and Allocation functionality.
 * Handles the creation of allocations and classifies each order line.
 */
public class OrderAllocationController {
    private OrderAllocationService service;

    /**
     * Constructor that initiates the OrderProcessor.
     *
     */
    public OrderAllocationController() {
        this.service = new OrderAllocationImpl();
    }

    /**
     * Gets all orders from csv.
     *
     * @return list of all orders (unsorted)
     */
    public List<Order> getAllOrders() {
        return service.getAllOrders();
    }

    /**
     * Sorts orders according to acceptance criteria
     *
     * @return sorted list of orders
     */
    public List<Order> getSortedOrders(List<Order> orders) {
        return Order.sortOrders(orders);
    }

    /**
     * Gets order lines for a specific order
     *
     * @param orderId order ID to filter by
     * @return list of order lines for the order
     */
    public List<OrderLine> getOrderLinesForOrder(String orderId) {
        List<OrderLine> allLines = service.getAllOrderLines();
        return OrderLine.filterByOrderId(allLines, orderId);
    }

    /**
     * Sorts order lines
     * Sorting criteria: lineNo ASC
     *
     * @param orderLines list of order lines to sort
     * @return sorted list of order lines
     */
    public List<OrderLine> getSortedOrderLines(List<OrderLine> orderLines) {
        return OrderLine.sortOrderLines(orderLines);
    }

    /**
     * Get boxes already sorted by FEFO/FIFO
     *
     * @return list of boxes sorted by FEFO/FIFO
     */
    public List<Box> getAllBoxes(){
        return service.getAllBoxes();
    }

    /**
     * Processes orders and allocates inventory.
     *
     * @param mode allocation mode (STRICT or PARTIAL)
     * @return order eligibility result
     */
    public OrderEligibilityResult processOrders(AllocationMode mode) {
       return service.processOrders(mode);
    }
}
