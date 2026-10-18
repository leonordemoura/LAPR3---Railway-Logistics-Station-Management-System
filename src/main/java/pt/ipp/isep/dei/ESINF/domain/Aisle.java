package pt.ipp.isep.dei.ESINF.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a warehouse aisle with bays organized by bay number.
 */
public class Aisle {
    private int aisleNumber;
    private String warehouseId;
    private Map<Integer, Bay> bays; // bay number -> Bay

    /**
     * Creates an aisle with empty bays.
     *
     * @param aisleNumber the aisle number
     * @param warehouseId the warehouse ID
     */
    public Aisle(int aisleNumber, String warehouseId) {
        this.aisleNumber = aisleNumber;
        this.warehouseId = warehouseId;
        this.bays = new HashMap<>();
    }

    /**
     * Returns the unique number identifying this aisle.
     *
     * @return the aisle number
     */
    public int getAisleNumber() {
        return aisleNumber;
    }

    /**
     * Returns the ID of the warehouse containing this aisle.
     *
     * @return the warehouse ID
     */
    public String getWarehouseId() {
        return warehouseId;
    }

    /**
     * Returns the map of bays in this aisle.
     *
     * @return the map of bays keyed by bay number
     */
    public Map<Integer, Bay> getBays() {
        return bays;
    }

    /**
     * Adds a bay to this aisle.
     * @param bay the bay to add
     */
    public void addBay(Bay bay) {
        this.bays.put(bay.getBay(), bay);
    }

    /**
     * Gets a bay by its number.
     * @param bayNumber the bay number
     * @return the bay or null if not found
     */
    public Bay getBay(int bayNumber) {
        return this.bays.get(bayNumber);
    }
}