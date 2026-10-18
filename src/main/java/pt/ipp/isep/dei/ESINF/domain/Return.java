package pt.ipp.isep.dei.ESINF.domain;

import java.util.Date;

/**
 * Domain model representing a product return event.
 * Encapsulates identifying information, quantity, reason and relevant timestamps.
 * This class is a simple data holder; business logic for returns should reside in services.
 */
public class Return {

    /**
     * Unique identifier for this return record.
     */
    private String returnId;

    /**
     * SKU (stock keeping unit) of the returned product.
     */
    private String sku;

    /**
     * Quantity of items returned.
     */
    private int qty;

    /**
     * Reason provided for the return (e.g., damaged, expired, customer dissatisfaction).
     */
    private String reason;

    /**
     * Timestamp when the return was registered.
     */
    private Date timestamp;

    /**
     * Expiry date of the returned item, if applicable (can be null when not relevant).
     */
    private Date expiryDate;

    /**
     * Constructs a new Return instance with all relevant fields.
     *
     * @param returnId   unique identifier for the return record
     * @param sku        SKU of the returned product
     * @param qty        quantity returned
     * @param reason     reason for the return
     * @param timestamp  registration timestamp for the return
     * @param expiryDate expiry date of the item when applicable
     */
    public Return(String returnId, String sku, int qty, String reason, Date timestamp, Date expiryDate) {
        this.returnId = returnId;
        this.sku = sku;
        this.qty = qty;
        this.reason = reason;
        this.timestamp = timestamp;
        this.expiryDate = expiryDate;
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
     * Returns the quantity of items returned.
     *
     * @return quantity as an int
     */
    public int getQty() {
        return qty;
    }

    /**
     * Returns the declared reason for this return.
     *
     * @return reason as a String
     */
    public String getReason() {
        return reason;
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
     * Returns the timestamp when the return was recorded.
     *
     * @return timestamp as a Date
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the expiry date of the returned item when available.
     *
     * @return expiryDate as a Date, or null if not applicable
     */
    public Date getExpiryDate() {
        return expiryDate;
    }
}