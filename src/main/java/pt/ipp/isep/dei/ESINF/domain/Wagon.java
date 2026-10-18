package pt.ipp.isep.dei.ESINF.domain;
import java.util.Date;

/**
 * Represents a wagon: a unit of inventory containing a quantity of a SKU from a specific box, with receipt and expiry information.
 */
public class Wagon {
    private String wagonId;
    private String boxId;
    private String sku;
    private int qty;
    private Date expiryDate;
    private Date receivedAt;

    /**
     * Creates a wagon record for inventory tracking.
     *
     * @param wagonId     unique wagon identifier
     * @param boxId       source box identifier
     * @param sku         SKU identifier
     * @param qty         quantity in wagon
     * @param expiryDate  expiry date
     * @param receivedAt  receipt timestamp
     */
    public Wagon(String wagonId, String boxId, String sku, int qty, Date expiryDate, Date receivedAt) {
        this.wagonId = wagonId;
        this.boxId = boxId;
        this.sku = sku;
        this.qty = qty;
        this.expiryDate = expiryDate; // may be null for non-perishable
        this.receivedAt = receivedAt;
    }

    /**
     * Returns the unique wagon identifier.
     * @return wagon ID (never null)
     */
    public String getWagonId() { return wagonId; }

    /**
     * Returns the source box identifier.
     * @return box ID (never null)
     */
    public String getBoxId() { return boxId; }

    /**
     * Returns the SKU identifier.
     * @return SKU (never null)
     */
    public String getSku() { return sku; }

    /**
     * Returns the quantity in this wagon.
     * @return quantity (&ge; 1)
     */
    public int getQty() { return qty; }

    /**
     * Returns the expiry date (null for non-perishables).
     * @return expiry date or null
     */
    public Date getExpiryDate() { return expiryDate; }

    /**
     * Returns the receipt timestamp.
     * @return received at (never null)
     */
    public Date getReceivedAt() { return receivedAt; }
}
