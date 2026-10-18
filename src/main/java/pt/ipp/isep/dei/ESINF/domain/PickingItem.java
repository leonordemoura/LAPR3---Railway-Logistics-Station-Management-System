package pt.ipp.isep.dei.ESINF.domain;

/**
 * Represents a unit of work to be picked: a quantity of a SKU from a specific
 * box and location (aisle/bay) that belongs to an order line.
 */
public class PickingItem {
    private String orderId;
    private int lineNo;
    private int aisle;
    private int bay;
    private String boxId;
    private String sku;
    private int quantity;
    private double weight;

    /**
     * Creates a picking item.
     *
     * @param orderId   owning order identifier
     * @param lineNo    order line number
     * @param aisle     warehouse aisle
     * @param bay       warehouse bay
     * @param boxId     box identifier
     * @param sku       stock keeping unit
     * @param quantity  quantity to pick
     * @param weight    total weight (kg) for the entire quantity
     */
    public PickingItem(String orderId, int lineNo, int aisle, int bay,
                       String boxId, String sku, int quantity, double weight) {
        this.orderId = orderId;
        this.lineNo = lineNo;
        this.aisle = aisle;
        this.bay = bay;
        this.boxId = boxId;
        this.sku = sku;
        this.quantity = quantity;
        this.weight = weight;
    }

    /** @return order identifier */
    public String getOrderId() { return orderId; }
    /** @return order line number */
    public int getLineNo() { return lineNo; }
    /** @return aisle */
    public int getAisle() { return aisle; }
    /** @return bay */
    public int getBay() { return bay; }
    /** @return box identifier */
    public String getBoxId() { return boxId; }
    /** @return SKU */
    public String getSku() { return sku; }
    /** @return quantity */
    public int getQuantity() { return quantity; }
    /** @return total weight (kg) */
    public double getWeight() { return weight; }

    /**
     * Computes the weight per unit (totalWeight / quantity).
     * Returns 0.0 when quantity is zero or negative.
     *
     * @return weight per unit (kg)
     */
    public double getWeightPerUnit() { return quantity > 0 ? weight / quantity : 0.0; }

    @Override
    public String toString() {
        return String.format("%s-%d %s x%d (%.2fkg) @ %d-%d [%s]",
                orderId, lineNo, sku, quantity, weight, aisle, bay, boxId);
    }
}
