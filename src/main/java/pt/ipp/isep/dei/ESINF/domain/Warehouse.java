package pt.ipp.isep.dei.ESINF.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a warehouse containing multiple aisles organized by aisle number.
 */
public class Warehouse {
    private String warehouseId;
    private String name;
    //private String terminalId;
    private Map<Integer, Aisle> aisles; // aisle number -> Aisle

    /**
     * Creates a warehouse with specified attributes and aisles.
     *
     * @param warehouseId unique warehouse identifier
     * @param name        warehouse name
     * @param aisles      map of aisle numbers to aisles
     */
    public Warehouse(String warehouseId, String name, Map<Integer, Aisle> aisles) {
        this.warehouseId = warehouseId;
        this.name = name;
        //this.terminalId = terminalId;
        this.aisles = aisles != null ? new HashMap<>(aisles) : new HashMap<>();
    }

    /**
     * Returns the unique warehouse identifier.
     * @return warehouse ID
     */
    public String getWarehouseId() {
        return warehouseId;
    }

    /**
     * Returns the warehouse name.
     * @return warehouse name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns an unmodifiable map of aisles by aisle number.
     * @return aisles map
     */
    public Map<Integer, Aisle> getAisles() {
        return aisles;
    }

    /**
     * Adds an aisle to this warehouse.
     * @param aisle the aisle to add
     */
    public void addAisle(Aisle aisle) {
        this.aisles.put(aisle.getAisleNumber(), aisle);
    }

    /**
     * Gets an aisle by its number.
     * @param aisleNumber the aisle number
     * @return the aisle or null if not found
     */
    public Aisle getAisle(int aisleNumber) {
        return this.aisles.get(aisleNumber);
    }
}
