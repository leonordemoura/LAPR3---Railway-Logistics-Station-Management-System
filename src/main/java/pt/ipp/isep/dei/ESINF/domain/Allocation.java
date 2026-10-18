package pt.ipp.isep.dei.ESINF.domain;

/**
 * Represents an allocation decision produced: a quantity (qty) of a
 * SKU assigned from a specific box and location to fulfill an order line.
 */
public class Allocation {
    private String orderId;
    private int lineNo;
    private String sku;
    private int qty;
    private String boxId;
    private String warehouseId;
    private int aisle;
    private int bay;

    /**
     * Creates an allocation record.
     *
     * @param orderId     order identifier
     * @param lineNo      order line number
     * @param sku         SKU allocated
     * @param qty         quantity allocated
     * @param boxId       source box identifier
     * @param warehouseId warehouse identifier (where box is located)
     * @param aisle       aisle of the box
     * @param bay         bay of the box
     */
    public Allocation(String orderId, int lineNo, String sku, int qty, String boxId, String warehouseId, int aisle, int bay) {
        this.orderId = orderId;
        this.lineNo = lineNo;
        this.sku = sku;
        this.qty = qty;
        this.boxId = boxId;
        this.warehouseId = warehouseId;
        this.aisle = aisle;
        this.bay = bay;
    }

    /**
     * Returns the order identifier.
     * @return order ID
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * Returns the order line number.
     * @return line number
     */
    public int getLineNo() {
        return lineNo;
    }

    /**
     * Returns the SKU identifier.
     * @return SKU
     */
    public String getSku() {
        return sku;
    }

    /**
     * Returns the allocated quantity.
     * @return quantity
     */
    public int getQty() {
        return qty;
    }

    /**
     * Returns the source box identifier.
     * @return box ID
     */
    public String getBoxId() {
        return boxId;
    }

    /**
     * Returns the warehouse identifier.
     * @return warehouse ID
     */
    public String getWarehouseId() {
        return warehouseId;
    }

    /**
     * Returns the warehouse identifier.
     * @return warehouse ID number
     */
    public int getWarehouseIdNumber() {
        int result = Integer.parseInt(warehouseId.replaceAll("[^0-9]", ""));
        return result;
    }

    /**
     * Returns the aisle number.
     * @return aisle
     */
    public int getAisle() {
        return aisle;
    }

    /**
     * Returns the bay number.
     * @return bay 
     */
    public int getBay() {
        return bay;
    }

    @Override
    public String toString() {
        return String.format("%s,%d,%s,%d,%s,%d,%d",
                orderId, lineNo, sku, qty, boxId, aisle, bay);
    }
}
