package pt.ipp.isep.dei.ESINF.domain;

import pt.ipp.isep.dei.ESINF.domain.constants.Goods;
import java.util.Date;

/**
 * Represents a physical box received in the warehouse (from a wagon), which
 * may be perishable or not and which can be unloaded to a specific location.
 * Tracks current available quantity.
 */
public class Box {
    private String boxId;
    private String wagonId;
    private String sku;
    private int qty;
    private int qtyAvailable; // Para dispatch parcial
    private int qtyForAllocation;
    private Date expiryDate; // null = non-perishable
    private Date receivedAt;


    /**
     * Creates a new box.
     *
     * @param boxId      box identifier
     * @param wagonId    origin wagon identifier
     * @param sku        SKU inside the box
     * @param qty        initial quantity
     * @param expiryDate expiry (null for non-perishable)
     * @param receivedAt reception timestamp
     */
    public Box(String boxId, String wagonId, String sku, int qty, Date expiryDate, Date receivedAt) {
        this.boxId = boxId;
        this.wagonId = wagonId;
        this.sku = sku;
        this.qty = qty;
        this.qtyAvailable = qty;
        this.qtyForAllocation = qty;
        this.expiryDate = expiryDate;
        this.receivedAt = receivedAt;
    }

    // Getters
    /**
     * Returns the box identifier.
     * @return box ID
     */
    public String getBoxId() { return boxId; }

    /**
     * Returns the wagon identifier.
     * @return wagon ID
     */
    public String getWagonId() { return wagonId; }

    /**
     * Returns the SKU identifier.
     * @return SKU
     */
    public String getSku() { return sku; }

    /**
     * Returns the total quantity in box.
     * @return total quantity
     */
    public int getQty() { return qty; }

    /**
     * Returns the available quantity for picking.
     * @return available quantity
     */
    public int getQtyAvailable() { return qtyAvailable; }

    /**
     * Returns the quantity reserved for allocation.
     * @return allocation quantity
     */
    public int getQtyForAllocation() { return qtyForAllocation; }

    /**
     * Returns the expiry date (null for non-perishables).
     * @return expiry date or null
     */
    public Date getExpiryDate() { return expiryDate; }

    /**
     * Returns the receipt timestamp.
     * @return received at
     */
    public Date getReceivedAt() { return receivedAt; }


    //setters
    /**
     * Sets the box identifier.
     * @param boxId box ID
     */
    public void setBoxId(String boxId) { this.boxId = boxId; }

    /**
     * Sets the wagon identifier.
     * @param wagonId wagon ID
     */
    public void setWagonId(String wagonId) { this.wagonId = wagonId; }

    /**
     * Sets the SKU identifier.
     * @param sku SKU
     */
    public void setSku(String sku) { this.sku = sku; }

    /**
     * Sets the total quantity in box.
     * @param qty total quantity
     */
    public void setQty(int qty) { this.qty = qty; }

    /**
     * Sets the available quantity for picking.
     * @param qtyAvailable available quantity
     */
    public void setQtyAvailable(int qtyAvailable) { this.qtyAvailable = qtyAvailable; }

    /**
     * Sets the expiry date (null for non-perishables).
     * @param expiryDate expiry date or null
     */
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    /**
     * Sets the receipt timestamp.
     * @param receivedAt receipt timestamp
     */
    public void setReceivedAt(Date receivedAt) { this.receivedAt = receivedAt; }


    // Derived property
    /**
     * Returns the goods type based on expiry date.
     * @return PERISHABLE if expiryDate != null, NON_PERISHABLE otherwise
     */
    public Goods getType() {
        return expiryDate != null ? Goods.PERISHABLE : Goods.NON_PERISHABLE;
    }

    /**
     * Checks if goods are perishable.
     * @return true if expiryDate != null
     */
    public boolean isPerishable() {
        return expiryDate != null;
    }

    /**
     * Checks if goods are unloaded to warehouse.
     * @return true (boxes are always in a location in the hierarchical structure)
     */
    public boolean isUnloaded() {
        return true;
    }


    // For dispatch operation
    /**
     * Reduces available quantity by specified amount.
     *
     * @param amount amount to reduce
     * @throws IllegalArgumentException if amount > qtyAvailable
     */
    public void reduceQuantity(int amount) {
        if (amount > qtyAvailable) {
            throw new IllegalArgumentException("Cannot reduce more than available");
        }
        qtyAvailable -= amount;
        if (qtyForAllocation > 0) {
            qtyForAllocation = qtyAvailable;
        }
    }

    /**
     * Reduces available allocation quantity by the given amount.
     * Ensures quantity never goes below zero.
     *
     * @param amount amount to allocate
     * @return actual amount allocated
     */
    public int allocate(int amount) {
        int toAllocate = Math.min(amount, qtyForAllocation);
        qtyForAllocation -= toAllocate;
        return toAllocate;
    }

    /**
     * Reallocates the previously allocated item if the line is Undispatchale.
     * @param amount that will be reallocated
     */
    public void reallocate(int amount) {
        qtyForAllocation += amount;
    }

    /**
     * Checks if this box has any available quantity.
     *
     * @return true if qtyAvailable > 0
     */
    public boolean hasAvailableQty() {
        return qtyAvailable > 0;
    }

    /**
     * Checks if this box has already had an allocation.
     *
     * @return true if qtyAvailable != qtyForAllocation
     */
    public boolean hasAllocatedQty() {
        return qtyAvailable != qtyForAllocation;
    }

    /**
     * Checks if this box is empty.
     *
     * @return true if it is empty or false if it is not
     */
    public boolean isEmpty() {
        return qtyAvailable == 0;
    }
}