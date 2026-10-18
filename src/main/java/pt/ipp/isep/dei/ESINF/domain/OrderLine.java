package pt.ipp.isep.dei.ESINF.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Domain entity representing a single order line. Includes utilities for sorting and filtering.
 */
public class OrderLine {
    private String orderId;
    private int lineNo;
    private String sku;
    private int qty;

    /**
     * Creates an order line.
     * @param orderId order identifier
     * @param lineNo  line number
     * @param sku     SKU
     * @param qty     requested quantity
     */
    public OrderLine(String orderId, int lineNo, String sku, int qty) {
        this.orderId = orderId;
        this.lineNo = lineNo;
        this.sku = sku;
        this.qty = qty;
    }

    /**
     * Returns the order identifier.
     * @return order ID
     */
    public String getOrderId() { return orderId; }

    /**
     * Returns the order line number.
     * @return line number
     */
    public int getLineNo() { return lineNo; }

    /**
     * Returns the SKU identifier.
     * @return SKU
     */
    public String getSku() { return sku; }

    /**
     * Returns the allocated quantity.
     * @return quantity
     */
    public int getQty() { return qty; }

    /**
     * Sorts a list of order lines
     * Sorting criteria: lineNo ASC
     *
     * @param lines list of order lines to sort
     * @return new sorted list of order lines
     */
    public static List<OrderLine> sortOrderLines(List<OrderLine> lines) {
        if (lines == null || lines.size() <= 1) {
            return lines == null ? new ArrayList<>() : new ArrayList<>(lines);
        }

        lines.sort(Comparator.comparing(OrderLine::getLineNo));
        return lines;
    }

    /**
     * Filters order lines by orderId
     *
     * @param lines all order lines
     * @param orderId target order ID
     * @return filtered list of order lines
     */
    public static List<OrderLine> filterByOrderId(List<OrderLine> lines, String orderId) {
        if (lines == null) {
            return new ArrayList<>();
        }

        List<OrderLine> filtered = new ArrayList<>();

        for (OrderLine line : lines) {
            if (line.getOrderId().equals(orderId)) {
                filtered.add(line);
            }
        }

        return filtered;
    }

    @Override
    public String toString() {
        return String.format("OrderLine %s\n line=%d\n sku=%s\n qty=%d]",
                orderId, lineNo, sku, qty);
    }
}
