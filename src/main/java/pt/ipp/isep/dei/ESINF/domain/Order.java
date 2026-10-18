package pt.ipp.isep.dei.ESINF.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Domain entity representing a customer's order with due date and priority.
 * Provides deterministic sorting utilities used by USEI02.
 */
public class Order {
    private String orderId;
    private Date dueDate;
    private int priority;

    /**
     * Creates an order.
     *
     * @param orderId  order identifier
     * @param dueDate  due date
     * @param priority lower values mean higher priority
     */
    public Order(String orderId, Date dueDate, int priority) {
        this.orderId = orderId;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    /**
     * Returns the order identifier.
     * @return order ID
     */
    public String getOrderId() { return orderId; }

    /**
     * Returns the order due date.
     * @return due date
     */
    public Date getDueDate() { return dueDate; }

    /**
     * Returns the order priority.
     * @return priority
     */
    public int getPriority() { return priority; }

    // Logic
    /**
     * Sorts a list of orders
     * Sorting criteria:
     * 1. priority ASC
     * 2. dueDate ASC
     * 3. orderId ASC
     *
     * @param orders list of orders to sort
     * @return new sorted list of orders
     */
    public static List<Order> sortOrders(List<Order> orders) {
        if (orders == null || orders.size() <= 1) {
            return orders == null ? new ArrayList<>() : new ArrayList<>(orders);
        }

        orders.sort(Comparator.comparing(Order::getPriority).thenComparing(Order::getDueDate).thenComparing(Order::getOrderId));

        return orders;
    }

    @Override
    public String toString() {
        return String.format("Order[%s, priority=%d, dueDate=%s]",
                orderId, priority, dueDate);
    }
}
