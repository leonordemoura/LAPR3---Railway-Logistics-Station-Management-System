package pt.ipp.isep.dei.ESINF.controller;

import pt.ipp.isep.dei.ESINF.domain.*;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.PickingPlanService;
import pt.ipp.isep.dei.ESINF.services.impl.PickingPlanServiceImpl;

import java.util.*;

/**
 * Controller for USEI03 - Picking Plans functionality.
 * Handles the creation of picking plans using different packing heuristics.
 */
public class PickingPlanController {
    
    private final PickingPlanService pickingPlanService;
    private final WarehouseRepository warehouseRepository;
    private static List<Allocation> lastAllocations = new ArrayList<>();
    private static PickingPlan lastPickingPlan = null;

    public PickingPlanController() {
        this.pickingPlanService = new PickingPlanServiceImpl();
        this.warehouseRepository = Repositories.getInstance().getWarehouseRepository();

    }
    
    /**
     * Gets all items from the warehouse repository.
     * @return map of SKU to Item
     */
    public Map<String, Item> getAllItems() {
        return warehouseRepository.getItems();
    }
    
    /**
     * Sets the allocations from USEI02 result.
     * This method should be called by the OrderAllocationController after processing.
     * @param allocations the allocations from USEI02
     */
    public static void setAllocations(List<Allocation> allocations) {
        lastAllocations = new ArrayList<>(allocations);
    }
    
    /**
     * Gets all allocations from the last order allocation process.
     * This method should be called after USEI02 has been executed.
     * @return list of allocations
     */
    public List<Allocation> getAllocations() {
        return new ArrayList<>(lastAllocations);
    }
    
    /**
     * Creates a picking plan using the specified heuristic and parameters.
     * 
     * @param heuristic the packing heuristic to use
     * @param trolleyCapacity the capacity of each trolley in kg
     * @param overflowPolicy how to handle items that don't fit
     * @param allocations the allocation rows from USEI02
     * @param skuToItem map of SKU to Item for weight calculation
     * @return the created picking plan
     */
    public PickingPlan createPickingPlan(PickingPlanService.PackingHeuristic heuristic,
                                       double trolleyCapacity,
                                       PickingPlanService.OverflowPolicy overflowPolicy,
                                       List<Allocation> allocations,
                                       Map<String, Item> skuToItem) {
        PickingPlan plan = pickingPlanService.createPickingPlan(heuristic, trolleyCapacity, overflowPolicy, allocations, skuToItem);
        lastPickingPlan = plan;
        return plan;
    }
    
    /**
     * Gets the events from the last picking plan creation.
     * @return list of events
     */
    public List<String> getEvents() {
        return pickingPlanService.getEvents();
    }
    
    /**
     * Validates that allocations are available for picking plan creation.
     * @return true if allocations are available, false otherwise
     */
    public boolean hasAllocations() {
        List<Allocation> allocations = getAllocations();
        return allocations != null && !allocations.isEmpty();
    }
    
    /**
     * Gets a summary of available allocations.
     * @return summary string
     */
    public String getAllocationsSummary() {
        List<Allocation> allocations = getAllocations();
        if (allocations == null || allocations.isEmpty()) {
            return "No allocations available. Please run Order Allocation (USEI02) first.";
        }
        
        int totalAllocations = allocations.size();
        double totalWeight = calculateTotalWeight(allocations);
        
        return String.format("Available allocations: %d items, Total weight: %.2f kg", 
                           totalAllocations, totalWeight);
    }
    
    /**
     * Calculates the total weight of all allocations.
     * @param allocations list of allocations
     * @return total weight in kg
     */
    private double calculateTotalWeight(List<Allocation> allocations) {
        Map<String, Item> items = getAllItems();
        double totalWeight = 0.0;
        
        for (Allocation allocation : allocations) {
            Item item = items.get(allocation.getSku());
            if (item != null) {
                totalWeight += item.getUnitWeight() * allocation.getQty();
            }
        }
        
        return totalWeight;
    }

    public static PickingPlan getLastPickingPlan() {
        return lastPickingPlan;
    }
    
    
    /**
     * Clears the current state (useful for testing).
     */
    public static void clearState() {
        lastAllocations = new ArrayList<>();
        lastPickingPlan = null;
    }
}
