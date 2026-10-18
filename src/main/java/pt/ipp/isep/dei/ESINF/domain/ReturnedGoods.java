package pt.ipp.isep.dei.ESINF.domain;

import java.util.Date;

/**
 * Domain model representing a handled returned goods record.
 * Encapsulates identifying information, timestamps, product SKU, quantity and whether an action was taken.
 * Business logic related to returned goods should reside in service classes.
 */
public class ReturnedGoods {

    /**
     * Unique identifier for the return record.
     */
    private String returnId;

    /**
     * Timestamp when the return was processed or recorded.
     */
    private Date timestamp;

    /**
     * SKU (stock keeping unit) of the returned product.
     */
    private String sku;

    /**
     * Quantity of items returned.
     */
    private int qty;

    /**
     * Flag indicating whether an action (e.g., restock, discard) was taken for this returned goods record.
     */
    private boolean actionTaken;

    /**
     * Constructs a new ReturnedGoods instance with all relevant fields.
     *
     * @param returnId    unique identifier for the return record
     * @param timestamp   timestamp when the return was recorded or processed
     * @param sku         SKU of the returned product
     * @param qty         quantity returned
     * @param actionTaken whether an action was taken for this returned goods
     */
    public ReturnedGoods(String returnId, Date timestamp, String sku, int qty, boolean actionTaken) {
        this.returnId = returnId;
        this.timestamp = timestamp;
        this.sku = sku;
        this.qty = qty;
        this.actionTaken = actionTaken;
    }

    /**
     * Returns the unique identifier for this return record.
     *
     * @return returnId as a String
     */
    public String getReturnId() {
        return returnId;
    }

    /**
     * Returns the timestamp when the return was recorded or processed.
     *
     * @return timestamp as a Date
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the SKU of the returned product.
     *
     * @return sku as a String
     */
    public String getSku() {
        return sku;
    }

    /**
     * Returns the quantity of items returned.
     *
     * @return quantity as an int
     */
    public int getQty() {
        return qty;
    }

    /**
     * Indicates whether an action was taken for this returned goods record.
     *
     * @return true if an action was taken, false otherwise
     */
    public boolean getActionTaken() {
        return actionTaken;
    }
}