package pt.ipp.isep.dei.ESINF.domain;

import pt.ipp.isep.dei.ESINF.domain.constants.AllocationStatus;

/**
 * Represents eligibility status of an order line for allocation processing.
 */
public class LineEligibility {
    private String orderId;
    private int lineNo;
    private String sku;
    private int requestedQty;
    private int allocatedQty;
    private AllocationStatus status;

    /**
     * Creates an order line eligibility record.
     *
     * @param orderId       order identifier
     * @param lineNo        order line number
     * @param sku           SKU identifier
     * @param requestedQty  requested quantity
     * @param allocatedQty  allocated quantity
     * @param status        allocation status
     */
    public LineEligibility(String orderId, int lineNo, String sku, int requestedQty, int allocatedQty, AllocationStatus status) {
        this.orderId = orderId;
        this.lineNo = lineNo;
        this.sku = sku;
        this.requestedQty = requestedQty;
        this.allocatedQty = allocatedQty;
        this.status = status;
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
     * Returns the requested quantity.
     * @return requested quantity
     */
    public int getRequestedQty() { return requestedQty; }

    /**
     * Returns the allocated quantity.
     * @return allocated quantity
     */
    public int getAllocatedQty() { return allocatedQty; }

    /**
     * Returns the allocation status.
     * @return status
     */
    public AllocationStatus getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("%-10s| %-5d| %-10s| %-11d| %-11d| %s",
                orderId, lineNo, sku, requestedQty, allocatedQty, status);
    }
}
