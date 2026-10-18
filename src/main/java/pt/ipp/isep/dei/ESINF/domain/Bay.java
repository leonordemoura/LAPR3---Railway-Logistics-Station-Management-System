package pt.ipp.isep.dei.ESINF.domain;

import pt.ipp.isep.dei.ESINF.services.impl.InventoryServiceImpl;

import java.util.*;

/**
 * Represents a storage bay within a warehouse aisle.
 */
public class Bay {
    private int bay;
    private int capacityBoxes;
    private Map<String, Box> boxes;

    /**
     * Constructs a new Bay with the specified warehouse, location, and capacity.
     * @param bay            the bay number within the aisle
     * @param capacityBoxes  the maximum box capacity
     */
    public Bay(int bay, int capacityBoxes) {
        this.bay = bay;
        this.capacityBoxes = capacityBoxes;
        this.boxes = new HashMap<>();
    }

    /**
     * Returns the bay number of this bay within its aisle.
     *
     * @return the bay number
     */
    public int getBay() { return bay; }

    /**
     * Returns the maximum number of boxes this bay can store.
     *
     * @return the capacity in boxes (&ge; 0)
     */
    public int getCapacityBoxes() { return capacityBoxes; }

    /**
     * Returns the map of boxes in this bay.
     *
     * @return the map of boxes keyed by box ID
     */
    public Map<String, Box> getBoxes() { return boxes; }

    /**
     * Returns the ordered list of boxes in this bay (FIFO/FEFO order).
     * Converts the Map to a sorted List.
     *
     * @return the ordered list of boxes
     */
    public List<Box> getOrderedBoxes() {
        List<Box> result = new ArrayList<>(boxes.values());
        // Sort by FIFO/FEFO order (this will be done by the calling code)
        return result;
    }

    /**
     * Adds a box to this bay.
     * @param box the box to add
     */
    public void addBox(Box box) {
        this.boxes.put(box.getBoxId(), box);
        // reconstruir LinkedHashMap na ordem FEFO/FIFO
        Map<String, Box> sorted = new LinkedHashMap<>();
        boxes.values().stream()
                .sorted(InventoryServiceImpl::compareBoxesForBayOrder)
                .forEach(b -> sorted.put(b.getBoxId(), b));
        boxes = sorted; // substitui
    }

    /**
     * Gets a box by its ID.
     * @param boxId the box ID
     * @return the box or null if not found
     */
    public Box getBox(String boxId) {
        return this.boxes.get(boxId);
    }

    /**
     * Removes a box from this bay.
     * @param boxId the box ID to remove
     * @return the removed box or null if not found
     */
    public Box removeBox(String boxId) {
        return this.boxes.remove(boxId);
    }

    /**
     * Checks if this bay has available capacity for more boxes.
     * @return true if current box count is less than capacity
     */
    public boolean hasAvailableCapacity() {
        return this.boxes.size() < this.capacityBoxes;
    }

    /**
     * Returns the current number of boxes in this bay.
     * @return current box count
     */
    public int getCurrentBoxCount() {
        return this.boxes.size();
    }
}
