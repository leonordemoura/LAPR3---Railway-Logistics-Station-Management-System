package pt.ipp.isep.dei.ESINF.domain;

import java.util.List;

/**
 * Represents a physical trolley (cart) used for picking items in the warehouse.
 * Each trolley has a weight capacity limit and contains a list of {@link PickingItem}s
 * assigned by the packing process.
 */
public class Trolley {
    private String trolleyId;
    private double capacity;
    private double usedWeight;
    private List<PickingItem> items;

    /**
     * Creates a new trolley.
     *
     * @param trolleyId   identifier for the trolley (e.g., "T1")
     * @param capacity    maximum capacity in kilograms
     * @param usedWeight  current used weight in kilograms
     * @param items       list of picking items assigned to this trolley
     */
    public Trolley(String trolleyId, double capacity, double usedWeight, List<PickingItem> items) {
        this.trolleyId = trolleyId;
        this.capacity = capacity;
        this.usedWeight = usedWeight;
        this.items = items;
    }

    /** @return the trolley identifier */
    public String getTrolleyId() { return trolleyId; }
    /** @return the maximum capacity in kilograms */
    public double getCapacity() { return capacity; }
    /** @return the used weight in kilograms */
    public double getUsedWeight() { return usedWeight; }
    /**
     * Updates the used weight of this trolley (in kilograms).
     * @param usedWeight new used weight
     */
    public void setUsedWeight(double usedWeight) { this.usedWeight = usedWeight; }
    /** @return the items assigned to this trolley */
    public List<PickingItem> getItems() { return items; }

    /**
     * Computes the utilization percentage of the trolley as used/capacity.
     * Returns 0.0 when capacity is zero.
     *
     * @return utilization percentage (0..100+)
     */
    public double getUtilization() {
        return capacity > 0 ? (usedWeight / capacity) * 100.0 : 0.0;
    }

    @Override
    public String toString() {
        return String.format("Trolley{id=%s, used=%.2f/%.2f (%.1f%%), items=%d}",
                trolleyId, usedWeight, capacity, getUtilization(), items.size());
    }
}


