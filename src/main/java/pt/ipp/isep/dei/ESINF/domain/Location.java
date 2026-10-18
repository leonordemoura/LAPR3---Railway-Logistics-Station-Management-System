package pt.ipp.isep.dei.ESINF.domain;

/**
 * Represents a precise location within a warehouse (warehouse + aisle + bay).
 */
public class Location {
    private String warehouseId;
    private int aisle;
    private int bay;

    /**
     * Creates a warehouse location.
     *
     * @param warehouseId warehouse identifier
     * @param aisle       aisle number
     * @param bay         bay number
     */
    public Location(String warehouseId, int aisle, int bay) {
        this.warehouseId = warehouseId;
        this.aisle = aisle;
        this.bay = bay;
    }

    /**
     * Returns the warehouse identifier.
     * @return warehouse ID
     */
    public String getWarehouseId()
    {
        return warehouseId;
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

    /**
     * Calculates distance between this location and another location
     *
     * @param other the other location
     * @return the distance between stations
     */
    public double distanceTo(Location other) {
        if (this.aisle == other.aisle) {
            return Math.abs(this.bay - other.bay);
        } else {
            return this.bay + Math.abs(this.aisle - other.aisle) * 3 + other.bay;
        }
    }

    @Override
    public String toString() {
        return "(" + aisle + ", " + bay + ")";
    }
}
